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
package com.braintribe.model.processing.crypto.factory;

import com.braintribe.crypto.Cryptor;
import com.braintribe.model.crypto.configuration.CryptoConfiguration;

/**
 * <p>
 * Factory of {@link Cryptor} instances.
 * 
 *
 * @param <T>
 *            The type of {@link CryptoConfiguration} the factory uses for creating {@link Cryptor} instances.
 * @param <E>
 *            The type of {@link Cryptor} the factory creates.
 */
public interface CryptorFactory<T extends CryptoConfiguration, E extends Cryptor> {

	E getCryptor(T cryptoConfiguration) throws CryptorFactoryException;

	<R extends Cryptor> R getCryptor(Class<R> requiredType, T cryptoConfiguration) throws CryptorFactoryException;

	CryptorBuilder<T, E> builder() throws CryptorFactoryException;

	interface CryptorBuilder<T, E> {

		CryptorBuilder<T, E> configuration(T cryptoConfiguration) throws CryptorFactoryException;

		E build() throws CryptorFactoryException;

		<R extends Cryptor> R build(Class<R> requiredType) throws CryptorFactoryException;

	}
	
}
