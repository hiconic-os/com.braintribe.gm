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
package com.braintribe.model.processing.aop.api.interceptor;

import com.braintribe.model.processing.aop.api.aspect.Advice;
import com.braintribe.model.processing.aop.api.context.BeforeContext;

/**
 * the interceptor to be called for the before {@link Advice}
 * 
 * @author pit, dirk
 * 
 * @param <I>
 *            - the input, the request
 * @param <O>
 *            - the output, the response
 */
public interface BeforeInterceptor<I, O> extends Interceptor {
	/**
	 * run the interception
	 * 
	 * @param context
	 *            - the {@link BeforeContext} that contains the appropriate information
	 */
	void run(BeforeContext<I, O> context) throws InterceptionException;

}
