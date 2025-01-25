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

import com.braintribe.common.lcd.Pair;
import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.processing.service.api.MappingServiceProcessor;
import com.braintribe.model.processing.service.api.Service;
import com.braintribe.model.processing.service.api.ServiceRequestContext;
import com.braintribe.model.service.api.ServiceRequest;

public class MappingServiceProcessorDispatching<P extends ServiceRequest, R> implements DispatchConfigurator<P, R> {

	private final MappingServiceProcessor<P, R> annotatedServiceProcessor;

	static <P extends ServiceRequest, R> MappingServiceProcessorDispatching<P, R> create(MappingServiceProcessor<P, R> persistenceProcessor) {
		return new MappingServiceProcessorDispatching<>(persistenceProcessor);
	}

	public MappingServiceProcessorDispatching(MappingServiceProcessor<P, R> persistenceProcessor) {
		this.annotatedServiceProcessor = persistenceProcessor;
	}

	@Override
	public void configureDispatching(DispatchConfiguration<P, R> dispatching) {
		Lookup lookup = MethodHandles.lookup();
		for (Method method : annotatedServiceProcessor.getClass().getMethods()) {
			Service serviceAnnotation = method.getAnnotation(Service.class);

			if (serviceAnnotation == null)
				continue;

			Pair<EntityType<P>, SignatureType> signature = checkSignature(method);
			EntityType<P> requestType = signature.first();
			SignatureType signatureType = signature.second();

			try {
				MethodHandle methodHandle = lookup.unreflect(method);
				MethodHandle boundHandle = methodHandle.bindTo(annotatedServiceProcessor);

				if (method.getReturnType() == Maybe.class)
					dispatching.registerReasoned(requestType, new ReasonedMethodHandleServiceProcessor<>(boundHandle, signatureType));
				else
					dispatching.register(requestType, new MethodHandleServiceProcessor<>(boundHandle, signatureType));

			} catch (Error e) {
				throw e;

			} catch (Throwable e) {
				throw new IllegalStateException("Unexpected Exception when converting @Service annotated method to ServiceProcessor", e);
			}
		}
	}

	private Pair<EntityType<P>, SignatureType> checkSignature(Method method) {
		Class<?>[] parameterTypes = method.getParameterTypes();

		final Class<?> requestParameterType;
		final SignatureType signatureType;

		switch (parameterTypes.length) {
			case 1:
				requestParameterType = parameterTypes[0];
				signatureType = SignatureType.REQUEST;
				break;

			case 2: {
				Class<?> arg1Type = parameterTypes[0];
				Class<?> arg2Type = parameterTypes[1];

				if (arg1Type == ServiceRequestContext.class) {
					requestParameterType = arg2Type;
					signatureType = SignatureType.CONTEXT_REQUEST;

				} else if (arg2Type == ServiceRequestContext.class) {
					requestParameterType = arg1Type;
					signatureType = SignatureType.REQUEST_CONTEXT;

				} else {
					throw new UnsupportedOperationException(
							"method " + method + " is annotated with @Service but does not comply with any of the supported parameter signatures");
				}

				break;
			}
			default:
				throw new UnsupportedOperationException(
						"method " + method + " is annotated with @Service but does not comply with any of the supported parameter signatures");
		}

		if (!ServiceRequest.class.isAssignableFrom(requestParameterType))
			throw new UnsupportedOperationException(
					"method " + method + " is annotated with @Service but does not comply with any of the supported parameter signatures");

		EntityType<P> requestType = EntityTypes.T((Class<P>) requestParameterType);

		return Pair.of(requestType, signatureType);
	}
}
