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
package com.braintribe.model.processing.aop.api.aspect;

import com.braintribe.model.processing.aop.api.interceptor.AfterInterceptor;
import com.braintribe.model.processing.aop.api.interceptor.AroundInterceptor;
import com.braintribe.model.processing.aop.api.interceptor.BeforeInterceptor;
import com.braintribe.model.processing.aop.api.interceptor.Interceptor;

/**
 * this the context to be used to add the different point cuts 
 * 
 * @author pit, dirk
 *
 */
public interface PointCutConfigurationContext {
	/**
	 * add a fully built binding 
	 * @param pointCutBinding - the fully qualified {@link PointCutBinding}
	 */
	void addPointCutBinding( PointCutBinding pointCutBinding);
	/**
	 * add a binding with the three parameters 
	 * @param joinPoint - the {@link AccessJoinPoint} the defines the method 
	 * @param advice - the {@link Advice} that defines when 
	 * @param interceptor - the {@link Interceptor} to be called 
	 */
	void addPointCutBinding( AccessJoinPoint joinPoint, Advice advice, Interceptor... interceptor);
	
	/**
	 * add a binding for the before {@link Advice}
	 * @param joinPoint - the {@link AccessJoinPoint} that the defines the method
	 * @param interceptor - one or more {@link BeforeInterceptor}s to be called 
	 */
	void addPointCutBinding( AccessJoinPoint joinPoint, BeforeInterceptor<?, ?>... interceptor);
	/**
	 * add a binding for the around {@link Advice}
	 * @param joinPoint - the {@link AccessJoinPoint} that the defines the method
	 * @param interceptor - one or more {@link AroundInterceptor}s to be called 
	 */
	void addPointCutBinding( AccessJoinPoint joinPoint, AroundInterceptor<?, ?>... interceptor);
	/**
	 * add a binding for the after {@link Advice}
	 * @param joinPoint - the {@link AccessJoinPoint} that the defines the method
	 * @param interceptor - one or more {@link AfterInterceptor}s to be called 
	 */
	void addPointCutBinding( AccessJoinPoint joinPoint, AfterInterceptor<?, ?>... interceptor);
	
}
