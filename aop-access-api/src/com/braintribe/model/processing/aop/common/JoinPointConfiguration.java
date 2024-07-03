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

import static com.braintribe.utils.lcd.CollectionTools2.newList;

import java.util.List;

import com.braintribe.model.processing.aop.api.interceptor.AfterInterceptor;
import com.braintribe.model.processing.aop.api.interceptor.AroundInterceptor;
import com.braintribe.model.processing.aop.api.interceptor.BeforeInterceptor;
import com.braintribe.model.processing.aop.api.interceptor.Interceptor;

/**
 * a little container for the different interceptors 
 * 
 * @author pit, dirk
 */
public class JoinPointConfiguration {

	public List<BeforeInterceptor<?, ?>> beforeInterceptors = newList();
	public List<AroundInterceptor<?, ?>> aroundInterceptors = newList();
	public List<AfterInterceptor<?, ?>> afterInterceptors = newList();

	/** Convenience to make handling this class easier. */
	public static <E extends Interceptor> List<E> castInterceptors(List<? extends Interceptor> list) {
		return (List<E>) list;
	}


	
}
