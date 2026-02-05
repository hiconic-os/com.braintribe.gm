// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
//
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
package com.braintribe.transport.messaging.bq;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import com.braintribe.logging.Logger;
import com.braintribe.model.messaging.Destination;
import com.braintribe.model.messaging.Message;
import com.braintribe.model.messaging.Queue;
import com.braintribe.model.messaging.Topic;
import com.braintribe.transport.messaging.api.MessageConsumer;
import com.braintribe.transport.messaging.api.MessageProducer;
import com.braintribe.transport.messaging.api.MessagingComponentStatus;
import com.braintribe.transport.messaging.api.MessagingContext;
import com.braintribe.transport.messaging.api.MessagingException;
import com.braintribe.transport.messaging.api.MessagingSession;

/**
 * @see MessagingSession
 */
public class BqMessagingSession implements MessagingSession {

	private BqMessagingConnection connection;
	private MessagingContext messagingContext;

	private Set<MessageConsumer> consumers = new HashSet<>();
	private Set<MessageProducer> producers = new HashSet<>();

	private final ReentrantLock connectionLock = new ReentrantLock();
	private final long connectionLockTimeout = 2L;
	private final TimeUnit connectionLockTimeoutUnit = TimeUnit.SECONDS;
	private volatile MessagingComponentStatus status = MessagingComponentStatus.NEW;

	private static final Logger log = Logger.getLogger(BqMessagingSession.class);

	public BqMessagingConnection getConnection() {
		return connection;
	}

	public void setConnection(BqMessagingConnection connection, MessagingContext messagingContext) {
		this.connection = connection;
		this.messagingContext = messagingContext;
	}

	public MessagingContext getMessagingContext() {
		return messagingContext;
	}

	@Override
	public MessageProducer createMessageProducer() throws MessagingException {
		return createMessageProducer(null);
	}

	@Override
	public MessageProducer createMessageProducer(Destination destination) throws MessagingException {
		if (destination != null) {
			validateDestination(destination);
		}

		assertOpen();

		BqMessageProducer producer = new BqMessageProducer();
		producer.setDestination(destination);
		producer.setSession(this);
		producer.setApplicationId(messagingContext.getApplicationId());
		producer.setNodeId(messagingContext.getNodeId());

		producers.add(producer);

		return producer;
	}

	@Override
	public MessageConsumer createMessageConsumer(Destination destination) throws MessagingException {

		validateDestination(destination);

		assertOpen();

		BqMessageConsumer consumer = new BqMessageConsumer(UUID.randomUUID().toString());

		consumer.setDestination(destination);
		consumer.setSession(this);
		consumer.setApplicationId(messagingContext.getApplicationId());
		consumer.setNodeId(messagingContext.getNodeId());

		if (destination instanceof Topic) {
			getConnection().getBqMessaging().subscribeTopicConsumer(destination.getName(), consumer.getConsumerId());
			log.trace(() -> "Registered topic consumer [ " + consumer + " ] to the BqMessaging");
		}

		consumers.add(consumer);

		log.debug(() -> "Opened consumer [ " + consumer + " ]");

		return consumer;
	}

	@Override
	public void open() {
		try {
			tryLockConnectionLock("open");
			try {
				if (status == MessagingComponentStatus.CLOSING || status == MessagingComponentStatus.CLOSED) {
					throw new MessagingException("Messaging session in unexpected state: " + status.toString().toLowerCase());
				}

				// asserts that the connection is opened
				getConnection().assertOpen();

				if (status == MessagingComponentStatus.OPEN) {
					// opening an already opened connection shall be a no-op
					log.debug(() -> "Messaging session already opened.");
					return;
				}

				this.status = MessagingComponentStatus.OPEN;

			} finally {
				connectionLock.unlock();
			}

		} catch (InterruptedException e) {
			throwConnectionLockInterrupted(e, "open");
		}

	}

	@Override
	public void close() {
		try {
			tryLockConnectionLock("close");
			try {

				if (status == MessagingComponentStatus.CLOSING || status == MessagingComponentStatus.CLOSED) {
					// closing an already closed connection shall be a no-op
					log.debug(() -> "No-op close() call. Messaging session closing already requested. current state: "
							+ status.toString().toLowerCase());
					return;
				}

				this.status = MessagingComponentStatus.CLOSING;

				closeConsumers();

				this.consumers = null;
				this.producers = null;

				log.debug(() -> "Messaging session closed.");

				this.status = MessagingComponentStatus.CLOSED;

			} catch (Throwable t) {
				log.error(t);

			} finally {
				connectionLock.unlock();
			}

		} catch (InterruptedException e) {
			throwConnectionLockInterrupted(e, "close");
		}

	}

	private void closeConsumers() {
		for (MessageConsumer consumer : consumers) {
			try {
				consumer.close();
			} catch (Throwable t) {
				log.error("Failed to close consumer created by this messaging session: " + consumer + ": " + t.getMessage(), t);
			}
		}
	}

	private void tryLockConnectionLock(String openOrClose) throws InterruptedException {
		if (!connectionLock.tryLock(connectionLockTimeout, connectionLockTimeoutUnit))
			throw new MessagingException("Failed to "+openOrClose+" the messaging connection. Unable to acquire lock after " + connectionLockTimeout + " "
					+ connectionLockTimeoutUnit.toString().toLowerCase());
	}

	private void throwConnectionLockInterrupted(InterruptedException e, String closeOrOpen) {
		throw new MessagingException("Failed to " +closeOrOpen+" the messaging connection. Unable to acquire lock after " + connectionLockTimeout + " "
				+ connectionLockTimeoutUnit.toString().toLowerCase() + " : " + e.getMessage(), e);
	}

	@Override
	public Queue createQueue(String name) throws MessagingException {
		validateDestinationName(name);

		assertOpen();

		Queue queue = Queue.T.create();
		queue.setName(name);

		return queue;
	}

	@Override
	public Topic createTopic(String name) throws MessagingException {
		validateDestinationName(name);

		assertOpen();

		Topic topic = Topic.T.create();
		topic.setName(name);

		return topic;
	}

	@Override
	public Message createMessage() throws MessagingException {
		return Message.T.create();
	}

	/**
	 * <p>
	 * Common validations over {@link Destination}(s) names.
	 * 
	 * @param name
	 *            The name to be validated
	 * @throws MessagingException
	 *             If the given name is invalid
	 */
	protected void validateDestinationName(String name) throws MessagingException {
		if (name == null || name.trim().isEmpty()) {
			throw new MessagingException("Destination name [ " + name + " ] is not valid");
		}
	}

	/**
	 * Common validations over {@link Destination}(s).
	 * 
	 * @param destination
	 *            The destination to be validated
	 * @throws IllegalArgumentException
	 *             If the given destination is {@code null}
	 * @throws UnsupportedOperationException
	 *             If the type of the given destination instance is not supported
	 */
	protected void validateDestination(Destination destination) {
		if (destination == null) {
			throw new IllegalArgumentException("destination cannot be null");
		}

		if (!(destination instanceof Topic || destination instanceof Queue)) {
			throw new UnsupportedOperationException("Unsupported destination type: " + destination);
		}
	}

	/**
	 * <p>
	 * Asserts that this messaging session is in a valid state to be used: Already open. not "closing" nor "closed";
	 * 
	 * <p>
	 * This method does not try to open the messaging session.
	 * 
	 * @throws MessagingException
	 *             If this connection is NOT in a valid state to be used
	 */
	protected void assertOpen() throws MessagingException {
		if (status != MessagingComponentStatus.OPEN)
			throw new MessagingException("Messaging session is not opened. Current state: " + status.toString().toLowerCase());
	}

	@Override
	public String toString() {
		return "Blocking Queue Messaging";
	}
}
