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
package com.braintribe.model.processing.aop.api.service;

import java.util.Set;

import com.braintribe.model.aopaccessapi.AccessAspectAfterInterceptorRequest;
import com.braintribe.model.aopaccessapi.AccessAspectAfterInterceptorResponse;
import com.braintribe.model.aopaccessapi.AccessAspectAroundInterceptorRequest;
import com.braintribe.model.aopaccessapi.AccessAspectAroundInterceptorResponse;
import com.braintribe.model.aopaccessapi.AccessAspectBeforeInterceptorRequest;
import com.braintribe.model.aopaccessapi.AccessAspectBeforeInterceptorResponse;
import com.braintribe.model.extensiondeployment.meta.AccessPointCut;
import com.braintribe.model.processing.aop.api.aspect.Advice;

/**
 * the interface for the handling service<br/>
 * there are two implementations of it,
 * <li>externalizing implementation: translates the calls into a dispatchable (rpc, mq) content - see MasterCartridge</li>
 * <li>internalizing implementation: translates the dispatchable content into calls - see CartridgeBase</li>
 * @author pit, dirk
 *
 */
public interface AccessAspectService {

	/**
	 * run the interceptors hooked to the before {@link Advice}
	 * @param request - the {@link AccessAspectBeforeInterceptorRequest} that contains the appropriate request
	 * @return - the {@link AccessAspectBeforeInterceptorResponse} that contains the respective response
	 */
	AccessAspectBeforeInterceptorResponse runBeforeInterceptor( AccessAspectBeforeInterceptorRequest request) throws AccessAspectServiceException;

	/**
	 * run the interceptors hooked to the around {@link Advice}
	 * @param request - the {@link AccessAspectAroundInterceptorRequest} that contains the appropriate request
	 * @return - the {@link AccessAspectAroundInterceptorResponse} that contains the respective response
	 */
	AccessAspectAroundInterceptorResponse runAroundInterceptor( AccessAspectAroundInterceptorRequest request) throws AccessAspectServiceException;

	/**
	 * run the interceptors hooked to the after {@link Advice}
	 * @param request - the {@link AccessAspectAfterInterceptorRequest} that contains the appropriate request
	 * @return - the {@link AccessAspectAfterInterceptorResponse} that contains the respective response
	 */

	AccessAspectAfterInterceptorResponse runAfterInterceptor( AccessAspectAfterInterceptorRequest request) throws AccessAspectServiceException;

	/**
	 * delivers the pointcuts from service.
	 * @return Set of pointcuts
	 */
	Set<AccessPointCut> getPointCuts() throws AccessAspectServiceException;

}
