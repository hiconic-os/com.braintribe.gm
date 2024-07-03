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
/**
 * The classes of this package are the base for any JMS-related implementation of the {@link com.braintribe.transport.messaging.api.Messaging} interface.
 * It provides implementations of these interfaces that are common to all JMS-based APIs.
 * <br><br>
 * Actual implementation that use this artifact as a base have to extend the following classes:
 * <ul>
 *  <li>{@link com.braintribe.transport.messaging.jms.JmsMessaging}</li>
 *  <li>{@link com.braintribe.transport.messaging.jms.JmsConnectionProvider}</li>
 *  <li>{@link com.braintribe.transport.messaging.jms.AbstractJmsMessagingSessionProvider}</li>
 * </ul>
 * <br><br>
 * The following artifacts are known at the time of writing to use this base code:
 * <br><br>
 * <ul>
 *  <li><code>JmsJndiMessaging</code>, which is an abstract base for</li>
 *  <li>&nbsp;&nbsp;&nbsp;<code>JmsJBossMessaging</code> (uses JBoss JMS messaging)</li>
 *  <li><code>JmsActiveMqMessaging</code> (uses Apache ActiveMQ messaging)</li>
 * </ul>
 */
package com.braintribe.transport.messaging.jms;
