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

import java.security.KeyPair;

import com.braintribe.model.crypto.key.encoded.EncodedKeyPair;
import com.braintribe.model.processing.crypto.token.generator.EncryptionTokenGeneratorException;
import com.braintribe.model.processing.crypto.token.generator.KeyPairGenerator;

/**
 * <p>
 * A {@link KeyPairGenerator} which generates {@link KeyPair}(s) based on {@link EncodedKeyPair}(s).
 * 
 * <p>
 * This generator enriches the given {@link EncodedKeyPair} instances with the generated key's material.
 * 
 */
public class EncodedKeyPairGenerator extends EncodedKeyGenerator implements KeyPairGenerator<EncodedKeyPair> {

	@Override
	public KeyPair generate(EncodedKeyPair encryptionToken, String provider) throws EncryptionTokenGeneratorException {

		KeyPair keyPair = generateKeyPair(encryptionToken.getKeyAlgorithm(), provider, encryptionToken);

		if (encryptionToken.getPublicKey() != null && keyPair.getPublic() != null) {
			export(keyPair.getPublic(), encryptionToken.getPublicKey());
		}

		if (encryptionToken.getPrivateKey() != null && keyPair.getPrivate() != null) {
			export(keyPair.getPrivate(), encryptionToken.getPrivateKey());
		}

		return keyPair;
	}

}
