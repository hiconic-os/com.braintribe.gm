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
package com.braintribe.model.processing.securityservice.api.exceptions;

public class ExpiredSessionException extends InvalidSessionException {

	private static final long serialVersionUID = 445050490121572978L;

	public ExpiredSessionException() {
		super();
	}

	public ExpiredSessionException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExpiredSessionException(String message) {
		super(message);
	}

	public ExpiredSessionException(Throwable cause) {
		super(cause);
	}

}
