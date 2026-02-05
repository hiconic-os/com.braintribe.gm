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

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import com.braintribe.model.messaging.Destination;
import com.braintribe.transport.messaging.api.MessageProducer;
import com.braintribe.transport.messaging.api.MessagingConnection;
import com.braintribe.transport.messaging.api.MessagingConnectionProvider;
import com.braintribe.transport.messaging.api.MessagingContext;
import com.braintribe.transport.messaging.api.test.GmMessagingDeliveryQueueTest;

public class BqMessagingDeliveryQueueTest extends GmMessagingDeliveryQueueTest {

	@Override
	protected MessagingConnectionProvider<? extends MessagingConnection> getMessagingConnectionProvider() {
		return TestMessagingConnectionProvider.instance.get();
	}

	@Override
	protected MessagingContext getMessagingContext() {
		return TestMessagingConnectionProvider.instance.getMessagingContext();
	}

	/**
	 * <p>
	 * Produces an unmarshallable message payload to ensure the consumers behavior upon marshalling errors.
	 */
	@Override
	protected void sendUnmarshallableMessage(MessageProducer messageProducer) {

		BqMessageProducer producer = (BqMessageProducer) messageProducer;

		Destination destination = producer.getDestination();

		char destinationType = producer.getDestinationType(destination);

		byte[] messageBody = "This is just an example of unmarshallable body".getBytes();

		Map<String, Object> empty = Collections.emptyMap();

		producer.getSession().getConnection().getBqMessaging().sendMessage(destinationType, destination.getName(), UUID.randomUUID().toString(),
				messageBody, 5, 0, empty, empty);

	}

}
