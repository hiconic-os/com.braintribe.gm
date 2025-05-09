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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.braintribe.model.messaging.Destination;
import com.braintribe.model.messaging.Message;
import com.braintribe.model.messaging.Queue;
import com.braintribe.testing.category.Slow;
import com.braintribe.transport.messaging.api.MessageConsumer;
import com.braintribe.transport.messaging.api.MessageProducer;
import com.braintribe.transport.messaging.api.MessagingConnection;
import com.braintribe.transport.messaging.api.MessagingSession;

/**
 * <p>
 * Tests the delivery of messages through queues.
 * 
 */
public abstract class GmMessagingDeliveryQueueTest extends GmMessagingDeliveryTest {

	@Override
	public Class<? extends Destination> getDestinationType() {
		return Queue.class;
	}

	@Test
	public void testMessagesDistribution() throws Exception {

		MessagingConnection connection = getMessagingConnectionProvider().provideMessagingConnection();

		MessagingSession session = connection.createMessagingSession();

		Queue queue = session.createQueue("gm-test-queue-"+getMethodName());

		final MessageConsumer consumer1 = session.createMessageConsumer(queue);
		final MessageConsumer consumer2 = session.createMessageConsumer(queue);
		final MessageConsumer consumer3 = session.createMessageConsumer(queue);
		final MessageConsumer consumer4 = session.createMessageConsumer(queue);
		final MessageConsumer consumer5 = session.createMessageConsumer(queue);
		
		final Map<Message, MessageConsumer> recipients = new ConcurrentHashMap<>();
		
		consumer1.setMessageListener(message -> {
			consumer1.close();
			recipients.put(message, consumer1);
		});

		consumer2.setMessageListener(message -> {
			consumer2.close();
			recipients.put(message, consumer2);
		});

		consumer3.setMessageListener(message -> {
			consumer3.close();
			recipients.put(message, consumer3);
		});

		consumer4.setMessageListener(message -> {
			consumer4.close();
			recipients.put(message, consumer4);
		});

		consumer5.setMessageListener(message -> {
			consumer5.close();
			recipients.put(message, consumer5);
		});
		
		MessageProducer producer = session.createMessageProducer(queue);
		List<Message> messages = createMessages(session, 9);
		
		for (Message message : messages) {
			producer.sendMessage(message);
		}

		long timeout = System.currentTimeMillis() + 10000;
		int rec = 0;
		while (rec < 5) {
			System.out.println("Waiting 5 messages. Current: "+rec);
			Thread.sleep(500);
			rec = 0;
			for (@SuppressWarnings("unused") Map.Entry<Message, MessageConsumer> entry : recipients.entrySet()) {
				rec++;
			}
			if (System.currentTimeMillis() > timeout) {
				break;
			}
		}
		
		Thread.sleep(2000);
		
		//final count
		rec = recipients.size();
		for (Map.Entry<Message, MessageConsumer> entry : recipients.entrySet()) {
			System.out.println(entry.getKey().hashCode()+" was delivered to "+entry.getValue());
		}
		
		connection.close();
		
		// This makes no sense, why should it only deliver 5 messages?
		// If a consumer is called multiple times in parallel, closing it inside one of the onMessage() methods will not prevent it from receiving multiple messages.
		//Assert.assertEquals("It appears some closed producers also got messages", 5, rec);
		
	}

	@Test
	@Category(Slow.class)
	public void testReceiveTimeout() throws Exception {
		
		MessagingConnection connection = getMessagingConnectionProvider().provideMessagingConnection();
		
		MessagingSession session = connection.createMessagingSession();

		Destination destination = session.createQueue("tf-test-queue-"+getMethodName());

		MessageProducer messageProducer = session.createMessageProducer(destination);
		MessageConsumer messageConsumer = session.createMessageConsumer(destination);

		//send persistent message that will expire in 2 seconds.
		Message sentMessage = createMessage(session, true, 2000);
		messageProducer.sendMessage(sentMessage);
		
		//receive with 0 timeout, one message
		Message message = messageConsumer.receive(0);
		Assert.assertNotNull(message);

		//send persistent message that will expire in 2 seconds.
		sentMessage = createMessage(session, true, 2000);
		messageProducer.sendMessage(sentMessage);

		//receive with 1000 timeout, one message
		message = messageConsumer.receive(1000);
		Assert.assertNotNull(message);

		//send persistent message that will expire in 2 seconds.
		sentMessage = createMessage(session, true, 2000);
		messageProducer.sendMessage(sentMessage);

		//receive with 5000 timeout, one message
		message = messageConsumer.receive(2000);
		Assert.assertNotNull(message);
		
		//receive with 5000 timeout, no messages
		message = messageConsumer.receive(5000);
		Assert.assertNull(message);
		
		connection.close();
	}
	
	@Test
	public void testReceiveRetained() throws Exception {
		
		MessagingConnection connection = getMessagingConnectionProvider().provideMessagingConnection();
		
		MessagingSession session = connection.createMessagingSession();

		Destination destination = session.createQueue("tf-test-queue-"+getMethodName());

		MessageConsumer messageConsumer = session.createMessageConsumer(destination);

		//persistent message that will expire in 2 seconds.
		Message sentMessage = createMessage(session, true, 2000);
		
		MessageProducer messageProducer = session.createMessageProducer(destination);
		
		messageProducer.sendMessage(sentMessage);
		
		//Creating consumer after sendMessage();

		ReceiverMessageListener messageListener = new ReceiverMessageListener();
		ReceiverJob receiverJob = new ReceiverJob(messageConsumer, messageListener);
		
		ExecutorService executorService = submitReceiver(messageListener, receiverJob);

		try {
			assertDeliveries(getMethodName(), destination.getClass(), messageListener, Arrays.asList(sentMessage), 1, false);
		} finally {
			executorService.shutdownNow();
			connection.close();
		}
		
	}
	
	@Test
	public void testReceiveRetainedBatch() throws Exception {
		
		MessagingConnection connection = getMessagingConnectionProvider().provideMessagingConnection();
		
		MessagingSession session = connection.createMessagingSession();
		
		Destination destination = session.createQueue("tf-test-queue-"+getMethodName());

		MessageConsumer messageConsumer = session.createMessageConsumer(destination);

		//10.000 persistent messages that will expire in 5 seconds.
		List<Message> messages = createMessages(session, multipleMessagesQty, true, 5000);
		
		MessageProducer messageProducer = session.createMessageProducer(destination);
		
		for (Message message : messages) {
			messageProducer.sendMessage(message);
		}

		ReceiverMessageListener messageListener = new ReceiverMessageListener();
		ReceiverJob receiverJob = new ReceiverJob(messageConsumer, messageListener);

		ExecutorService executorService = submitReceiver(messageListener, receiverJob);
		
		try {
			assertDeliveries(getMethodName(), destination.getClass(), messageListener, messages, 1, false);
		} finally {
			executorService.shutdownNow();
			connection.close();
		}
		
	}
	
	
}
