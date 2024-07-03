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
package com.braintribe.model.processing.cryptor.basic.cipher.key.codec;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import com.braintribe.codec.Codec;
import com.braintribe.codec.CodecException;
import com.braintribe.model.processing.crypto.token.loader.EncryptionTokenLoaderException;

/**
 * <p>
 * Provides {@link com.braintribe.codec.Codec}(s) capable of importing/exporting {@link PublicKey}(s) from/to byte
 * arrays.
 * 
 * @see com.braintribe.model.processing.crypto.token.KeyCodecProvider
 *
 */
public class PublicKeyCodecProvider extends AbstractKeyCodecProvider<PublicKey> {

	@Override
	public Codec<PublicKey, byte[]> getKeyCodec(String algorithm) throws EncryptionTokenLoaderException {
		return getKeyCodec(PublicKey.class, algorithm);
	}

	@Override
	protected Codec<PublicKey, byte[]> createKeyCodec(final String algorithm) {
		return new Codec<PublicKey, byte[]>() {

			@Override
			public byte[] encode(PublicKey value) {
				return value.getEncoded();
			}

			@Override
			public PublicKey decode(byte[] encodedValue) throws CodecException {
				try {
					X509EncodedKeySpec spec = new X509EncodedKeySpec(encodedValue);
					KeyFactory factory = KeyFactory.getInstance(algorithm);
					return factory.generatePublic(spec);
				} catch (Exception e) {
					throw asCodecException("Failed to decode public key", e);
				}
			}

			@Override
			public Class<PublicKey> getValueClass() {
				return PublicKey.class;
			}

		};
	}

}
