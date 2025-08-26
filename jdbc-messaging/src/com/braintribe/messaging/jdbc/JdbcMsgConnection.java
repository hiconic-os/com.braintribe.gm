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

import static com.braintribe.utils.lcd.CollectionTools2.newConcurrentMap;
import static com.braintribe.utils.lcd.CollectionTools2.newConcurrentSet;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import javax.sql.DataSource;

import com.braintribe.logging.Logger;
import com.braintribe.model.messaging.Destination;
import com.braintribe.model.messaging.Topic;
import com.braintribe.transport.messaging.api.MessagingComponentStatus;
import com.braintribe.transport.messaging.api.MessagingConnection;
import com.braintribe.transport.messaging.api.MessagingContext;
import com.braintribe.transport.messaging.api.MessagingException;
import com.braintribe.transport.messaging.api.MessagingSession;

/**
 * {@link MessagingConnection} implementation representing a connection to a Pub/Sub server.
 */
public class JdbcMsgConnection implements MessagingConnection {

	private static final Logger logger = Logger.getLogger(JdbcMsgConnection.class);

	public static final String ETCD_CLIENT_CERTIFICATE = "ETCD_CLIENT_CERTIFICATE";

	public final MessagingContext messagingContext;

	private MessagingComponentStatus status = MessagingComponentStatus.NEW;

	private final Set<JdbcMessagingSession> sessions = new HashSet<>();
	private final ReentrantLock sessionsLock = new ReentrantLock();

	private final ReentrantLock lifeCycleLock = new ReentrantLock();

	private final Set<JdbcMessageProducer> messageProducers = newConcurrentSet();
	private final Set<JdbcMessageConsumer> messageConsumers = newConcurrentSet();

	private final JdbcMsgGmDb db;

	private final Map<String, Set<JdbcMessageConsumer>> topicConsumers = newConcurrentMap();
	private final Map<String, Set<JdbcMessageConsumer>> queueConsumers = newConcurrentMap();

	public JdbcMsgConnection(String sqlPrefix, DataSource dataSource, MessagingContext messagingContext) {
		this.messagingContext = messagingContext;

		this.db = new JdbcMsgGmDb(dataSource, sqlPrefix, messagingContext, this);
	}

	@Override
	public void open() throws MessagingException {
		// NO OP
	}

	@Override
	public MessagingSession createMessagingSession() throws MessagingException {
		init();

		JdbcMessagingSession session = new JdbcMessagingSession(this);

		sessionsLock.lock();
		try {
			sessions.add(session);
		} finally {
			sessionsLock.unlock();
		}
		return session;
	}

	private void init() throws MessagingException {
		if (status == MessagingComponentStatus.OPEN)
			return;

		lifeCycleLock.lock();
		try {
			lc_init();
		} finally {
			lifeCycleLock.unlock();
		}
	}

	private void lc_init() throws MessagingException {
		if (status == MessagingComponentStatus.OPEN)
			return;

		if (status == MessagingComponentStatus.CLOSING || status == MessagingComponentStatus.CLOSED)
			throw new MessagingException("Cannot init connection, it is already in state: " + status.toString());

		db.ensureTablesAndTriggers();

		status = MessagingComponentStatus.OPEN;
	}

	@Override
	public void close() throws MessagingException {
		if (status == MessagingComponentStatus.CLOSED)
			return;

		lifeCycleLock.lock();
		try {
			lc_close();
		} finally {
			lifeCycleLock.unlock();
		}
	}

	private void lc_close() {
		if (status == MessagingComponentStatus.CLOSED)
			return;

		status = MessagingComponentStatus.CLOSING;

		closeMessageHandlers("producer", messageProducers);
		closeMessageHandlers("consumer", messageConsumers);

		db.close();

		status = MessagingComponentStatus.CLOSED;
	}

	private void closeMessageHandlers(String producerOrConsumer, Set<? extends JdbcAbstractMessageHandler> handlers) {
		Iterator<? extends JdbcAbstractMessageHandler> it = handlers.iterator();
		while (it.hasNext()) {
			JdbcAbstractMessageHandler handler = it.next();
			it.remove();
			try {
				handler.close();
			} catch (Exception e) {
				logger.debug(() -> "Error while closing message " + producerOrConsumer + ": " + handler, e);
			}
		}
	}

	/* package */ void registerConsumer(JdbcMessageConsumer consumer) {
		db.ensureMsgListenerThreadsRunning();

		messageConsumers.add(consumer);
		subscribe(consumer);
	}

	private void subscribe(JdbcMessageConsumer consumer) {
		Destination destination = consumer.destination;

		Map<String, Set<JdbcMessageConsumer>> consumers = getConsumersMap(destination);

		Set<JdbcMessageConsumer> set = consumers.computeIfAbsent(destination.getName(), d -> newConcurrentSet());
		set.add(consumer);
	}

	/* package */ void unregisterConsumer(JdbcMessageConsumer messageConsumer) {
		messageConsumers.remove(messageConsumer);
		unsubscribe(messageConsumer);
	}

	private void unsubscribe(JdbcMessageConsumer consumer) {
		Destination destination = consumer.destination;

		Set<JdbcMessageConsumer> set = getConsumersMap(destination).get(destination.getName());
		if (set != null)
			set.remove(consumer);
	}

	/* package */ void registerProducer(JdbcMessageProducer producer) {
		messageProducers.add(producer);
	}

	/* package */ void unregisterProducer(JdbcMessageProducer messageProducer) {
		messageProducers.remove(messageProducer);
	}

	private Map<String, Set<JdbcMessageConsumer>> getConsumersMap(Destination destination) {
		return getConsumersMap(destination instanceof Topic);
	}

	/* package */ Map<String, Set<JdbcMessageConsumer>> getConsumersMap(boolean isTopic) {
		return isTopic ? topicConsumers : queueConsumers;
	}

	public void sendMessage(JdbcMessageEnvelope envelope, Destination destination) {
		db.sendMessage(envelope, destination);
	}

	/** @see JdbcConnectionProvider#deleteExpiredMessages() */
	public int deleteExpiredMessages() {
		return db.deleteExpiredMessages();
	}

}
