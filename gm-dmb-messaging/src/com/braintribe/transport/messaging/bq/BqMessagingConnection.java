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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import com.braintribe.logging.Logger;
import com.braintribe.transport.messaging.api.MessagingComponentStatus;
import com.braintribe.transport.messaging.api.MessagingConnection;
import com.braintribe.transport.messaging.api.MessagingContext;
import com.braintribe.transport.messaging.api.MessagingException;

public class BqMessagingConnection implements MessagingConnection {

	private static final BqMessaging BQ_MESSAGING = new BqMessaging();

	private final MessagingContext messagingContext;
	private BqMessaging bqMessaging;
	private Long connectionId;

	private final ReentrantLock connectionLock = new ReentrantLock();
	private final long connectionLockTimeout = 10L;
	private final TimeUnit connectionLockTimeoutUnit = TimeUnit.SECONDS;

	private volatile MessagingComponentStatus status = MessagingComponentStatus.NEW;
	private List<BqMessagingSession> sessions = new ArrayList<>();

	private static final Logger log = Logger.getLogger(BqMessagingConnection.class);

	public BqMessagingConnection(MessagingContext messagingContext) {
		this.messagingContext = messagingContext;
	}

	public BqMessaging getBqMessaging() {
		if (bqMessaging != null)
			return bqMessaging;

		if (status == MessagingComponentStatus.NEW)
			throw new IllegalStateException("BqMessaging is not yet initialized on state: " + status);
		else
			throw new MessagingException("Connection is " + status);
	}

	public Long getConnectionId() {
		return connectionId;
	}

	@Override
	public BqMessagingSession createMessagingSession() throws MessagingException {
		open();

		BqMessagingSession session = new BqMessagingSession();
		session.setConnection(this, messagingContext);
		session.open();

		sessions.add(session);

		return session;
	}

	@Override
	public void open() throws MessagingException {
		try {
			if (!connectionLock.tryLock(connectionLockTimeout, connectionLockTimeoutUnit))
				return;

			try {

				if (status == MessagingComponentStatus.CLOSING || status == MessagingComponentStatus.CLOSED) {
					throw new MessagingException("Connection [ " + connectionId + " ] in unexpected state: " + status.toString().toLowerCase());
				}

				if (status == MessagingComponentStatus.OPEN) {
					// opening an already opened connection shall be a no-op
					log.trace(() -> "open() called in an already opened connection. Connection [ " + connectionId + " ] already established.");
					return;
				}

				if (bqMessaging == null) {
					synchronized (this) {
						if (bqMessaging == null) {
							bqMessaging = BQ_MESSAGING;
						}
					}
				}

				this.connectionId = bqMessaging.connect();

				log.debug(() -> "Connection [ #" + connectionId + " ] established.");

				this.status = MessagingComponentStatus.OPEN;

			} finally {
				connectionLock.unlock();
			}

		} catch (InterruptedException e) {
			throwConnectionLockInterrupted(e, "open");
		}
	}

	@Override
	public void close() throws MessagingException {
		try {
			if (!connectionLock.tryLock(connectionLockTimeout, connectionLockTimeoutUnit))
				return;

			try {
				if (status == MessagingComponentStatus.CLOSING || status == MessagingComponentStatus.CLOSED) {
					// closing an already closed connection shall be a no-op
					log.debug(() -> "No-op close() call. Connection closing already requested. current state: " + status.toString().toLowerCase());
					return;
				}

				if (status == MessagingComponentStatus.NEW && log.isTraceEnabled()) {
					log.trace("Closing a connection which was not opened. current state: " + status.toString().toLowerCase());
				}

				this.status = MessagingComponentStatus.CLOSING;
				closeSessions();

				if (bqMessaging != null)
					bqMessaging.disconnect(connectionId);

				bqMessaging = null;

				if (status == MessagingComponentStatus.NEW) {
					log.debug(() -> "Unopened connection closed.");
				} else {
					log.debug(() -> "Connection [ #" + connectionId + " ] closed.");
				}

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

	private void closeSessions() {
		for (BqMessagingSession session : sessions) {
			try {
				session.close();
			} catch (Throwable t) {
				log.error("Failed to close session created by this messaging connection: " + session + ": " + t.getMessage(), t);
			}
		}
		sessions = null;
	}

	private void throwConnectionLockInterrupted(InterruptedException e, String openOrClose) {
		throw new MessagingException("Failed to " + openOrClose + " the messaging connection. Unable to acquire lock after " + connectionLockTimeout
				+ " " + connectionLockTimeoutUnit.toString().toLowerCase() + " : " + e.getMessage(), e);
	}

	/**
	 * <p>
	 * Asserts that this connection is in a valid state to be used: Already open. not "closing" nor "closed";
	 * 
	 * <p>
	 * This method does not try to open a new connection.
	 * 
	 * @throws MessagingException
	 *             If this connection is NOT in a valid state to be used
	 */
	protected void assertOpen() throws MessagingException {
		if (status != MessagingComponentStatus.OPEN)
			throw new MessagingException("Connection is not opened. Current state: " + status.toString().toLowerCase());
	}

}
