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
package com.braintribe.model.processing.aop.common;

import static com.braintribe.model.processing.aop.common.JoinPointConfiguration.castInterceptors;

import java.util.HashMap;
import java.util.Map;

import com.braintribe.model.processing.aop.api.aspect.AccessJoinPoint;
import com.braintribe.model.processing.aop.api.aspect.Advice;
import com.braintribe.model.processing.aop.api.aspect.PointCutBinding;
import com.braintribe.model.processing.aop.api.aspect.PointCutConfigurationContext;
import com.braintribe.model.processing.aop.api.interceptor.AfterInterceptor;
import com.braintribe.model.processing.aop.api.interceptor.AroundInterceptor;
import com.braintribe.model.processing.aop.api.interceptor.BeforeInterceptor;
import com.braintribe.model.processing.aop.api.interceptor.Interceptor;

/**
 * implementation for the {@link PointCutConfigurationContext}..
 * 
 * @author pit, dirk
 *
 */
public class PointCutConfigurationContextImpl implements PointCutConfigurationContext{
	
	private final Map<AccessJoinPoint, JoinPointConfiguration> joinPointConfigurations = new HashMap<AccessJoinPoint, JoinPointConfiguration>();
	/**
	 * either finds or creates a {@link JoinPointConfiguration} for the joint point 
	 */
	public JoinPointConfiguration acquireJoinPointConfiguration(AccessJoinPoint accessJoinPoint) {
		JoinPointConfiguration jpc = joinPointConfigurations.get(accessJoinPoint);
		if (jpc == null) {
			jpc = new JoinPointConfiguration();
			joinPointConfigurations.put( accessJoinPoint, jpc);
		}
		return jpc;
	}
	
	@Override
	public void addPointCutBinding(PointCutBinding pointCutBinding) {
		AccessJoinPoint accessJoinPoint = pointCutBinding.getJoinPoint();
		JoinPointConfiguration jpc = acquireJoinPointConfiguration(accessJoinPoint);
		switch (pointCutBinding.getAdvice()) {
			case after:
				jpc.afterInterceptors.addAll(castInterceptors(pointCutBinding.getInterceptors()));
				break;
			case around:
				jpc.aroundInterceptors.addAll(castInterceptors(pointCutBinding.getInterceptors()));
				break;
			case before:
				jpc.beforeInterceptors.addAll(castInterceptors(pointCutBinding.getInterceptors()));
				break;
			default:
				break;
		}
	}
	
	@Override
	public void addPointCutBinding(AccessJoinPoint joinPoint, Advice advice, Interceptor... interceptor) {
		PointCutBinding binding = new PointCutBinding( joinPoint, advice, interceptor);
		addPointCutBinding(binding);
	}

	@Override
	public void addPointCutBinding(AccessJoinPoint joinPoint, BeforeInterceptor<?, ?>... interceptor) {
		addPointCutBinding(joinPoint, Advice.before, interceptor);
	}

	@Override
	public void addPointCutBinding(AccessJoinPoint joinPoint, AroundInterceptor<?, ?>... interceptor) {
		addPointCutBinding(joinPoint, Advice.around, interceptor);
	}

	@Override
	public void addPointCutBinding(AccessJoinPoint joinPoint, AfterInterceptor<?, ?>... interceptor) {
		addPointCutBinding(joinPoint, Advice.after, interceptor);
	}

	public Map<AccessJoinPoint, JoinPointConfiguration> getJoinPointConfiguration() {
		return joinPointConfigurations;
	}
	
	
}
