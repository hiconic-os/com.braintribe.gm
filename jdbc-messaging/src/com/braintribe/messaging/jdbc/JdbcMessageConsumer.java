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

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.braintribe.logging.Logger;
import com.braintribe.model.messaging.Destination;
import com.braintribe.model.messaging.Message;
import com.braintribe.transport.messaging.api.MessageConsumer;
import com.braintribe.transport.messaging.api.MessageListener;
import com.braintribe.transport.messaging.api.MessagingComponentStatus;
import com.braintribe.transport.messaging.api.MessagingException;

/**
 * {@link MessageConsumer} implementation for JDBC Messaging.
 * 
 * @see MessageConsumer
 */
public class JdbcMessageConsumer extends JdbcAbstractMessageHandler implements MessageConsumer {

	private static final Logger logger = Logger.getLogger(JdbcMessageConsumer.class);

	private final int maxQueueSize = 1000;

	private MessageListener messageListener;
	private MessagingComponentStatus status = MessagingComponentStatus.NEW;

	private final LinkedBlockingQueue<Message> messageQueue = new LinkedBlockingQueue<>();

	public JdbcMessageConsumer(JdbcMessagingSession session, Destination destination) {
		super(session, destination);
	}

	@Override
	public MessageListener getMessageListener() throws MessagingException {
		return messageListener;
	}

	@Override
	public void setMessageListener(MessageListener messageListener) throws MessagingException {
		this.messageListener = messageListener;
	}

	@Override
	public Message receive() throws MessagingException {
		return receive(0);
	}

	@Override
	public Message receive(long timeout) throws MessagingException {
		Message message;
		try {
			if (timeout > 0) {
				message = messageQueue.poll(timeout, TimeUnit.MILLISECONDS);
			} else {
				message = messageQueue.take();
			}
		} catch (InterruptedException e) {
			logger.trace(() -> "Got interrupted.");
			return null;
		}
		if (message != null && logger.isDebugEnabled()) {
			logger.debug("Received etcd message: " + message + " with body " + message.getBody());
		}
		return message;
	}

	@Override
	public void close() throws MessagingException {
		if (status == MessagingComponentStatus.CLOSED) {
			logger.debug(() -> "Producer is already closed");
			return;
		}

		connection.unregisterConsumer(this);

		status = MessagingComponentStatus.CLOSED;
	}

	@SuppressWarnings("unused")
	public void receivedMessage(String destination, Message message) {
		if (messageListener != null) {
			messageListener.onMessage(message);
		} else {
			while ((messageQueue.size() + 1) > maxQueueSize) {
				try {
					messageQueue.remove();
				} catch (Exception e) {
					// ignore
					break;
				}
			}
			messageQueue.add(message);
		}
	}

}
