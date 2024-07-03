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

import java.util.function.Supplier;

import com.braintribe.model.messaging.Destination;
import com.braintribe.model.messaging.Message;
import com.braintribe.transport.messaging.api.MessageConsumer;
import com.braintribe.transport.messaging.api.MessageListener;
import com.braintribe.transport.messaging.api.MessagingException;
import com.braintribe.transport.messaging.api.MessagingSession;

public class GmMessagingRpcTestServer implements MessageListener {
	
	Supplier<MessagingSession> sessionProvider;
	
	private MessagingSession session;
	private Destination rpcDestination;
	private RpcRequestProcessor processor;
	
	private MessageConsumer consumer;
	
	public GmMessagingRpcTestServer(Supplier<MessagingSession> sessionProvider, String rpcDestinationName, RpcRequestProcessor processor) throws Exception {
		this.sessionProvider = sessionProvider;
		this.session = sessionProvider.get();
		this.rpcDestination = this.session.createQueue(rpcDestinationName);
		
		this.processor = processor;
		
		this.consumer = session.createMessageConsumer(this.rpcDestination);
		this.consumer.setMessageListener(this);
	}
	
	@Override
	public void onMessage(Message message) throws MessagingException {
		
		System.out.println("received "+message.getBody()+", replying to "+message.getReplyTo().getName());
		
		Object processedBody = processor.process(message.getBody());
		
		Message replyMessage = session.createMessage();
		replyMessage.setBody(processedBody);
		replyMessage.setCorrelationId(message.getCorrelationId());
		
		session.createMessageProducer(message.getReplyTo()).sendMessage(replyMessage);
	}
	
	public void close() throws Exception {
		session.close();
	}
	
	public interface RpcRequestProcessor {
		
		Object process(Object request);
		
	}
	
}
