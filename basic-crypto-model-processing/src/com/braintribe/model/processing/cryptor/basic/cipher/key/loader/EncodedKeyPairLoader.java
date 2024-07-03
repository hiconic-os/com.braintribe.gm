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
package com.braintribe.model.processing.cryptor.basic.cipher.key.loader;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import com.braintribe.model.crypto.key.encoded.EncodedKeyPair;
import com.braintribe.model.crypto.key.encoded.EncodedPrivateKey;
import com.braintribe.model.crypto.key.encoded.EncodedPublicKey;
import com.braintribe.model.processing.crypto.token.loader.EncryptionTokenLoaderException;
import com.braintribe.model.processing.crypto.token.loader.KeyPairLoader;

/**
 * TODO: document.
 * 
 */
public class EncodedKeyPairLoader extends EncodedKeyLoader implements KeyPairLoader<EncodedKeyPair> {

	@Override
	public KeyPair load(EncodedKeyPair encodedKeyPair) throws EncryptionTokenLoaderException {

		if (encodedKeyPair == null) {
			throw new IllegalArgumentException("Key pair argument cannot be null");
		}

		EncodedPublicKey encodedPublicKey = encodedKeyPair.getPublicKey();
		EncodedPrivateKey encodedPrivateKey = encodedKeyPair.getPrivateKey();

		if (encodedPublicKey == null && encodedPrivateKey == null) {
			throw new IllegalArgumentException("Key pair argument has neither public nor private key");
		}

		PublicKey publicKey = null;
		PrivateKey privateKey = null;

		if (encodedPublicKey != null) {
			publicKey = loadKey(PublicKey.class, encodedPublicKey);
		}

		if (encodedPrivateKey != null) {
			privateKey = loadKey(PrivateKey.class, encodedPrivateKey);
		}

		return new KeyPair(publicKey, privateKey);

	}

}
