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

import java.util.Date;
import java.util.Map;

import com.braintribe.model.generic.annotation.meta.Description;
import com.braintribe.model.generic.eval.EvalContext;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.securityservice.credentials.Credentials;
import com.braintribe.model.service.api.ServiceRequest;

/**
 * This request is meant to be external: it authenticates to any delegate authentication service and open a user session
 * if the authentication was successful.
 * 
 * This request is completely generic, and agnostic to the type of credential, see {@link SimplifiedOpenUserSession} for
 * simple requests.
 * 
 * @see AuthenticateCredentials
 * @see SimplifiedOpenUserSession
 * @see OpenUserSessionWithUserAndPassword
 */
public interface OpenUserSession extends SecurityRequest {

	EntityType<OpenUserSession> T = EntityTypes.T(OpenUserSession.class);

	String getLocale();
	void setLocale(String locale);

	Credentials getCredentials();
	void setCredentials(Credentials credentials);

	Map<String, String> getProperties();
	void setProperties(Map<String, String> properties);

	Date getExpiryDate();
	void setExpiryDate(Date expiryDate);
	
	@Description("Addresses and entry point by name to activate a specific authorization. Requires internal priviledge")
	String getEntryPoint();
	void setEntryPoint(String entryPoint);
	
	@Description("Skips any authorization for the session creation (e.g HTTP origin driven entry point rules). Requires internal priviledge")
	boolean getSkipAuthorization();
	void setSkipAuthorization(boolean skipAuthorization);

	@Override
	EvalContext<? extends OpenUserSessionResponse> eval(Evaluator<ServiceRequest> evaluator);

}
