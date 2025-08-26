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

import com.braintribe.model.messaging.Destination;
import com.braintribe.transport.messaging.api.MessagingContext;

public abstract class JdbcAbstractMessageHandler {

	public final MessagingContext messagingContext;
	public final JdbcMessagingSession session;
	public final JdbcMsgConnection connection;
	public final Destination destination; // nullable for producers, non-null for consumers

	public JdbcAbstractMessageHandler(JdbcMessagingSession session, Destination destination) {
		this.session = session;
		this.connection = session.connection;
		this.messagingContext = connection.messagingContext;
		this.destination = destination;
	}

	public abstract void close();

}
