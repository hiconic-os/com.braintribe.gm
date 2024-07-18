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

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.model.processing.service.api.ReasonedServiceProcessor;
import com.braintribe.model.processing.service.api.ServiceRequestContext;
import com.braintribe.model.service.api.ServiceRequest;

public class ReasonedMethodHandleServiceProcessor<P extends ServiceRequest, R> implements ReasonedServiceProcessor<P, R> {

	private final MethodHandle methodHandle;
	private SignatureType signatureType;

	public ReasonedMethodHandleServiceProcessor(MethodHandle methodHandle, SignatureType signatureType) {
		this.methodHandle = methodHandle;
		this.signatureType = signatureType;
	}

	@Override
	public Maybe<? extends R> processReasoned(ServiceRequestContext context, P request) {
		try {
			switch (signatureType) {
			case REQUEST: return (Maybe<R>) methodHandle.invoke(request);
			case CONTEXT_REQUEST: return (Maybe<R>) methodHandle.invoke(context, request);
			case REQUEST_CONTEXT: return (Maybe<R>) methodHandle.invoke(request, context);
			default:
				throw new UnsupportedOperationException("Unsupported signature type: " + signatureType);
			}
		} catch (RuntimeException | Error e) {
			throw e;

		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

}
