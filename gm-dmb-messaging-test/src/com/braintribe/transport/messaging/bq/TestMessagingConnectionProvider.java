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

import com.braintribe.codec.marshaller.bin.Bin2Marshaller;
import com.braintribe.transport.messaging.api.MessagingConnectionProvider;
import com.braintribe.transport.messaging.api.MessagingContext;

public class TestMessagingConnectionProvider {
	
	public static final TestMessagingConnectionProvider instance = new TestMessagingConnectionProvider();
	
	private final BlockingQueueMessagingConnectionProvider messagingConnectionProvider;
	
	private TestMessagingConnectionProvider() {
		messagingConnectionProvider = getMessagingConnectionProvider();
	}
	
	public MessagingConnectionProvider<?> get() {
		return messagingConnectionProvider;
	}
	
	private BlockingQueueMessagingConnectionProvider getMessagingConnectionProvider() {
		BlockingQueueMessagingConnectionProvider gmDmbMqConnectionProvider = new BlockingQueueMessagingConnectionProvider();
		gmDmbMqConnectionProvider.setMessagingContext(getMessagingContext());
		
		return gmDmbMqConnectionProvider;
	}
	
	protected MessagingContext getMessagingContext() {
		MessagingContext context = new MessagingContext();
		context.setMarshaller(new Bin2Marshaller());
		return context;
	}
}
