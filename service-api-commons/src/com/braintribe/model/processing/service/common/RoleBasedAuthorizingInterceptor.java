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
package com.braintribe.model.processing.service.common;

import static java.util.Collections.emptySet;

import java.util.Set;

import com.braintribe.cfg.Required;
import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.Reasons;
import com.braintribe.gm.model.security.reason.Forbidden;
import com.braintribe.gm.model.security.reason.MissingSession;
import com.braintribe.model.processing.service.api.ReasonedServicePreProcessor;
import com.braintribe.model.processing.service.api.ServiceRequestContext;
import com.braintribe.model.processing.service.common.context.UserSessionAspect;
import com.braintribe.model.service.api.ServiceRequest;
import com.braintribe.model.usersession.UserSession;

public class RoleBasedAuthorizingInterceptor implements ReasonedServicePreProcessor<ServiceRequest> {

	private Set<String> allowedRoles = emptySet();

	@Required
	public void setAllowedRoles(Set<String> allowedRoles) {
		this.allowedRoles = allowedRoles;
	}

	@Override
	public Maybe<? extends ServiceRequest> processReasoned(ServiceRequestContext requestContext, ServiceRequest request) {
		UserSession userSession = requestContext.findOrNull(UserSessionAspect.class);
		if (userSession == null)
			return Reasons.build(MissingSession.T).text("Missing session").toMaybe();

		Set<String> roles = userSession.getEffectiveRoles();

		boolean missesRequiredRole = !allowedRoles.stream() //
				.filter(roles::contains) //
				.findFirst() //
				.isPresent();

		if (missesRequiredRole)
			return Reasons.build(Forbidden.T).text("Not authorized").toMaybe();

		return Maybe.complete(request);
	}
}
