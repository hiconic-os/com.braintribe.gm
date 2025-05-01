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

import com.braintribe.transport.messaging.impl.StandardMessagingSessionProvider;

/**
 * <p>
 * Connection to a message broker.
 * 
 */
public interface MessagingConnection {

	/**
	 * WTF - This is never called from the outside, there is no point in having this method exposed here.
	 * 
	 * <p>
	 * Opens (or starts) the messaging connection.
	 * 
	 * <p>
	 * Opening an already opened connection has no effect.
	 * 
	 * <p>
	 * Attempting to open a connection already closed throws a {@link MessagingException}.
	 * 
	 * <p>
	 * Opening a connection explicitly with this method is not mandatory, as implementations must ensure that the connection is opened on
	 * {@link #createMessagingSession()} calls.
	 * 
	 * @throws MessagingException
	 *             If the provider fails to open a connection to the underlying message broker, or if {@link #close()} was already called for this
	 *             connection.
	 */
	void open() throws MessagingException;

	/**
	 * <p>
	 * Closes the messaging connection, ensuring that the {@link MessagingSession}(s), {@link MessageProducer}(s) and {@link MessageConsumer}(s)
	 * created through it are also closed.
	 * 
	 * <p>
	 * Closing an already closed connection has no effect.
	 * 
	 * <p>
	 * Once closed, a connection cannot be reopened, thus after calling this method on a connection, subsequent calls to {@link #open()} must fail.
	 * 
	 * @throws MessagingException
	 *             In case of failures while closing this connection
	 */
	void close() throws MessagingException;

	/**
	 * This is only called once from {@link StandardMessagingSessionProvider#provideMessagingSession}, and is thus absolutely pointless. Especially
	 * when the implementations handle cases where this method is called multiple time, even though it isn't.
	 * <p>
	 * This is a pointless layer that explicitly represents the {@link MessagingSession} lifecycle.
	 * 
	 * <p>
	 * Creates a {@link MessagingSession}.
	 * 
	 * <p>
	 * This method throws a {@link MessagingException} if {@link #close()} was already called for this connection.
	 * 
	 * <p>
	 * If {@link #open()} was not explicitly called on this connection, implementations must ensure that calling this method will open the connection.
	 * 
	 * @return A {@link MessagingSession}
	 * 
	 * @throws MessagingException
	 *             If the provider fails to open a session to the underlying message broker, or if {@link #close()} was already called for this
	 *             connection.
	 */
	MessagingSession createMessagingSession() throws MessagingException;

}
