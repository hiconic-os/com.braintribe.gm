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
package com.braintribe.model.processing.securityservice.api;

import java.util.Date;
import java.util.Map;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.model.time.TimeSpan;
import com.braintribe.model.user.User;
import com.braintribe.model.usersession.UserSession;
import com.braintribe.model.usersession.UserSessionType;

/**
 * <p>
 * A service used to manipulate {@link UserSession user sessions};
 * 
 */
public interface UserSessionService {

	/**
	 * <p>
	 * Creates a {@link UserSession} for the {@link User} given by {@code user}.
	 * 
	 * @param user
	 *            {@link User} to have a {@link UserSession} created for.
	 * @param type
	 *            {@link UserSessionType}.
	 * @param maxIdleTime
	 *            {@link TimeSpan} to be used as the created session's max idle time (
	 *            {@link UserSession#getMaxIdleTime()}).
	 * @param maxAge
	 *            If provided, this {@link TimeSpan} is used to calculate the user session's fixed expiry date (
	 *            {@link UserSession#getFixedExpiryDate()}). Ignored if {@code fixedExpiryDate} parameter is provided.
	 * @param fixedExpiryDate
	 *            If provided, determines the user session's fixed expiry date ({@link UserSession#getFixedExpiryDate()})
	 * @param internetAddress
	 *            Internet address of the caller for whom the session is being created, to be accessible through
	 *            {@link UserSession#getCreationInternetAddress()}
	 * @param properties
	 *            If provided, determines the user session's properties ({@link UserSession#getProperties()})
	 * @param acquirationKey
	 *            A key to access the session alternatively by
	 * @return {@link UserSession} object for the {@link User} given by the {@code user} parameter.
	 */
	Maybe<UserSession> createUserSession(User user, UserSessionType type, TimeSpan maxIdleTime, TimeSpan maxAge, Date fixedExpiryDate,
			String internetAddress, Map<String, String> properties, String acquirationKey, boolean blocksAuthenticationAfterLogout);

	/**
	 * <p>
	 * Fetches a {@link UserSession} object matching the given {@code sessionId} in the underlying persistence layer.
	 * 
	 * @param sessionId
	 *            session id of the {@link UserSession} to be fetched (matching {@link UserSession#getSessionId()})
	 * 
	 * @return {@link UserSession} object matching the given {@code sessionId} or null if no {@link UserSession} is found.
	 * 
	 */
	Maybe<UserSession> findUserSession(String sessionId);

	/**
	 * <p>
	 * Fetches a {@link UserSession} object matching the given {@code sessionId} in the underlying persistence layer,
	 * updates it's {@code lastAccessedDate} to the current and recalculates the {@code idleExpiryDate}.
	 * 
	 * @param acquirationKey
	 *            acquirationKey of the {@link UserSession} to be fetched
	 * 
	 * @return {@link UserSession} object matching the given {@code sessionId} or null if no {@link UserSession} is found.
	 * 
	 */
	Maybe<UserSession> findUserSessionByAcquirationKey(String acquirationKey);

	/**
	 * <p>
	 * Finds a {@link UserSession} object matching the given {@code sessionId} in the underlying persistence layer, updates
	 * it's {@code lastAccessedDate} to the current and recalculates the {@code idleExpiryDate}.
	 * 
	 * @param sessionId
	 *            session id of the {@link UserSession} to be fetched (matching {@link UserSession#getSessionId()})
	 * 
	 */
	void touchUserSession(String sessionId, Date lastAccessDate, Date expiryDate);

	/**
	 * <p>
	 * Deletes the {@link UserSession} object matching the given {@code sessionId} in the underlying persistence layer.
	 * 
	 * @param sessionId
	 *            session id of the {@link UserSession} to be deleted.
	 */
	Maybe<DeletedSessionInfo> deleteUserSession(String sessionId);
}
