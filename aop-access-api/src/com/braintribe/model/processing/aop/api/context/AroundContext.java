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
package com.braintribe.model.processing.aop.api.context;

import com.braintribe.model.processing.aop.api.interceptor.AroundInterceptor;
import com.braintribe.model.processing.aop.api.service.AopIncrementalAccess;

/**
 * the context for the {@link AroundInterceptor}
 * 
 * @author dirk, pit
 * 
 * @param <I>
 *            - input, the request
 * @param <O>
 *            - output, the response
 */
public interface AroundContext<I, O> extends Context<I> {
	/**
	 * proceed with the next interceptor in line, using the input specified (rather than the original one)
	 * 
	 * @param input
	 *            - the overriding request
	 */
	O proceed(I input);

	/** proceed with the next interceptor in line, using the default input */
	O proceed();

	/** override the request for subsequent AroundInterceptors */
	void overrideRequest(I request);

	/**
	 * @return an identification that can be used to do a reentrant proceed call back.
	 * 
	 * @see AopIncrementalAccess#proceedAround(com.braintribe.model.aopaccessapi.AccessAspectAroundProceedRequest)
	 */
	String getProceedIdentification();

}
