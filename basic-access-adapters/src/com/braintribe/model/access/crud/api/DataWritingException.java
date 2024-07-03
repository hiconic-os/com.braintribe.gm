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
package com.braintribe.model.access.crud.api;

/**
 * Unchecked exception type used by {@link DataWriter} implementations.
 * 
 * @author gunther.schenk
 */
public class DataWritingException extends CrudExpertException {

	private static final long serialVersionUID = 2942367211040801437L;

	public DataWritingException() {
	}

	public DataWritingException(String message) {
		super(message);
	}

	public DataWritingException(Throwable cause) {
		super(cause);
	}

	public DataWritingException(String message, Throwable cause) {
		super(message, cause);
	}

	public DataWritingException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
