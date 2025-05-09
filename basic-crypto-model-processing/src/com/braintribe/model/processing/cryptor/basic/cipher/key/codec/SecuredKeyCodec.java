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

import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import com.braintribe.codec.Codec;
import com.braintribe.codec.CodecException;

/**
 * <p>
 * A codec of {@link java.security.Key} to {@code byte} array and vice-versa which wraps the key using password-based
 * encryption.
 * 
 * <p>
 * This codec is not concerned about key encoding and spec details (X.509 / PCKS8), and relies on the JCA wrap/unwrap
 * API.
 * 
 *
 */
public class SecuredKeyCodec<T extends Key> implements Codec<T, byte[]> {

	// constructor-required
	private String keyAlgorithm;
	private Class<T> keyType;
	private String pbeTransformation;
	private boolean cacheCiphers;

	// post-initialized
	private int keyTypeCode;
	private SecretKey pbeKey;
	private PBEParameterSpec pbeParams;
	private Cipher wrapper;
	private Cipher unwrapper;

	public SecuredKeyCodec(String keyAlgorithm, Class<T> keyType, String pbeTransformation, char[] password, byte[] salt, int iterations)
			throws Exception {
		super();

		Objects.requireNonNull(keyAlgorithm, "keyAlgorithm");
		Objects.requireNonNull(keyType, "keyType");
		Objects.requireNonNull(pbeTransformation, "pbeTransformation");
		Objects.requireNonNull(password, "password");
		Objects.requireNonNull(salt, "salt");

		this.keyAlgorithm = keyAlgorithm;
		this.keyType = keyType;
		this.pbeTransformation = pbeTransformation;

		if (keyType.isAssignableFrom(SecretKey.class)) {
			keyTypeCode = Cipher.SECRET_KEY;
		} else if (keyType.isAssignableFrom(PrivateKey.class)) {
			keyTypeCode = Cipher.PRIVATE_KEY;
		} else if (keyType.isAssignableFrom(PublicKey.class)) {
			keyTypeCode = Cipher.PUBLIC_KEY;
		} else {
			throw new IllegalArgumentException("Invalid key type: " + keyType);
		}

		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(pbeTransformation);
		PBEKeySpec keySpec = new PBEKeySpec(password);
		pbeKey = keyFactory.generateSecret(keySpec);

		pbeParams = new PBEParameterSpec(salt, iterations);

	}

	@Override
	public byte[] encode(T value) throws CodecException {

		byte[] encoded = null;

		try {
			if (cacheCiphers) {
				if (wrapper == null) {
					synchronized (this) {
						if (wrapper == null) {
							wrapper = createCipher(Cipher.WRAP_MODE);
						}
					}
				}
				synchronized (wrapper) {
					encoded = wrapper.wrap(value);
				}
			} else {
				Cipher localWrapper = createCipher(Cipher.WRAP_MODE);
				encoded = localWrapper.wrap(value);
			}
		} catch (Exception e) {
			throw new CodecException(e.getMessage(), e);
		}

		return encoded;

	}

	@Override
	public T decode(byte[] encodedValue) throws CodecException {

		T decoded = null;

		try {
			if (cacheCiphers) {
				if (unwrapper == null) {
					synchronized (this) {
						if (unwrapper == null) {
							unwrapper = createCipher(Cipher.UNWRAP_MODE);
						}
					}
				}
				synchronized (unwrapper) {
					decoded = (T) unwrapper.unwrap(encodedValue, keyAlgorithm, keyTypeCode);
				}
			} else {
				Cipher localUnwrapper = createCipher(Cipher.UNWRAP_MODE);
				decoded = (T) localUnwrapper.unwrap(encodedValue, keyAlgorithm, keyTypeCode);

			}
		} catch (Exception e) {
			throw new CodecException(e.getMessage(), e);
		}

		return decoded;

	}

	@Override
	public Class<T> getValueClass() {
		return keyType;
	}

	protected Cipher createCipher(int opmode) throws Exception {
		Cipher ciph = Cipher.getInstance(pbeTransformation);
		ciph.init(opmode, pbeKey, pbeParams);
		return ciph;
	}

}
