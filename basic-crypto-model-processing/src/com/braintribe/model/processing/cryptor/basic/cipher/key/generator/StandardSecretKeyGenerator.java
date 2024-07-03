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
package com.braintribe.model.processing.cryptor.basic.cipher.key.generator;

import javax.crypto.SecretKey;

import com.braintribe.model.processing.crypto.token.generator.EncryptionTokenGeneratorException;
import com.braintribe.model.processing.crypto.token.generator.SecretKeyGenerator;

/**
 * <p>
 * A {@link SecretKeyGenerator} which generates {@link SecretKey}(s) based on
 * {@link com.braintribe.model.crypto.key.SecretKey}(s).
 * 
 */
public class StandardSecretKeyGenerator extends StandardKeyGenerator implements SecretKeyGenerator<com.braintribe.model.crypto.key.SecretKey, SecretKey> {

	@Override
	public SecretKey generate(com.braintribe.model.crypto.key.SecretKey encryptionToken, String provider) throws EncryptionTokenGeneratorException {
		SecretKey secretKey = generateSecretKey(encryptionToken.getKeyAlgorithm(), provider, encryptionToken);
		return secretKey;
	}

}
