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
package com.braintribe.model.processing.service.impl;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Method;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.processing.service.api.MappingServiceProcessor;
import com.braintribe.model.processing.service.api.Service;
import com.braintribe.model.processing.service.api.ServiceRequestContext;
import com.braintribe.model.service.api.ServiceRequest;

public class MappingServiceProcessorDispatching<P extends ServiceRequest, R> implements DispatchConfigurator<P, R> {

	private final Object annotatedProcessor;

	static <P extends ServiceRequest, R> MappingServiceProcessorDispatching<P, R> create(MappingServiceProcessor<P, R> annotatedProcessor) {
		return new MappingServiceProcessorDispatching<>(annotatedProcessor);
	}

	public MappingServiceProcessorDispatching(Object annotatedProcessor) {
		this.annotatedProcessor = annotatedProcessor;
	}

	@Override
	public void configureDispatching(DispatchConfiguration<P, R> dispatching) {
		Lookup lookup = MethodHandles.lookup();

		for (Method m : annotatedProcessor.getClass().getMethods()) {
			Class<?> reqParamType;

			Service serviceAnnotation = m.getAnnotation(Service.class);
			if (serviceAnnotation == null)
				continue;

			Class<?>[] paramTypes = m.getParameterTypes();

			switch (paramTypes.length) {
				case 0:
					throw new UnsupportedOperationException("Service method " + m + " must have at least one parameter - (ServiceRequest)");
				case 2:
					if (paramTypes[1] != ServiceRequestContext.class)
						throw new UnsupportedOperationException(
								"Service method " + m + " with 2 parameters doesn't have ServiceRequestContext as its second parameter.");
					//$FALL-THROUGH$
				case 1:
					reqParamType = paramTypes[0];
					if (!ServiceRequest.class.isAssignableFrom(reqParamType))
						throw new UnsupportedOperationException("Service method " + m + " doesn't have ServiceRequest as its first parameter.");
					break;

				default:
					throw new UnsupportedOperationException(
							"Service method " + m + " cannot have more than 2 parameters - (ServiceRequest, ServiceRequestContext)");
			}

			try {
				EntityType<P> requestType = EntityTypes.T((Class<P>) reqParamType);

				boolean passContext = paramTypes.length == 2;

				MethodHandle methodHandle = lookup.unreflect(m);
				MethodHandle boundHandle = methodHandle.bindTo(annotatedProcessor);

				if (m.getReturnType() == Maybe.class)
					dispatching.registerReasoned(requestType, new ReasonedMethodHandleServiceProcessor<>(boundHandle, passContext));
				else
					dispatching.register(requestType, new MethodHandleServiceProcessor<>(boundHandle, passContext));

			} catch (Error e) {
				throw e;

			} catch (Throwable e) {
				throw new IllegalStateException("Unexpected Exception when converting @Service annotated method to ServiceProcessor", e);
			}
		}
	}

}
