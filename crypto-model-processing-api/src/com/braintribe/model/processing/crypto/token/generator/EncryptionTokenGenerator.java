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
package com.braintribe.model.processing.crypto.token.generator;

import com.braintribe.model.crypto.token.EncryptionToken;

/**
 * <p>
 * Generates security tokens (e.g.: certificates, keys .. ) based on {@link EncryptionToken} instances.
 * 
 */
public interface EncryptionTokenGenerator<I extends EncryptionToken, O> {

	/**
	 * <p>
	 * Generates a security token based on provided {@link EncryptionToken}.
	 * 
	 * @param encryptionToken
	 *            The {@link EncryptionToken} to base the security token generation.
	 * @param provider
	 *            The name of the security provider to be used for generating the token.
	 * @return A security token generated based on provided {@link EncryptionToken}.
	 * @throws EncryptionTokenGeneratorException
	 *             If the security token generation fails.
	 */
	O generate(I encryptionToken, String provider) throws EncryptionTokenGeneratorException;

}
