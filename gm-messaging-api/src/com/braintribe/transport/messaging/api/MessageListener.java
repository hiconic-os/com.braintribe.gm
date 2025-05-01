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
 * Message listeners can be registered to message consumer thus allowing non-blocking asynchronous consumption.
 * 
 * <p>
 * Whenever a new message is consumed, the listener is notified through its {@link #onMessage(Message)} method.
 * 
 * @see MessageConsumer
 * @see MessageConsumer#setMessageListener(MessageListener)
 */
@FunctionalInterface
public interface MessageListener {

	/**
	 * <p>
	 * Called when a message is received by the {@link MessageConsumer} to which this listener was registered (
	 * {@link MessageConsumer#setMessageListener(MessageListener)}).
	 * 
	 * @param message
	 *            The received message
	 * @throws MessagingException
	 *             Signals to the consumer a failure while processing the given message. {@link MessageConsumer}
	 *             implementations might take distinguished actions when this happens.
	 */
	void onMessage(Message message) throws MessagingException;

}
