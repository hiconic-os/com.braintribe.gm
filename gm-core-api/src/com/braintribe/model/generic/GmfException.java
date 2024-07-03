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
package com.braintribe.model.generic;

/**
 * This should be the super type of all custom GM {@link Exception}s.
 */
public class GmfException extends RuntimeException {

	private static final long serialVersionUID = -7605916119918787461L;

	public GmfException() {
		super();
	}

	public GmfException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public GmfException(String message, Throwable cause) {
		super(message, cause);
	}

	public GmfException(String message) {
		super(message);
	}

	public GmfException(Throwable cause) {
		super(cause);
	}

}
