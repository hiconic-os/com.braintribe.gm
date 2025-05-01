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
package com.braintribe.transport.messaging.impl;

import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.LifecycleAware;
import com.braintribe.cfg.Required;
import com.braintribe.logging.Logger;
import com.braintribe.transport.messaging.api.MessagingConnection;
import com.braintribe.transport.messaging.api.MessagingConnectionProvider;
import com.braintribe.transport.messaging.api.MessagingException;
import com.braintribe.transport.messaging.api.MessagingSession;
import com.braintribe.transport.messaging.api.MessagingSessionProvider;

/**
 * Natural implementation of {@link MessagingSessionProvider}.
 * <p>
 * This component establishes and manages {@link MessagingConnection}(s) to the platform message broker, providing
 * {@link MessagingSession}(s).
 * 
 */
public class StandardMessagingSessionProvider implements MessagingSessionProvider, LifecycleAware {

	// constants
	private static final Logger log = Logger.getLogger(StandardMessagingSessionProvider.class);

	// configurable
	private MessagingConnectionProvider<?> messagingConnectionProvider;
	private boolean lazyInitialization = true;

	// cached
	private volatile MessagingConnection messagingConnection;

	public StandardMessagingSessionProvider() {
	}

	@Required
	@Configurable
	public void setMessagingConnectionProvider(MessagingConnectionProvider<?> messagingConnectionProvider) {
		this.messagingConnectionProvider = messagingConnectionProvider;
	}

	@Configurable
	public void setLazyInitialization(boolean lazyInitialization) {
		this.lazyInitialization = lazyInitialization;
	}

	@Override
	public void postConstruct() {
		ensureConnection(true);
	}

	@Override
	public void preDestroy() {
		close();
	}

	@Override
	public MessagingSession provideMessagingSession() throws MessagingException {
		ensureConnection(false);

		return messagingConnection.createMessagingSession();
	}

	@Override
	public void close() {
		closeConnection();
	}

	private void closeConnection() {
		MessagingConnection connection = messagingConnection;

		if (connection == null)
			return;

		try {
			connection.close();

			log.debug(() -> "Closed messaging connection: " + connection);

		} catch (Exception e) {
			log.error("Failed to close messaging connection", e);
		}
	}

	private void ensureConnection(boolean startup) {
		if (messagingConnection == null && startup ^ lazyInitialization) {
			initializeConnection();
		}
	}

	private void initializeConnection() {
		if (messagingConnection != null)
			return;

		synchronized (this) {
			if (messagingConnection == null) {
				messagingConnection = messagingConnectionProvider.provideMessagingConnection();
				// This is where messagingConnection.open() is NOT called and never was, making it pointless on the API level...

				log.debug(() -> "Initialized messaging connection: " + messagingConnection);
			}
		}
	}

	@Override
	public String toString() {
		return description();
	}

	@Override
	public String description() {
		if (messagingConnectionProvider == null) {
			return "StandardMessagingSessionProvider (unconfigured)";
		} else {
			return messagingConnectionProvider.description();
		}
	}
}
