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
package com.braintribe.transport.messaging.jms;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import javax.jms.Connection;

import com.braintribe.logging.Logger;
import com.braintribe.transport.messaging.api.MessagingConnectionProvider;
import com.braintribe.transport.messaging.api.MessagingContext;
import com.braintribe.transport.messaging.api.MessagingException;

/**
 * Abstract implementation of the {@link MessagingConnectionProvider} interface that serves as a base for all JMS-based
 * messaging implementations.
 * <br><br>
 * It deals with the handling of {@link JmsConnection} objects and closes them when {@link #close()} is invoked.
 * <br><br>
 * A subclass of this class has to implement the method {@link #createJmsConnection()}, which should return a valid
 * {@link Connection} object.
 * 
 * @see MessagingConnectionProvider
 */
public abstract class JmsConnectionProvider implements MessagingConnectionProvider<JmsConnection> {

	private static final Logger logger = Logger.getLogger(JmsConnectionProvider.class);
	
	protected MessagingContext context = null;
	
	protected Set<JmsConnection> connections = new HashSet<JmsConnection>();
	protected ReentrantLock connectionsLock = new ReentrantLock();

	public abstract javax.jms.Queue getQueue(JmsSession session, String queueName) throws MessagingException;
	public abstract javax.jms.Topic getTopic(JmsSession session, String queueName) throws MessagingException;
	
	public MessagingContext getContext() {
		return context;
	}
	public void setContext(MessagingContext context) {
		this.context = context;
	}

	protected abstract Connection createJmsConnection() throws MessagingException;
	
	protected void addConnection(JmsConnection connection) {
		connectionsLock.lock();
		try {
			connections.add(connection);
		} finally {
			connectionsLock.unlock();
		}
	}
	
	@Override
	public void close() {
		Set<JmsConnection> cloneSet = new HashSet<JmsConnection>();
		connectionsLock.lock();
		try {
			cloneSet.addAll(this.connections);
			connections.clear();
		} finally {
			connectionsLock.unlock();
		}
		
		for (JmsConnection c : cloneSet) {
			try {
				c.close();
			} catch (MessagingException e) {
				logger.error("Error while closing connection "+c, e);
			}
		}

	}
	
	protected void connectionClosed(JmsConnection connection) {
		connectionsLock.lock();
		try {
			connections.remove(connection);
		} finally {
			connectionsLock.unlock();
		}
	}
	
	@Override
	public String toString() {
		return description();
	}
	
	@Override
	public String description() {
		return "Generic JMS Connection";
	}
}
