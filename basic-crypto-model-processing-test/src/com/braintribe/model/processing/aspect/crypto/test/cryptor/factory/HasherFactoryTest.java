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
package com.braintribe.model.processing.aspect.crypto.test.cryptor.factory;

import org.junit.Assert;
import org.junit.Test;

import com.braintribe.crypto.CryptorException;
import com.braintribe.crypto.hash.Hasher;
import com.braintribe.model.crypto.configuration.hashing.HashingConfiguration;
import com.braintribe.model.processing.aspect.crypto.test.commons.TestDataProvider;
import com.braintribe.model.processing.cryptor.basic.hash.BasicHasherFactory;

public class HasherFactoryTest extends CryptorFactoryTestBase {

	@Test
	public void testForAllSupportedConfigurations() throws Exception {
		testForAllSupportedConfigurations(hasherFactory);
	}

	@Test(expected = CryptorException.class)
	public void testUnsupportedConfiguration() throws Exception {

		HashingConfiguration config = createHashingConfiguration("MD4", false);

		Hasher hasher = hasherFactory.newCryptor(config);

		hasher.encrypt(TestDataProvider.inputAString).result();

	}

	protected static void testForAllSupportedConfigurations(BasicHasherFactory factory) throws Exception {
		for (HashingConfiguration configuration : hashingConfigurations.values()) {
			Hasher hasher = factory.newCryptor(configuration);
			Assert.assertNotNull(hasher);
			assertCryptorType(hasher, configuration);
			testCryptor(hasher);
		}
	}

}
