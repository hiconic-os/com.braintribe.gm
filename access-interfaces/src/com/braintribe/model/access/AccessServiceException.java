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
package com.braintribe.model.access;

/**
 * Exception thrown by the {@link AccessService}.
 * 
 * 
 */
public class AccessServiceException extends RuntimeException {

	private static final long serialVersionUID = -9217585078045912212L;

	public AccessServiceException(String message) {
		super(message);
	}

	public AccessServiceException(Throwable cause) {
		super(cause);
	}

	public AccessServiceException(String message, Throwable cause) {
		super(message, cause);
	}
}
