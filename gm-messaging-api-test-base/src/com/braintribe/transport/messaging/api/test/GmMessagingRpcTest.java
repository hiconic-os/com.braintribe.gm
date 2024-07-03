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
package com.braintribe.transport.messaging.api.test;

import java.util.function.Supplier;

import org.junit.Test;

import com.braintribe.transport.messaging.api.MessagingConnection;
import com.braintribe.transport.messaging.api.MessagingSession;
import com.braintribe.transport.messaging.api.test.rpc.GmMessagingRpcTestClient;
import com.braintribe.transport.messaging.api.test.rpc.GmMessagingRpcTestServer;
import com.braintribe.transport.messaging.api.test.rpc.GmMessagingRpcTestServer.RpcRequestProcessor;

/**
 * <p>
 * Tests the rpc-over-messaging model through {@code GmMessagingApi}.
 * 
 */
public abstract class GmMessagingRpcTest extends GmMessagingTest {

	@Test
	public void testRpcMessaging() throws Exception {
		
		final MessagingConnection connection = getMessagingConnectionProvider().provideMessagingConnection();
		final MessagingSession session = connection.createMessagingSession();
		final MessagingSession session2 = connection.createMessagingSession();
		
		Supplier<MessagingSession> sessionProvider = new Supplier<MessagingSession>() {
			@Override
			public MessagingSession get() throws RuntimeException {
				return session;
			}
		};
		Supplier<MessagingSession> sessionProvider2 = new Supplier<MessagingSession>() {
			@Override
			public MessagingSession get() throws RuntimeException {
				return session2;
			}
		};
		
		GmMessagingRpcTestServer testServer = new GmMessagingRpcTestServer(sessionProvider, "rpc-queue", new RpcRequestProcessor() {
			@Override
			public Object process(Object request) {
				return "RESPONSE TO "+request;
			}
		});


		GmMessagingRpcTestClient client = new GmMessagingRpcTestClient(sessionProvider2, "rpc-queue", "rpc-queue-reply");
		
		Object response = client.call("REQUEST");
		
		System.out.println("RECEIVED: "+response);
		
		testServer.close();
		connection.close();

	}
	
}
