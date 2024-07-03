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
package com.braintribe.model.processing.query.api.stringifier;

public class QueryStringifierRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 4838495512266634954L;

	public QueryStringifierRuntimeException() {
		super();
	}

	public QueryStringifierRuntimeException(final String message) {
		super(message);
	}

	public QueryStringifierRuntimeException(final Throwable cause) {
		super(cause);
	}

	public QueryStringifierRuntimeException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public QueryStringifierRuntimeException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
