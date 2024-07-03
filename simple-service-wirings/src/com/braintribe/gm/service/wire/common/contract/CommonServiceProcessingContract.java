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
package com.braintribe.gm.service.wire.common.contract;

import com.braintribe.gm.service.impl.DomainIdServiceAroundProcessor;
import com.braintribe.model.processing.securityservice.commons.service.AuthorizingServiceInterceptor;
import com.braintribe.model.processing.service.api.ServiceInterceptorProcessor;
import com.braintribe.model.processing.service.common.ElapsedTimeMeasuringInterceptor;
import com.braintribe.model.processing.service.common.ThreadNamingInterceptor;
import com.braintribe.model.processing.service.common.eval.ConfigurableServiceRequestEvaluator;
import com.braintribe.wire.api.space.WireSpace;

public interface CommonServiceProcessingContract extends WireSpace {

	/* Interceptors with these identifications are registered automatically */
	String AUTH_INTERCEPTOR_ID = "auth";
	String DOMAIN_ID_INTERCEPTOR_ID = "domain-id";
	String THREAD_NAMING_INTERCEPTOR_ID = "thread-naming";
	String TIME_MEASURING_INTERCEPTOR_ID = "time-measuring";

	/**
	 * This evaluator is pre-configured with the following {@link ServiceInterceptorProcessor interceptors}:
	 * <ul>
	 * <li>{@value #AUTH_INTERCEPTOR_ID} - {@link AuthorizingServiceInterceptor}
	 * <li>{@value #DOMAIN_ID_INTERCEPTOR_ID} - {@link DomainIdServiceAroundProcessor}
	 * <li>{@value #THREAD_NAMING_INTERCEPTOR_ID} - {@link ThreadNamingInterceptor}
	 * <li>{@value #TIME_MEASURING_INTERCEPTOR_ID} - {@link ElapsedTimeMeasuringInterceptor}
	 * </ul>
	 */
	ConfigurableServiceRequestEvaluator evaluator();
}
