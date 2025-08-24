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
package com.braintribe.model.processing.service.common;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.Objects;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.ReasonException;
import com.braintribe.model.service.api.result.Failure;
import com.braintribe.model.service.api.result.ResponseEnvelope;
import com.braintribe.model.service.api.result.ServiceResult;
import com.braintribe.model.service.api.result.Unsatisfied;

/**
 * <p>
 * This class consists exclusively of static methods that operate on or return {@link ServiceResult ServiceResults}.
 * 
 */
public class ServiceResults {

	private ServiceResults() {
	}

	public static <T> Maybe<T> evaluateReasoned(ServiceResult serviceResult) {
		Objects.requireNonNull(serviceResult, "serviceResult must not be null");

		switch (serviceResult.resultType()) {
		case success: return Maybe.complete((T)serviceResult.asResponse().getResult());
		case unsatisfied: return serviceResult.asUnsatisfied().toMaybe();
		case failure: 
			Throwable throwable = decodeFailure(serviceResult.asFailure());
			if (throwable instanceof RuntimeException) {
				throw (RuntimeException) throwable;
			} else if (throwable instanceof Error) {
				throw (Error) throwable;
			} else {
				throw new RuntimeException(throwable);
			}
		default:
			throw new IllegalStateException("unexpected result type " + serviceResult.resultType());
		}
	}
	
	public static <T> T evaluate(ServiceResult serviceResult) {

		Objects.requireNonNull(serviceResult, "serviceResult must not be null");

		ResponseEnvelope envelope = serviceResult.asResponse();

		if (envelope != null) {
			return (T) envelope.getResult();
		}

		Unsatisfied unsatisfied = serviceResult.asUnsatisfied();
		
		if (unsatisfied != null) {
			throw new ReasonException(unsatisfied.getWhy());
		}
		
		Failure failure = serviceResult.asFailure();

		if (failure != null) {
			Throwable throwable = decodeFailure(failure);
			if (throwable instanceof RuntimeException) {
				throw (RuntimeException) throwable;
			} else if (throwable instanceof Error) {
				throw (Error) throwable;
			} else {
				throw new UndeclaredThrowableException(throwable);
			}
		}
		
		throw new IllegalStateException("Unexpected " + ServiceResult.class.getName() + " type: " + serviceResult);

	}
	
	public static ServiceResult fromMaybe(Maybe<?> maybe) {
		return maybe.isSatisfied()? //
				envelope(maybe.get()): //
				Unsatisfied.from(maybe);
	}

	public static ResponseEnvelope envelope(Object result) {
		ResponseEnvelope envelope = ResponseEnvelope.T.create();
		envelope.setResult(result);
		return envelope;
	}

	public static Throwable decodeFailure(Failure failure) {
		Throwable throwable = FailureCodec.INSTANCE.decode(failure);
		return throwable;
	}

	public static Failure encodeFailure(Throwable throwable) {
		Failure failure = FailureCodec.INSTANCE.encode(throwable);
		return failure;
	}

}
