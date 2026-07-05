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
package com.braintribe.model.access.security.manipulation;

import java.util.stream.Collectors;

import com.braintribe.exception.AuthorizationException;
import com.braintribe.logging.Logger;
import com.braintribe.model.access.security.InterceptorData;
import com.braintribe.model.access.security.query.QueryInterceptor;
import com.braintribe.model.accessapi.ManipulationRequest;
import com.braintribe.model.accessapi.ManipulationResponse;
import com.braintribe.model.generic.manipulation.AtomicManipulation;
import com.braintribe.model.generic.manipulation.CompoundManipulation;
import com.braintribe.model.generic.manipulation.Manipulation;
import com.braintribe.model.generic.manipulation.NormalizedCompoundManipulation;
import com.braintribe.model.meta.data.prompt.Visible;
import com.braintribe.model.processing.aop.api.context.AroundContext;
import com.braintribe.model.processing.aop.api.interceptor.AroundInterceptor;
import com.braintribe.model.processing.aop.api.interceptor.InterceptionException;
import com.braintribe.model.processing.manipulation.basic.normalization.Normalizer;
import com.braintribe.model.processing.security.manipulation.IllegalManipulationException;
import com.braintribe.model.processing.security.manipulation.ManipulationSecurityExpert;
import com.braintribe.model.processing.security.manipulation.SecurityViolationEntry;
import com.braintribe.model.processing.security.manipulation.ValidationResult;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.utils.lcd.CollectionTools;

/**
 * Interceptor for manipulations - runs all the configured {@link ManipulationSecurityExpert} for given manipulation
 * (which is most likely a {@link CompoundManipulation } consisting of multiple {@link AtomicManipulation}s).
 * 
 * To read more on validation process, see {@link ManipulationSecurityExpert}.
 */
public class ApplyManipulationInterceptor implements AroundInterceptor<ManipulationRequest, ManipulationResponse> {

	private static Logger logger = Logger.getLogger(ApplyManipulationInterceptor.class);
	private final InterceptorData iData;
	private final Validator validator;

	public ApplyManipulationInterceptor(InterceptorData iData) {
		this.iData = iData;
		this.validator = createValidator(iData);
	}

	private Validator createValidator(InterceptorData iData) {
		return iData.manipulationSecurityExperts.isEmpty() ? null : new Validator(iData);
	}

	@Override
	public ManipulationResponse run(AroundContext<ManipulationRequest, ManipulationResponse> context) throws InterceptionException {
		ManipulationRequest request = context.getRequest();

		if (QueryInterceptor.isCurrentUserTrusted(iData, context))
			return applyManipulation(context, request);

		validateModelIsVisible(context);

		if (isValidationActive()) {
			Manipulation manipulation = request.getManipulation();
			NormalizedCompoundManipulation normalizedManipulation = Normalizer.normalize(manipulation);
			request.setManipulation(normalizedManipulation);

			validate(normalizedManipulation, context.getSession());
		}

		return applyManipulation(context, request);
	}

	private void validateModelIsVisible(AroundContext<ManipulationRequest, ManipulationResponse> context) throws InterceptionException {
		boolean visible = context.getSession().getModelAccessory().getMetaData().is(Visible.T);
		if (!visible)
			throw new AuthorizationException("Cannot apply manipulation as model is not visible.");
	}

	private boolean isValidationActive() {
		return validator != null;
	}

	private ManipulationResponse applyManipulation(AroundContext<ManipulationRequest, ManipulationResponse> context,
			ManipulationRequest manipulationRequest) throws InterceptionException {

		return context.proceed(manipulationRequest);
	}

	private void validate(NormalizedCompoundManipulation manipulation, PersistenceGmSession session) {
		ValidationResult result = validator.validate(manipulation, session);

		if (CollectionTools.isEmpty(result.getViolationEntries()))
			return;

		logger.error("Error validating manipulation!\n" + manipulation.stringify() + "\n" + printViolationEntriesOf(result));
		throw new IllegalManipulationException(result);
	}

	private String printViolationEntriesOf(ValidationResult result) {
		return "Violations:\n" + result.getViolationEntries().stream() //
				.map(SecurityViolationEntry::getDescription) //
				.collect(Collectors.joining("\n"));
	}
}
