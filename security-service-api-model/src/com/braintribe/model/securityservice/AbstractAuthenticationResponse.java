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
package com.braintribe.model.securityservice;

import com.braintribe.model.generic.annotation.Abstract;

import java.util.Set;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.securityservice.messages.AuthenticationResponseMessage;
import com.braintribe.model.securityservice.messages.AuthenticationStatusMessage;

/** @deprecated re-added because it's used in GWT */
@Deprecated
@Abstract
public interface AbstractAuthenticationResponse extends SecurityResponse {

	EntityType<AbstractAuthenticationResponse> T = EntityTypes.T(AbstractAuthenticationResponse.class);

	boolean getSuccessful();
	void setSuccessful(boolean successful);

	AuthenticationStatusMessage getStatusMessage();
	void setStatusMessage(AuthenticationStatusMessage statusMessage);

	Set<AuthenticationResponseMessage> getMessages();
	void setMessages(Set<AuthenticationResponseMessage> messages);

}
