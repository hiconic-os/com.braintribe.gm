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

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.SecretKeySpec;

import com.braintribe.codec.Codec;
import com.braintribe.codec.CodecException;
import com.braintribe.model.processing.crypto.token.loader.EncryptionTokenLoaderException;

/**
 * <p>
 * Provides {@link com.braintribe.codec.Codec}(s) capable of importing/exporting {@link SecretKey}(s) from/to byte
 * arrays.
 * 
 * @see com.braintribe.model.processing.crypto.token.KeyCodecProvider
 *
 */
public class SecretKeyCodecProvider extends AbstractKeyCodecProvider<SecretKey> {

	@Override
	public Codec<SecretKey, byte[]> getKeyCodec(String algorithm) throws EncryptionTokenLoaderException {
		return getKeyCodec(SecretKey.class, algorithm);
	}

	@Override
	protected Codec<SecretKey, byte[]> createKeyCodec(String algorithm) throws EncryptionTokenLoaderException {
		if (AESKeyCodec.algorithm.equals(algorithm)) {
			return new AESKeyCodec();
		} else if (DESedeKeyCodec.algorithm.equals(algorithm)) {
			return new DESedeKeyCodec();
		} else if (DESKeyCodec.algorithm.equals(algorithm)) {
			return new DESKeyCodec();
		} else {
			throw new EncryptionTokenLoaderException("Unsupported key algorithm: [ " + algorithm + " ]");
		}
	}

	protected static class AESKeyCodec implements Codec<SecretKey, byte[]> {

		protected static final String algorithm = "AES";

		@Override
		public byte[] encode(SecretKey value) {
			return value.getEncoded();
		}

		@Override
		public SecretKey decode(byte[] encodedValue) throws CodecException {
			try {
				return new SecretKeySpec(encodedValue, algorithm);
			} catch (Exception e) {
				throw asCodecException("Failed to decode secret key", e);
			}
		}

		@Override
		public Class<SecretKey> getValueClass() {
			return SecretKey.class;
		}

	}

	protected static class DESedeKeyCodec implements Codec<SecretKey, byte[]> {

		protected static final String algorithm = "DESede";

		@Override
		public byte[] encode(SecretKey value) throws CodecException {
			try {
				SecretKeyFactory factory = SecretKeyFactory.getInstance(algorithm);
				DESedeKeySpec spec = (DESedeKeySpec) factory.getKeySpec(value, javax.crypto.spec.DESedeKeySpec.class);
				return spec.getKey();
			} catch (Exception e) {
				throw asCodecException("Failed to encode " + algorithm + " key", e);
			}
		}

		@Override
		public SecretKey decode(byte[] encodedValue) throws CodecException {
			try {
				DESedeKeySpec spec = new DESedeKeySpec(encodedValue);
				SecretKeyFactory factory = SecretKeyFactory.getInstance(algorithm);
				return factory.generateSecret(spec);
			} catch (Exception e) {
				throw asCodecException("Failed to decode " + algorithm + " key", e);
			}
		}

		@Override
		public Class<SecretKey> getValueClass() {
			return SecretKey.class;
		}

	}

	protected static class DESKeyCodec implements Codec<SecretKey, byte[]> {

		protected static final String algorithm = "DES";

		@Override
		public byte[] encode(SecretKey value) throws CodecException {
			try {
				SecretKeyFactory factory = SecretKeyFactory.getInstance(algorithm);
				DESKeySpec spec = (DESKeySpec) factory.getKeySpec(value, javax.crypto.spec.DESKeySpec.class);
				return spec.getKey();
			} catch (Exception e) {
				throw asCodecException("Failed to encode " + algorithm + " key", e);
			}
		}

		@Override
		public SecretKey decode(byte[] encodedValue) throws CodecException {
			try {
				DESKeySpec spec = new DESKeySpec(encodedValue);
				SecretKeyFactory factory = SecretKeyFactory.getInstance(algorithm);
				return factory.generateSecret(spec);
			} catch (Exception e) {
				throw asCodecException("Failed to decode " + algorithm + " key", e);
			}
		}

		@Override
		public Class<SecretKey> getValueClass() {
			return SecretKey.class;
		}

	}

}
