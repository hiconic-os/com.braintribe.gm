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
package com.braintribe.messaging.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;
import java.util.Map;

import com.braintribe.logging.Logger;
import com.braintribe.model.messaging.Destination;
import com.braintribe.model.messaging.Message;
import com.braintribe.model.resource.Resource;
import com.braintribe.transport.messaging.api.MessageProducer;
import com.braintribe.transport.messaging.api.MessageProperties;
import com.braintribe.transport.messaging.api.MessagingComponentStatus;
import com.braintribe.transport.messaging.api.MessagingException;
import com.braintribe.utils.IOTools;
import com.braintribe.utils.RandomTools;
import com.braintribe.utils.lcd.NullSafe;
import com.braintribe.utils.stream.api.StreamPipe;

/**
 * {@link MessageProducer} implementation for JDBC Messaging.
 * 
 * @see MessageProducer
 */
public class JdbcMessageProducer extends JdbcAbstractMessageHandler implements MessageProducer {

	private static final Long defaultTimeToLive = 60_000L; // ONE MINUTE
	private static final Integer defaultPriority = 4;

	private Long timeToLive = defaultTimeToLive;
	private Integer priority = defaultPriority;

	private MessagingComponentStatus status = MessagingComponentStatus.OPEN;

	private static final Logger log = Logger.getLogger(JdbcMessageProducer.class);

	public JdbcMessageProducer(JdbcMessagingSession session, Destination destination) {
		super(session, destination);
	}

	@Override
	public Destination getDestination() {
		return destination;
	}

	@Override
	public void sendMessage(Message message) throws MessagingException {
		if (destination == null)
			throw new UnsupportedOperationException("Cannot send message as no default destination was assigned to the producer.");

		sendMessage(message, destination);
	}

	@Override
	public void sendMessage(Message message, Destination destination) throws MessagingException {
		NullSafe.nonNull(message, "message");
		NullSafe.nonNull(destination, "destination");

		enrichMessage(message);

		if (log.isTraceEnabled())
			log.trace("Publishing message to " + destination.getName() + ": " + message + " with message ID " + message.getMessageId()
					+ ", correlation ID: " + message.getCorrelationId() + ", and body " + message.getBody());

		// The pipe buffers the marshalled body, spilling to disk if needed, so a big message never sits in memory as a whole.
		try (StreamPipe bodyPipe = connection.pipeFactory.newPipe("jdbc-message-body")) {
			JdbcMessageEnvelope envelope = toEnvelope(message, bodyPipe);
			connection.sendMessage(envelope, destination);
		}

		if (log.isTraceEnabled())
			log.trace("Published message to " + destination.getName() + ": " + message + " with message ID " + message.getMessageId()
					+ " and correlation ID: " + message.getCorrelationId());
	}

	private void enrichMessage(Message message) {
		message.setMessageId(RandomTools.newStandardUuid());

		message.setDestination(null);

		if (message.getPriority() == null)
			message.setPriority(priority);
		else
			message.setPriority(normalizePriority(message.getPriority()));

		if (message.getTimeToLive() == null)
			message.setTimeToLive(timeToLive);

		if (message.getTimeToLive() > 0L)
			message.setExpiration(System.currentTimeMillis() + message.getTimeToLive());
		else
			message.setExpiration(0L);

		messagingContext.enrichOutbound(message);
	}

	private JdbcMessageEnvelope toEnvelope(Message message, StreamPipe bodyPipe) {
		JdbcMessageEnvelope envelope = new JdbcMessageEnvelope();

		marshalBody(message, bodyPipe, envelope);

		Long expiration = message.getExpiration();
		envelope.expiration = expiration != null ? expiration : System.currentTimeMillis() + timeToLive;

		Map<String, Object> props = message.getProperties();
		envelope.addresseeAppId = (String) props.get(MessageProperties.addreseeAppId.getName());
		envelope.addresseeNodeId = (String) props.get(MessageProperties.addreseeNodeId.getName());

		return envelope;
	}

	/** Marshals the message into given pipe and then decides, based on its size, which of the two body columns it goes to. */
	private void marshalBody(Message message, StreamPipe bodyPipe, JdbcMessageEnvelope envelope) {
		try (OutputStream out = bodyPipe.openOutputStream()) {
			connection.marshaller.marshall(out, message);

		} catch (IOException e) {
			throw new MessagingException("Error while marshalling message with id " + message.getMessageId(), e);
		}

		long size = bodyPipe.bytesWritten();

		if (base64Length(size) <= JdbcMessageEnvelope.INLINE_BODY_CHARS_LIMIT)
			envelope.bodyInline = toInlineBody(bodyPipe);
		else
			envelope.bodyBlob = toBlobBody(bodyPipe, size);
	}

	private static long base64Length(long bytes) {
		return 4 * ((bytes + 2) / 3);
	}

	
	/** Small by definition - see {@link JdbcMessageEnvelope#INLINE_BODY_CHARS_LIMIT} - so holding it in memory is fine. */
	private String toInlineBody(StreamPipe bodyPipe) {
		try (InputStream in = bodyPipe.openInputStream()) {
			return Base64.getEncoder().encodeToString(IOTools.slurpBytes(in));

		} catch (IOException e) {
			throw new MessagingException("Error while reading the marshalled message body", e);
		}
	}

	private Resource toBlobBody(StreamPipe bodyPipe, long size) {
		Resource result = Resource.createTransient(bodyPipe::openInputStream);
		result.setName("message-body");
		// Anything but text/plain, as that would make the ResourceColumn consider storing the body as a String
		result.setMimeType("application/octet-stream");
		result.setFileSize(size);

		return result;
	}

	@Override
	public void close() throws MessagingException {
		if (status == MessagingComponentStatus.CLOSED) {
			log.debug(() -> "Producer is already closed");
			return;
		}

		connection.unregisterProducer(this);

		status = MessagingComponentStatus.CLOSED;
	}

	@Override
	public Long getTimeToLive() {
		return timeToLive;
	}

	@Override
	public void setTimeToLive(Long timeToLive) {
		if (timeToLive == null)
			this.timeToLive = defaultTimeToLive;
		else
			this.timeToLive = timeToLive;
	}

	@Override
	public Integer getPriority() {
		return priority;
	}

	@Override
	public void setPriority(Integer priority) {
		if (priority == null)
			this.priority = defaultPriority;
		else
			this.priority = normalizePriority(priority);
	}

	private Integer normalizePriority(Integer priorityCandidate) {
		if (priorityCandidate == null)
			return null;
		if (priorityCandidate < 0)
			return 0;
		if (priorityCandidate > 9)
			return 9;

		return priorityCandidate;
	}

}
