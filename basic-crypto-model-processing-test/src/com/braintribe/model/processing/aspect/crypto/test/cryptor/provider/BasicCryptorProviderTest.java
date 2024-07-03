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
package com.braintribe.model.processing.aspect.crypto.test.cryptor.provider;

import org.junit.Test;

import com.braintribe.crypto.cipher.CipherCryptor;
import com.braintribe.crypto.hash.Hasher;
import com.braintribe.model.crypto.configuration.encryption.EncryptionConfiguration;
import com.braintribe.model.crypto.configuration.hashing.HashingConfiguration;
import com.braintribe.model.meta.data.crypto.PropertyCrypting;
import com.braintribe.model.processing.aspect.crypto.test.TestBase;

public class BasicCryptorProviderTest extends TestBase {

	@Test
	public void testForAllSupportedHashingConfigurations() throws Exception {
		for (HashingConfiguration configuration : hashingConfigurations.values()) {
			PropertyCrypting propertyCrypting = createPropertyCrypting(configuration);
			Hasher hasher = cryptorProvider.provideFor(Hasher.class, propertyCrypting);
			testCryptor(hasher);
		}
	}
	
	@Test
	public void testForAllSupportedEncryptionConfigurations() throws Exception {
		for (EncryptionConfiguration configuration : encryptionConfigurations.values()) {
			PropertyCrypting propertyCrypting = createPropertyCrypting(configuration);
			CipherCryptor cipherCryptor = cryptorProvider.provideFor(CipherCryptor.class, propertyCrypting);
			testCryptor(cipherCryptor);
		}
	}
	
	
}
