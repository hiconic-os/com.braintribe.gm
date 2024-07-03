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
package com.braintribe.transport.messaging.api.test.rpc;

import java.util.UUID;
import java.util.function.Supplier;

import com.braintribe.model.messaging.Destination;
import com.braintribe.model.messaging.Message;
import com.braintribe.transport.messaging.api.MessageConsumer;
import com.braintribe.transport.messaging.api.MessageProducer;
import com.braintribe.transport.messaging.api.MessagingSession;

public class GmMessagingRpcTestClient {

	Supplier<MessagingSession> sessionProvider;
	
	private MessagingSession session;
	private Destination rpcDestination;
	private Destination replyToDestination;
	
	private MessageProducer producer;
	private MessageConsumer consumer;
	
	public GmMessagingRpcTestClient(Supplier<MessagingSession> sessionProvider, String rpcDestinationName, String replyToDestinationName) throws Exception {
		this.sessionProvider = sessionProvider;
		this.session = sessionProvider.get();
		this.rpcDestination = this.session.createQueue(rpcDestinationName);
		this.replyToDestination = this.session.createQueue(replyToDestinationName);
		
		this.producer = session.createMessageProducer(this.rpcDestination);
		this.consumer = session.createMessageConsumer(this.replyToDestination);
	}
	
	public Object call(Object request) throws Exception {
		
		String correlationId = UUID.randomUUID().toString();
		
		Message message = session.createMessage();
		message.setBody(request);
		message.setCorrelationId(correlationId);
		message.setReplyTo(replyToDestination);
		message.setPersistent(true);
		
		producer.sendMessage(message);
		
		Message replyMessage = null;
		while ((replyMessage = consumer.receive()) != null) {
			if (replyMessage.getCorrelationId().equals(correlationId)) {
				System.out.println("received "+replyMessage.getBody()+" from "+replyToDestination.getName()+" with correlation id "+replyMessage.getCorrelationId());
				return replyMessage.getBody();
			}
		}
		
		throw new Exception("failed request");
	}
	
	public void close() throws Exception {
		session.close();
	}
	
}
