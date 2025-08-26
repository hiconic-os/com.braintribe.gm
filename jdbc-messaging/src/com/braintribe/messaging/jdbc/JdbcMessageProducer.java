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

import java.util.Base64;
import java.util.Map;

import com.braintribe.logging.Logger;
import com.braintribe.model.messaging.Destination;
import com.braintribe.model.messaging.Message;
import com.braintribe.transport.messaging.api.MessageProducer;
import com.braintribe.transport.messaging.api.MessageProperties;
import com.braintribe.transport.messaging.api.MessagingComponentStatus;
import com.braintribe.transport.messaging.api.MessagingException;
import com.braintribe.utils.RandomTools;
import com.braintribe.utils.lcd.NullSafe;

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
			throw new UnsupportedOperationException("Cannot send message as no destination was assigned to the message producer at creation time");

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

		JdbcMessageEnvelope envelope = toEnvelope(message);
		connection.sendMessage(envelope, destination);

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

	private JdbcMessageEnvelope toEnvelope(Message message) {
		byte[] messageBody = messagingContext.marshallMessage(message);
		String encodedMessageBody = Base64.getEncoder().encodeToString(messageBody);

		Long expiration = message.getExpiration();

		JdbcMessageEnvelope envelope = new JdbcMessageEnvelope();
		envelope.body = encodedMessageBody;
		envelope.expiration = expiration != null ? expiration : System.currentTimeMillis() + timeToLive;

		Map<String, Object> props = message.getProperties();
		envelope.addresseeAppId = (String) props.get(MessageProperties.addreseeAppId.getName());
		envelope.addresseeNodeId = (String) props.get(MessageProperties.addreseeNodeId.getName());

		return envelope;
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
