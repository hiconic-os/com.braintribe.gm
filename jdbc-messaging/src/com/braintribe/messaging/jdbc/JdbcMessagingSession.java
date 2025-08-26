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

import com.braintribe.logging.Logger;
import com.braintribe.model.messaging.Destination;
import com.braintribe.model.messaging.Message;
import com.braintribe.model.messaging.Queue;
import com.braintribe.model.messaging.Topic;
import com.braintribe.transport.messaging.api.MessageConsumer;
import com.braintribe.transport.messaging.api.MessageProducer;
import com.braintribe.transport.messaging.api.MessagingException;
import com.braintribe.transport.messaging.api.MessagingSession;
import com.braintribe.utils.lcd.NullSafe;

/**
 * {@link MessagingSession} implementation for JDBC Messaging.
 * 
 * @see MessagingSession
 */
public class JdbcMessagingSession implements MessagingSession {

	public final JdbcMsgConnection connection;

	private static final Logger logger = Logger.getLogger(JdbcMessagingSession.class);

	public JdbcMessagingSession(JdbcMsgConnection connection) {
		this.connection = connection;
	}

	@Override
	public MessageProducer createMessageProducer() throws MessagingException {
		return createMessageProducer(null);
	}

	@Override
	public Queue createQueue(String name) throws MessagingException {
		validateDestinationName(name);

		logger.trace(() -> "Creating queue: " + name);
		Queue t = Queue.T.create();
		t.setName(name);

		return t;
	}

	@Override
	public Topic createTopic(String name) throws MessagingException {
		validateDestinationName(name);

		logger.trace(() -> "Creating topic: " + name);
		Topic t = Topic.T.create();
		t.setName(name);

		return t;
	}

	private void validateDestinationName(String name) throws MessagingException {
		if (name == null || name.trim().isEmpty()) {
			throw new MessagingException("Destination name [ " + name + " ] is not valid");
		}
	}

	@Override
	public Message createMessage() throws MessagingException {
		return Message.T.create();
	}

	@Override
	public MessageProducer createMessageProducer(Destination destination) throws MessagingException {
		logger.debug(() -> "Creating message producer for destination: " + destination + ", name: "
				+ (destination != null ? destination.getName() : "<null>"));

		JdbcMessageProducer producer = new JdbcMessageProducer(this, destination);

		connection.registerProducer(producer);

		return producer;
	}

	@Override
	public MessageConsumer createMessageConsumer(Destination destination) throws MessagingException {
		NullSafe.nonNull(destination, "destination");

		logger.debug(() -> "Creating message consumer for destination: " + destination);

		JdbcMessageConsumer consumer = new JdbcMessageConsumer(this, destination);
		
		connection.registerConsumer(consumer);

		return consumer;
	}

	@Override
	public void open() throws MessagingException {
		// nothing to do
	}

	// This was copied from Etcd and clearly doesn't make any sense.
	// This session creates producers/consumers but closing it doesn't dispose of them
	@Override
	public void close() throws MessagingException {
		// nothing to do; handled by connection
	}

	@Override
	public String toString() {
		if (connection == null) {
			return "etcd Messaging";
		} else {
			return "etcd Messaging (" + connection.toString() + ")";
		}
	}
}
