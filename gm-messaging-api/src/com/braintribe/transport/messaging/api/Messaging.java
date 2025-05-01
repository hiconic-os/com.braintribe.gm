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
package com.braintribe.transport.messaging.api;

/**
 * <p>
 * The main entry point to a GenericModel-based messaging system.
 * 
 * <p>
 * Providers of specific message brokers must implement this interface.
 * 
 * @deprecated This is useless garbage, and it's not really used anywhere. This interface describes the same things as the deployment expert in the
 *             module - it turns {@link com.braintribe.model.messaging.expert.Messaging} instance into a {@link MessagingConnectionProvider}
 */
@Deprecated
public interface Messaging<T extends com.braintribe.model.messaging.expert.Messaging> {

	/**
	 * <p>
	 * Creates a {@link MessagingConnectionProvider} based on the given denotation type.
	 * 
	 * @param connection
	 *            The {@link com.braintribe.model.messaging.expert.Messaging} denotation type for which a
	 *            {@link MessagingConnectionProvider} must be created
	 * @param context
	 *            The {@link MessagingContext} for which the {@link MessagingConnectionProvider} must be created
	 * @return A {@link MessagingConnectionProvider} instance created based on the given denotation type and context
	 */
	MessagingConnectionProvider<? extends MessagingConnection> createConnectionProvider(T connection, MessagingContext context);

}
