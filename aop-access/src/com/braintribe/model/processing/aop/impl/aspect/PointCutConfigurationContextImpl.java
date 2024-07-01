// ============================================================================
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
// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
// ============================================================================
package com.braintribe.model.processing.aop.impl.aspect;

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
import com.braintribe.model.processing.aop.common.JoinPointConfiguration;

public class PointCutConfigurationContextImpl implements PointCutConfigurationContext{
	
	private final Map<AccessJoinPoint, JoinPointConfiguration> jointPointConfigurations = new HashMap<AccessJoinPoint, JoinPointConfiguration>();
	/**
	 * either finds or creates a {@link JoinPointConfiguration} for the joint point 
	 */
	public JoinPointConfiguration acquireJoinPointConfiguration(AccessJoinPoint accessJoinPoint) {
		JoinPointConfiguration jpc = jointPointConfigurations.get(accessJoinPoint);
		if (jpc == null) {
			jpc = new JoinPointConfiguration();
			jointPointConfigurations.put( accessJoinPoint, jpc);
		}
		return jpc;
	}
	
	@Override
	public void addPointCutBinding(PointCutBinding pointCutBinding) {
		AccessJoinPoint accessJoinPoint = pointCutBinding.getJoinPoint();
		JoinPointConfiguration jpc = acquireJoinPointConfiguration(accessJoinPoint);
		switch (pointCutBinding.getAdvice()) {
			case after:
				jpc.afterInterceptors.addAll(castInterceptors(jpc.afterInterceptors));
				break;
			case around:
				jpc.aroundInterceptors.addAll(castInterceptors(jpc.aroundInterceptors));
				break;
			case before:
				jpc.beforeInterceptors.addAll(castInterceptors(jpc.beforeInterceptors));
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


}
