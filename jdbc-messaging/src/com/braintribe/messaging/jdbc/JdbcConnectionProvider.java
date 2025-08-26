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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import javax.sql.DataSource;

import com.braintribe.cfg.Required;
import com.braintribe.logging.Logger;
import com.braintribe.model.messaging.Message;
import com.braintribe.transport.messaging.api.MessagingConnectionProvider;
import com.braintribe.transport.messaging.api.MessagingContext;
import com.braintribe.transport.messaging.api.MessagingException;
import com.braintribe.transport.messaging.impl.StandardMessagingSessionProvider;

/**
 * {@link MessagingConnectionProvider} implementation for providing {@link JdbcMsgConnection}(s).
 * <p>
 * NOTE: Messages with {@link Message#getExpiration()} b
 * 
 * @see MessagingConnectionProvider
 * @see JdbcMsgConnection
 */
public class JdbcConnectionProvider implements MessagingConnectionProvider<JdbcMsgConnection> {

	private static final Logger logger = Logger.getLogger(JdbcConnectionProvider.class);

	private String name = "<Unnamed JDBC Messaging>";
	private String sqlPrefix = "hc";
	private DataSource dataSource;
	private MessagingContext messagingContext;

	private final Set<JdbcMsgConnection> connections = new HashSet<>();
	private final ReentrantLock connectionsLock = new ReentrantLock();
	protected Map<String, String> resolvedHosts;
	protected long nextHostResolving = -1L;

	// @formatter:off
	@Required public void setName(String name)											{ this.name = name; }                          
	@Required public void setSqlPrefix(String sqlPrefix)                                { this.sqlPrefix = sqlPrefix; }
	@Required public void setDataSource(DataSource dataSource)                          { this.dataSource = dataSource; }
	@Required public void setMessagingContext(MessagingContext messagingContext)        { this.messagingContext = messagingContext; }
	// @formatter:on

	/**
	 * WTF - this impl makes no sense <br>
	 * why would you even create multiple instances?<br>
	 * There is only one instance needed, which is used inside {@link StandardMessagingSessionProvider}
	 */
	@Override
	public JdbcMsgConnection provideMessagingConnection() throws MessagingException {
		JdbcMsgConnection connection = new JdbcMsgConnection(sqlPrefix, dataSource, messagingContext);

		connectionsLock.lock();
		try {
			connections.add(connection);
		} finally {
			connectionsLock.unlock();
		}

		return connection;
	}

	/**
	 * This method should be called periodically to delete expired topic messages.
	 */
	public int deleteExpiredMessages() {
		int deleted = 0;

		for (JdbcMsgConnection connection : connections)
			deleted += connection.deleteExpiredMessages();

		return deleted;
	}

	@Override
	public void close() {
		connectionsLock.lock();
		try {
			for (JdbcMsgConnection con : connections) {
				try {
					con.close();
				} catch (Exception e) {
					logger.error("Could not close connection: " + con, e);
				}
			}
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
		return "JDBC Messaging (name: '" + name + "', tablePrefix: '" + sqlPrefix + "')";
	}
}
