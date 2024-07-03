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

import java.security.PrivateKey;

import com.braintribe.model.crypto.key.encoded.EncodedPrivateKey;
import com.braintribe.model.processing.crypto.token.loader.EncryptionTokenLoaderException;
import com.braintribe.model.processing.crypto.token.loader.PrivateKeyLoader;

/**
 * TODO: document.
 * 
 */
public class EncodedPrivateKeyLoader extends EncodedKeyLoader implements PrivateKeyLoader<EncodedPrivateKey, PrivateKey> {

	@Override
	public PrivateKey load(EncodedPrivateKey encodedPrivateKey) throws EncryptionTokenLoaderException {

		PrivateKey privateKey = loadKey(PrivateKey.class, encodedPrivateKey);

		return privateKey;

	}

}
