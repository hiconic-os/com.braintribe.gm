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
package com.braintribe.model.processing.crypto.token.loader;

import com.braintribe.model.crypto.token.EncryptionToken;

/**
 * <p>
 * Loads security tokens (e.g.: certificates, keys .. ) based on {@link EncryptionToken} instances.
 * 
 */
public interface EncryptionTokenLoader<I extends EncryptionToken, O> {

	/**
	 * <p>
	 * Loads a security token based on provided {@link EncryptionToken}.
	 * 
	 * @param encryptionToken
	 *            The {@link EncryptionToken} repesenting the security token to be loaded.
	 * @return A security token loaded based on provided {@link EncryptionToken}.
	 * @throws EncryptionTokenLoaderException
	 *             If the security token loading fails.
	 */
	O load(I encryptionToken) throws EncryptionTokenLoaderException;
	
}
