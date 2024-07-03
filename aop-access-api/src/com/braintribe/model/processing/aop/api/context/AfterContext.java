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

import com.braintribe.model.processing.aop.api.interceptor.AfterInterceptor;

/**
 * the {@link Context} interface for after advice, gets passed to the {@link AfterInterceptor}
 * 
 * @author dirk,pit
 *
 * @param <I>
 *            - the request type (or input)
 * @param <O>
 *            - the response type (or output)
 */
public interface AfterContext<I, O> extends Context<I> {

	/** skip subsequent interception */
	void skip();
	/**
	 * override response with the one given here
	 * 
	 * @param response
	 *            - the response to use.
	 */
	void overrideResponse(O response);

	/** Reads the existing response */
	O getResponse();

}
