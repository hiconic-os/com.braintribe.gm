// ============================================================================
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ============================================================================
package com.braintribe.model.processing.mqrpc.client;

import static com.braintribe.transport.messaging.api.MessageProperties.producerAppId;
import static com.braintribe.transport.messaging.api.MessageProperties.producerNodeId;
import static com.braintribe.transport.messaging.api.MessageProperties.producerSessionId;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.DestructionAware;
import com.braintribe.cfg.Required;
import com.braintribe.common.attribute.AttributeContext;
import com.braintribe.gm.model.reason.Reasons;
import com.braintribe.gm.model.reason.essential.Timeout;
import com.braintribe.logging.Logger;
import com.braintribe.model.generic.eval.IgnoreResponseAspect;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.messaging.Destination;
import com.braintribe.model.messaging.Message;
import com.braintribe.model.messaging.Queue;
import com.braintribe.model.messaging.Topic;
import com.braintribe.model.processing.rpc.commons.api.GmRpcException;
import com.braintribe.model.processing.rpc.commons.impl.client.AbstractRemoteServiceProcessor;
import com.braintribe.model.processing.rpc.commons.impl.client.GmRpcClientRequestContext;
import com.braintribe.model.processing.service.common.context.UserSessionAspect;
import com.braintribe.model.service.api.InstanceId;
import com.braintribe.model.service.api.ServiceRequest;
import com.braintribe.model.service.api.result.ResponseEnvelope;
import com.braintribe.model.service.api.result.ServiceResult;
import com.braintribe.model.service.api.result.StillProcessing;
import com.braintribe.model.service.api.result.Unsatisfied;
import com.braintribe.model.usersession.UserSession;
import com.braintribe.transport.messaging.api.MessageConsumer;
import com.braintribe.transport.messaging.api.MessageListener;
import com.braintribe.transport.messaging.api.MessageProducer;
import com.braintribe.transport.messaging.api.MessageProperties;
import com.braintribe.transport.messaging.api.MessagingException;
import com.braintribe.transport.messaging.api.MessagingSession;
import com.braintribe.transport.messaging.api.MessagingSessionProvider;
import com.braintribe.utils.lcd.LazyInitialization;
import com.braintribe.gm.model.reason.essential.InternalError;

public class GmMqRpcRemoteServiceProcessor extends AbstractRemoteServiceProcessor implements MessageListener, DestructionAware {
	private static final Logger log = Logger.getLogger(GmMqRpcRemoteServiceProcessor.class);

	private MessagingSessionProvider messagingSessionProvider;
	private String requestDestinationName;
	private EntityType<? extends Destination> requestDestinationType;
	private boolean ignoreResponses;
	private String responseTopicName;
	private long responseTimeout = 10000L;

	private volatile boolean stopProcessing = false;

	// post initialized
	private MessagingSession messagingSession;
	private MessageProducer requestProducer;
	private Destination requestDestination;
	private MessageConsumer responseConsumer;
	private Topic responseTopic;
	private final Map<String, BlockingQueue<Message>> responsesMap = new ConcurrentHashMap<>();
	private final LazyInitialization initialization = new LazyInitialization(this::initialize, this::close);

	// @formatter:off
	@Required public void setMessagingSessionProvider(MessagingSessionProvider messagingSessionProvider) { this.messagingSessionProvider = messagingSessionProvider; }
	@Required public void setRequestDestinationName(String requestDestinationName) { this.requestDestinationName = requestDestinationName; }
	@Required public void setRequestDestinationType(EntityType<? extends Destination> requestDestinationType) { this.requestDestinationType = requestDestinationType; }
	@Configurable public void setIgnoreResponses(boolean ignoreResponses) { this.ignoreResponses = ignoreResponses; }
	@Configurable public void setResponseTopicName(String responseTopicName) { this.responseTopicName = responseTopicName; }
	@Configurable public void setResponseTimeout(long responseTimeout) { this.responseTimeout = responseTimeout; }
	// @formatter:on

	@Override
	protected Logger logger() {
		return log;
	}

	@Override
	public void preDestroy() {
		initialization.shutDown();
	}

	private void initialize() {
		if (stopProcessing)
			throw new IllegalStateException("Client is closed");

		messagingSession = messagingSessionProvider.provideMessagingSession();
		requestDestination = determineDestination();
		requestProducer = messagingSession.createMessageProducer(requestDestination);
		responseTopic = messagingSession.createTopic(responseTopicName);
		responseConsumer = messagingSession.createMessageConsumer(responseTopic);
		responseConsumer.setMessageListener(this);
	}

	private Destination determineDestination() {
		if (Topic.T.isAssignableFrom(requestDestinationType))
			return messagingSession.createTopic(requestDestinationName);

		if (Queue.T.isAssignableFrom(requestDestinationType))
			return messagingSession.createQueue(requestDestinationName);

		throw new IllegalStateException("Destination type is not supported: " + requestDestinationType);
	}

	private void close() {
		stopProcessing = true;
		log.debug(() -> getClass().getSimpleName() + " from " + clientInstanceId + " is getting closed.");

		if (messagingSession != null) {
			try {
				messagingSession.close();
			} catch (Exception e) {
				log.error("Failed to close the messaging session", e);
			}
		}
	}
	
	@Override
	protected boolean skipSessionIdTransferToRequest() {
		return true;
	}

	@Override
	protected ServiceResult sendRequest(GmRpcClientRequestContext requestContext) {
		initialization.run();

		ServiceRequest request = requestContext.getServiceRequest();
		String correlationId = UUID.randomUUID().toString();
		boolean ignoreResponse = ignoreResponses || requestContext.getAttributeContext().findOrDefault(IgnoreResponseAspect.class, Boolean.FALSE);

		Message requestMessage = createRequestMessage(requestContext.getAttributeContext(), request, correlationId, ignoreResponse);

		try {
			BlockingQueue<Message> responseHolder = null;

			if (!ignoreResponse)
				responsesMap.put(correlationId, responseHolder = new LinkedBlockingQueue<>());

			requestProducer.sendMessage(requestMessage);
			log.trace(() -> "Application [" + clientInstanceId + "] sent message [" + correlationId + "] with: " + request);

			if (ignoreResponse)
				return ResponseEnvelope.T.create();

			while (true) {
				Message responseMessage = pollResponse(responseHolder);
				
				if (responseMessage == null) {
					// timed out
					return Unsatisfied.from(Reasons.build(Timeout.T).text("Timed out waiting for response").toMaybe());
				}

				if (correlationId.equals(responseMessage.getCorrelationId())) {
					Object body = responseMessage.getBody();
					
					if (!(body instanceof ServiceResult)) {
						String trackbackIdTail = " (tracebackId=" + UUID.randomUUID().toString() + ")";
						String reasonMsg = "Internal Error" + trackbackIdTail; 
						String logMsg = "Unexpected rpc message body " + trackbackIdTail + " " + body; 
						log.error(logMsg);
						return Unsatisfied.from(Reasons.build(InternalError.T).text(reasonMsg).toMaybe());
					}
					
					ServiceResult result = (ServiceResult) body;

					if (result instanceof StillProcessing) {
						continue;
					}

					InstanceId origin = requireOrigin(responseMessage);

					log.trace(() -> "Application [" + clientInstanceId + "] received a normal response for message [" + correlationId + "] from ["
							+ origin + "]: " + result);

					ResponseEnvelope responseEnvelope = result.asResponse();
					if (responseEnvelope != null)
						requestContext.notifyResponse(responseEnvelope.getResult());

					return result;
				}
			}

		} finally {
			if (!ignoreResponse)
				responsesMap.remove(correlationId);
		}
	}

	private Message pollResponse(BlockingQueue<Message> responseHolder) {
		try {
			return responseHolder.poll(responseTimeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			throw new GmRpcException("Client is closed", e);
		}
	}

	private Message createRequestMessage(AttributeContext attributeContext, ServiceRequest request, String correlationId, boolean ignoreResponse) {
		Message requestMessage = createMessageFromSession();
		requestMessage.setCorrelationId(correlationId);
		requestMessage.setDestination(requestDestination);
		requestMessage.setBody(request);
		if (!ignoreResponse)
			requestMessage.setReplyTo(responseTopic);

		Map<String, Object> properties = requestMessage.getProperties();
		properties.put(producerAppId.getName(), clientInstanceId.getApplicationId());
		properties.put(producerNodeId.getName(), clientInstanceId.getNodeId());
		
		attributeContext.findAttribute(UserSessionAspect.class) //
			.map(UserSession::getSessionId) //
			.ifPresent(sid -> properties.put(producerSessionId.getName(), sid));

		log.trace(() -> "Created message [" + correlationId + "] from [" + clientInstanceId + "] to deliver: " + request);

		return requestMessage;
	}

	private Message createMessageFromSession() {
		try {
			return messagingSession.createMessage();
		} catch (MessagingException e) {
			throw new IllegalStateException("Unable to create a message. Error: " + e.getMessage(), e);
		}
	}

	private InstanceId requireOrigin(Message message) {
		Map<String, Object> properties = message.getProperties();

		String applicationId = requireStringProperty(properties, MessageProperties.producerAppId.getName());
		String nodeId = requireStringProperty(properties, MessageProperties.producerNodeId.getName());

		InstanceId origin = InstanceId.T.create();
		origin.setApplicationId(applicationId);
		origin.setNodeId(nodeId);

		return origin;
	}

	private static String requireStringProperty(Map<String, Object> properties, String propertyName) {
		Object value = properties.get(propertyName);
		String strValue;
		if (value == null || (strValue = value.toString().trim()).isEmpty())
			throw new IllegalStateException("Response message is missing the [ " + propertyName + " ] property.");

		return strValue;
	}

	@Override
	public void onMessage(Message responseMessage) throws MessagingException {
		String correlationId = responseMessage.getCorrelationId();
		if (correlationId == null) {
			log.warn(() -> "Cannot process response for request [" + correlationId + "] as message has no correlation id");
			return;
		}

		if (stopProcessing) {
			log.debug(() -> "Ignoring response for request [" + correlationId + "]. The client is closing.");
			return;
		}

		BlockingQueue<Message> blockingQueue = responsesMap.get(correlationId);
		if (blockingQueue == null) {
			log.debug(() -> "Ignoring response for request [" + correlationId + "]. The instance [" + clientInstanceId
					+ "] is no longer waiting for responses for this request.");
			return;
		}

		try {
			blockingQueue.add(responseMessage);
			log.debug(() -> "Registered response for request [" + correlationId + "]: " + responseMessage);

		} catch (Exception e) {
			log.error("Failed to register response to request  [" + correlationId + "]", e);
		}
	}

}
