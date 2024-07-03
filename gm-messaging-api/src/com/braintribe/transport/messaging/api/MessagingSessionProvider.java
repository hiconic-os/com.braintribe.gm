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
 * Offers a convenience for applications for obtaining {@link MessagingSession}s abstracting the handling of
 * {@link MessagingConnection}(s) and their lifecycle.
 * 
 *
 */
public interface MessagingSessionProvider {

	/**
	 * <p>
	 * Provides a brand new {@link MessagingSession} established exclusively for the caller.
	 * 
	 * <p>
	 * Once provided, callers must ensure that sessions are always closed (with {@link MessagingSession#close()}) when
	 * no longer used.
	 * 
	 * @return A {@link MessagingSession} established exclusively for the caller.
	 * @throws MessagingException
	 *             In case a {@link MessagingSession} fails to be provided
	 */
	MessagingSession provideMessagingSession() throws MessagingException;

	/**
	 * <p>
	 * Closes this messaging session provider.
	 */
	void close();

	/**
	 * Returns a description describing the messaging provider. This can be compared to a toString method
	 * 
	 * @return A description describing the messaging provider.
	 */
	String description();

}
