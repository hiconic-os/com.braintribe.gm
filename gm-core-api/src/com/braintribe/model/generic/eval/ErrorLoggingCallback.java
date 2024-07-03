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
package com.braintribe.model.generic.eval;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.logging.Logger;
import com.braintribe.processing.async.api.AsyncCallback;

/**
 * Callback which logs an error, which is either the {@link Throwable} passed to {@link #onFailure(Throwable)}, or the
 * {@link Maybe#whyUnsatisfied() reason} why the result is {@link Maybe#isUnsatisfied() unsatisfied}.
 * 
 * @author peter.gazdik
 */
public class ErrorLoggingCallback<R> implements AsyncCallback<Maybe<R>> {

	private static final Logger log = Logger.getLogger(ErrorLoggingCallback.class);

	private static final ErrorLoggingCallback<?> INSTANCE = new ErrorLoggingCallback<>();

	public static <R> ErrorLoggingCallback<R> instance() {
		return (ErrorLoggingCallback<R>) INSTANCE;
	}

	private ErrorLoggingCallback() {
	}

	@Override
	public void onSuccess(Maybe<R> result) {
		if (result.isUnsatisfied())
			log.error(result.whyUnsatisfied().stringify());
	}

	@Override
	public void onFailure(Throwable t) {
		log.error(t);
	}

}
