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
package com.braintribe.model.processing.securityservice.commons.eval;

import java.util.function.Supplier;

import com.braintribe.cfg.Required;
import com.braintribe.common.attribute.common.UserInfo;
import com.braintribe.common.attribute.common.UserInfoAttribute;
import com.braintribe.model.generic.eval.EvalContext;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.processing.securityservice.api.attributes.LenientAuthenticationFailure;
import com.braintribe.model.processing.securityservice.commons.service.ContextualizedAuthorization;
import com.braintribe.model.processing.service.api.aspect.IsAuthorizedAspect;
import com.braintribe.model.processing.service.api.aspect.RequestorSessionIdAspect;
import com.braintribe.model.processing.service.api.aspect.RequestorUserNameAspect;
import com.braintribe.model.processing.service.common.context.UserSessionAspect;
import com.braintribe.model.service.api.ServiceRequest;
import com.braintribe.model.usersession.UserSession;

/**
 * Evaluates requests with a supplied, trusted user session and its complete authorization attributes.
 * Keeps the context consistent with {@link ContextualizedAuthorization}, including replacement of inherited
 * identity information and clearing a previous lenient authentication failure. Does not validate the supplied session.
 */
public class AuthorizingServiceRequestEvaluator implements Evaluator<ServiceRequest> {

	private Evaluator<ServiceRequest> delegate;
	private Supplier<UserSession> userSessionProvider;
	
	@Required
	public void setUserSessionProvider(Supplier<UserSession> userSessionProvider) {
		this.userSessionProvider = userSessionProvider;
	}
	
	@Required
	public void setDelegate(Evaluator<ServiceRequest> delegate) {
		this.delegate = delegate;
	}
	
	@Override
	public <T> EvalContext<T> eval(ServiceRequest evaluable) {
		EvalContext<T> evalContext = delegate.<T>eval(evaluable);
		UserSession userSession = userSessionProvider.get();
		evalContext.setAttribute(UserSessionAspect.class, userSession);
		evalContext.setAttribute(IsAuthorizedAspect.class, true);
		evalContext.setAttribute(RequestorSessionIdAspect.class, userSession.getSessionId());
		String userName = userSession.getUser().getName();
		evalContext.setAttribute(RequestorUserNameAspect.class, userName);
		evalContext.setAttribute(UserInfoAttribute.class, UserInfo.of(userName, userSession.getEffectiveRoles()));
		evalContext.setAttribute(LenientAuthenticationFailure.class, null);
		return evalContext;
	}
}
