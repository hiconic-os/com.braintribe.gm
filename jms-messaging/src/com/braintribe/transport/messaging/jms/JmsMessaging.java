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

import com.braintribe.transport.messaging.api.Messaging;

/**
 * This is the main entry point to JMS-based messaging. It is for the actual implementation to provide
 * a subclass of this abstract class.
 * <br><br>
 * Any subclass of this class has to implement the {@link #createConnectionProvider(com.braintribe.model.messaging.expert.Messaging, com.braintribe.transport.messaging.api.MessagingContext)} 
 * method.
 * @see Messaging
 */
public abstract class JmsMessaging <T extends com.braintribe.model.messaging.jms.JmsConnection> implements Messaging<T> {
	//Intentionally left blank
}
