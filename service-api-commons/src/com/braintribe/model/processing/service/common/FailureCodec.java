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

import java.util.IdentityHashMap;
import java.util.Map;

import com.braintribe.codec.Codec;
import com.braintribe.codec.CodecException;
import com.braintribe.common.ExceptionBuilder;
import com.braintribe.common.lcd.StackTraceCodec;
import com.braintribe.model.processing.service.api.ServiceProcessorNotificationException;
import com.braintribe.model.service.api.ServiceRequest;
import com.braintribe.model.service.api.result.Failure;

/**
 * <p>
 * Codec for converting {@link Throwable} to {@link Failure} and vice-versa.
 */
public class FailureCodec implements Codec<Throwable, Failure> {

	public final static FailureCodec INSTANCE = new FailureCodec();

	private FailureCodec() {
	}

	@Override
	public Failure encode(Throwable throwable) {
		Failure failure = FailureConverter.INSTANCE.apply(throwable);
		return failure;
	}

	@Override
	public Throwable decode(Failure failure) throws CodecException {
		Throwable throwable = createThrowable(failure, new IdentityHashMap<Failure, Throwable>());
		return throwable;
	}

	@Override
	public Class<Throwable> getValueClass() {
		return Throwable.class;
	}

	private Throwable createThrowable(Failure failure, Map<Failure, Throwable> created) {

		if (failure == null) {
			return null;
		}

		Throwable exception = created.get(failure);

		if (exception != null) {
			return exception;
		}

		exception = ExceptionBuilder.createException(failure.getType(), failure.getMessage(), createThrowable(failure.getCause(), created));

		if (exception instanceof ServiceProcessorNotificationException) {
			ServiceRequest notification = failure.getNotification();
			if (notification!= null) {
				((ServiceProcessorNotificationException)exception).setNotification(notification);
			}
		}

		StackTraceElement[] stackTrace = StackTraceCodec.INSTANCE.decode(failure.getDetails());
		if (stackTrace != null) {
			exception.setStackTrace(stackTrace);
		}

		created.put(failure, exception);

		for (Failure suppressed : failure.getSuppressed()) {
			Throwable suppressedThrowable = createThrowable(suppressed, created);
			if (suppressedThrowable != null) {
				exception.addSuppressed(suppressedThrowable);
			}
		}

		return exception;
	}

}
