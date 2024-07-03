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
package com.braintribe.transport.messaging.api;

import com.braintribe.model.messaging.Message;

/**
 * <p>
 * A message consumer is capable of receiving {@link Message}(s) from a messaging destination.
 * 
 * <p>
 * The destination, from which messages are to be consumed, shall be defined during MessageConsumer's creation, through
 * {@link MessagingSession#createMessageConsumer(com.braintribe.model.messaging.Destination)}.
 * 
 * <p>
 * Messages can be received synchronously, through {@link #receive()}; or asynchronously, by registering a
 * {@link MessageListener} through {@link #setMessageListener(MessageListener)}.
 * 
 */
public interface MessageConsumer {

	/**
	 * <p>
	 * Gets the {@link MessageListener} registered to this consumer.
	 * 
	 * @return The {@link MessageListener} registered to this consumer
	 * @throws MessagingException
	 *             If the {@link MessageListener} retrieval fails
	 */
	MessageListener getMessageListener() throws MessagingException;

	/**
	 * <p>
	 * Registers a {@link MessageListener} to this consumer.
	 * 
	 * @param messageListener
	 *            The {@link MessageListener} to be registered
	 * @throws MessagingException
	 *             If the {@link MessageListener} registration fails
	 */
	void setMessageListener(MessageListener messageListener) throws MessagingException;

	/**
	 * <p>
	 * Receives the next {@link Message} produced for this message consumer.
	 * 
	 * <p>
	 * This method blocks until a message is produced or until this message consumer is closed.
	 * 
	 * <p>
	 * {@code null} is returned the message consumer is closed.
	 * 
	 * @return The next {@link Message} produced for this message consumer. Might be {@code null} is the consumer is
	 *         closed.
	 * @throws MessagingException
	 *             If the message consumption fails
	 */
	Message receive() throws MessagingException;

	/**
	 * <p>
	 * Receives the next {@link Message} produced for this message consumer.
	 * 
	 * <p>
	 * This method blocks until a message arrives, the timeout expires, or until this message consumer is closed.
	 * 
	 * <p>
	 * {@code null} is returned if no message arrives after the timeout expires, or if the message consumer if closed
	 * before the timeout expires.
	 * 
	 * <p>
	 * A timeout of zero never expires and the call blocks indefinitely.
	 * 
	 * @param timeout
	 *            The maximum amount of time (millisecond) this call will block waiting for a message.
	 * @return The next {@link Message} produced for this message consumer. Might be {@code null} is the consumer is
	 *         closed or if no message was received after the timeout was reached.
	 * @throws MessagingException
	 *             If the message consumption fails
	 */
	Message receive(long timeout) throws MessagingException;

	/**
	 * <p>
	 * Closes this message consumer.
	 * 
	 * @throws MessagingException
	 *             In case of failures while closing this consumer
	 */
	void close() throws MessagingException;

}
