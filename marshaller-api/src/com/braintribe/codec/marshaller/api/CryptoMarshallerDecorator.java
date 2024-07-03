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
package com.braintribe.codec.marshaller.api;

import java.security.Key;

import com.braintribe.crypto.Cryptor;

/**
 * <p>
 * Builder of {@link Marshaller} cryptography decorators.
 * 
 * <p>
 * Implementations must ensure that the returned decorated {@link Marshaller}(s) will encrypt data on
 * {@link Marshaller#marshall(java.io.OutputStream, Object)} and decrypt upon
 * {@link Marshaller#unmarshall(java.io.InputStream)}.
 * 
 *
 */
public interface CryptoMarshallerDecorator {

	/**
	 * <p>
	 * Decorates the given {@link Marshaller} to work with cryptography.
	 * 
	 * @param delegate
	 *            The {@link Marshaller} to be decorated
	 * @return A cryptography-enabled {@link Marshaller}
	 * @throws MarshallException
	 *             If the decoration fails.
	 */
	Marshaller decorate(Marshaller delegate) throws MarshallException;

	/**
	 * <p>
	 * Decorates the given {@link Marshaller} to work with cryptography. Using the given {@link Key}.
	 * 
	 * @param delegate
	 *            The {@link Marshaller} to be decorated
	 * @param key
	 *            The {@link Key} used for encrypting/decrypting data
	 * @return A cryptography-enabled {@link Marshaller}
	 * @throws MarshallException
	 *             If the decoration fails.
	 */
	Marshaller decorate(Marshaller delegate, Key key) throws MarshallException;

	/**
	 * <p>
	 * Decorates the given {@link Marshaller} to work with cryptography. Using the given {@link Key} and cipher
	 * transformation.
	 * 
	 * @param delegate
	 *            The {@link Marshaller} to be decorated
	 * @param key
	 *            The {@link Key} used for encrypting/decrypting data
	 * @param cipherTransformation
	 *            The transformation used for encrypting/decrypting data
	 * @return A cryptography-enabled {@link Marshaller}
	 * @throws MarshallException
	 *             If the decoration fails.
	 */
	Marshaller decorate(Marshaller delegate, Key key, String cipherTransformation) throws MarshallException;

	/**
	 * <p>
	 * Decorates the given {@link Marshaller} to work with cryptography. Using the given {@link Cryptor}.
	 * 
	 * @param delegate
	 *            The {@link Marshaller} to be decorated
	 * @param cryptor
	 *            The {@link Cryptor} used for encrypting/decrypting data
	 * @return A cryptography-enabled {@link Marshaller}
	 * @throws MarshallException
	 *             If the decoration fails.
	 */
	Marshaller decorate(Marshaller delegate, Cryptor cryptor) throws MarshallException;

}
