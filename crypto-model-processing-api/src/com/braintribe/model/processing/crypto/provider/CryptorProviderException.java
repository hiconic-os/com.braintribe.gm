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
package com.braintribe.model.processing.crypto.provider;

public class CryptorProviderException extends Exception {

	private static final long serialVersionUID = 1L;

	public CryptorProviderException() {
		super();
	}

	public CryptorProviderException(String message, Throwable cause) {
		super(message, cause);
	}

	public CryptorProviderException(String message) {
		super(message);
	}

	public CryptorProviderException(Throwable cause) {
		super(cause);
	}

	/**
	 * <p>
	 * Builds a {@link CryptorProviderException} based on the given arguments.
	 * 
	 * @param message
	 *            The message of the resulting {@link CryptorProviderException}
	 * @param cause
	 *            The cause of the resulting {@link CryptorProviderException}
	 * @return A {@link CryptorProviderException} constructed based on the given arguments.
	 */
	public static CryptorProviderException wrap(String message, Throwable cause) {

		if (cause == null) {
			return new CryptorProviderException(message);
		}

		if (cause.getMessage() != null && !cause.getMessage().isEmpty()) {
			message += ": " + cause.getMessage();
		}

		return new CryptorProviderException(message, cause);

	}

}
