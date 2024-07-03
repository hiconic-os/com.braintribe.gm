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
package com.braintribe.model.processing.service.common.eval;

import java.util.function.Supplier;

import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.Required;
import com.braintribe.model.generic.eval.EvalContext;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.processing.service.common.context.UserSessionAspect;
import com.braintribe.model.service.api.ServiceRequest;
import com.braintribe.model.usersession.UserSession;

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
		evalContext.setAttribute(UserSessionAspect.class, userSessionProvider.get());
		return evalContext;
	}
}
