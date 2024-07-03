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

import java.security.Key;
import java.security.KeyPair;

import com.braintribe.crypto.Cryptor;
import com.braintribe.model.crypto.configuration.encryption.EncryptionConfiguration;

public interface CipherCryptorFactory<T extends EncryptionConfiguration, E extends Cryptor> extends CryptorFactory<T, E> {

	@Override
	CipherCryptorBuilder<T, E> builder() throws CryptorFactoryException;

	interface CipherCryptorBuilder<T, E> extends CryptorFactory.CryptorBuilder<T, E> {

		@Override
		CipherCryptorBuilder<T, E> configuration(T cryptoConfiguration) throws CryptorFactoryException;

		CipherCryptorBuilder<T, E> key(Key key) throws CryptorFactoryException;

		CipherCryptorBuilder<T, E> keyPair(KeyPair keyPair) throws CryptorFactoryException;
		
		CipherCryptorBuilder<T, E> mode(String mode) throws CryptorFactoryException;
		
		CipherCryptorBuilder<T, E> padding(String padding) throws CryptorFactoryException;
		
		CipherCryptorBuilder<T, E> provider(String provider) throws CryptorFactoryException;

	}

}
