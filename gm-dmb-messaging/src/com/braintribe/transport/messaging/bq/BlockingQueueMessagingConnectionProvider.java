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

import com.braintribe.transport.messaging.api.MessagingConnectionProvider;
import com.braintribe.transport.messaging.api.MessagingContext;
import com.braintribe.transport.messaging.api.MessagingException;

/**
 * {@link MessagingConnectionProvider} implementation for providing {@link BqMessagingConnection}(s).
 * 
 * @see MessagingConnectionProvider
 * @see BqMessagingConnection
 */
public class BlockingQueueMessagingConnectionProvider implements MessagingConnectionProvider<BqMessagingConnection> {

    private MessagingContext messagingContext;

	public void setMessagingContext(MessagingContext messagingContext) {
		this.messagingContext = messagingContext;
	}
    
	@Override
	public BqMessagingConnection provideMessagingConnection() throws MessagingException {
		return new BqMessagingConnection(messagingContext);
	}
	
	@Override
	public void close() {
		// no-op, there is nothing to be closed so far in this MessagingConnectionProvider
	}
	
	@Override
	public String toString() {
		return description();
	}

	@Override
	public String description() {
		return "Blocking Queue Messaging";
	}
}
