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
package com.braintribe.model.securityservice.messages;

import java.util.Set;


import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.securityservice.credentials.Credentials;
import com.braintribe.model.securityservice.credentials.HasUserIdentification;
import com.braintribe.model.securityservice.credentials.UserPasswordCredentials;
import com.braintribe.model.securityservice.credentials.identification.EmailIdentification;
import com.braintribe.model.securityservice.credentials.identification.UserIdentification;
import com.braintribe.model.securityservice.credentials.identification.UserNameIdentification;

/** @deprecated re-adding as used in GWT */
@Deprecated
public interface UnsupportedCredentialsMessage extends AuthenticationStatusMessage {

	EntityType<UnsupportedCredentialsMessage> T = EntityTypes.T(UnsupportedCredentialsMessage.class);

	/**
	 * <p>Gets the type signatures of the {@link Credentials} types supported by the component which responded with this message.
	 *
	 * @return
	 *   The type signatures of the {@link Credentials} types supported by the component which responded with this message.
	 */
	Set<String> getSupportedCredentials();
	
	/**
	 * <p>Sets the type signatures of the {@link Credentials} types supported by the component which responded with this message.
	 *
	 * @param supportedCredentials
	 *   The type signatures of the {@link Credentials} types supported by the component which responded with this message.
	 */
	void setSupportedCredentials(Set<String> supportedCredentials);

	/**
	 * <p>Gets the type signatures of the {@link UserIdentification} types supported by component the which responded with this message.
	 * 
	 * <p>If a supported {@link Credentials} type implements {@link HasUserIdentification}, components may still respond with a 
	 * {@code UnsupportedCredentialsMessage} in case a particular {@link UserIdentification} type is not supported alongside the supported credentials.
	 * 
	 * <p>e.g.: A component may choose to support {@link UserPasswordCredentials} only when associated with {@link UserNameIdentification}, 
	 *    responding with a {@code UnsupportedCredentialsMessage} if the supported credentials contained a different {@link UserIdentification}
	 *    type, like {@link EmailIdentification}.
	 * 
	 * @return
	 *   The type signatures of the {@link UserIdentification} types supported by component the which responded with this message.
	 */
	Set<String> getSupportedUserIdentifications();
	
	/**
	 * <p>Sets the type signatures of the {@link UserIdentification} types supported by component the which responded with this message.
	 * 
	 * @see #getSupportedUserIdentifications()
	 * 
	 * @param supportedUserIdentifications
	 *   The type signatures of the {@link UserIdentification} types supported by component the which responded with this message.
	 */
	void setSupportedUserIdentifications(Set<String> supportedUserIdentifications);

}
