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
package com.braintribe.model.processing.aspect.crypto.test.cryptor;

import org.junit.Assert;
import org.junit.Test;

import com.braintribe.crypto.Cryptor;
import com.braintribe.model.crypto.configuration.encryption.EncryptionConfiguration;
import com.braintribe.model.processing.aspect.crypto.test.TestBase;
import com.braintribe.model.processing.aspect.crypto.test.commons.TestDataProvider;
import com.braintribe.model.processing.cryptor.basic.cipher.BasicCipherCryptorFactory;

public class CipherCryptorTest extends TestBase {

	// ############################
	// ## .. General tests ..... ##
	// ############################

	@Test
	public void testStreamedCryptingForAllSupportedConfigurations() throws Exception {
		testStreamedCryptingForAllSupportedConfigurations(cipherCryptorFactory);
	}

	@Test
	public void testBytesCryptingForAllSupportedConfigurations() throws Exception {
		testBytesCryptingForAllSupportedConfigurations(cipherCryptorFactory);
	}

	@Test
	public void testStringCryptingForAllSupportedConfigurationsUsingBase64() throws Exception {
		testStringCryptingForAllSupportedConfigurations(cipherCryptorFactory, Cryptor.Encoding.base64);
	}

	@Test
	public void testStringCryptingForAllSupportedConfigurationsUsingHex() throws Exception {
		testStringCryptingForAllSupportedConfigurations(cipherCryptorFactory, Cryptor.Encoding.hex);
	}

	@Test
	public void testNulllCryptingForAllSupportedConfigurations() throws Exception {
		for (EncryptionConfiguration configuration : encryptionConfigurations.values()) {
			try {
				testNullCrypting(cipherCryptorFactory.getCryptor(configuration));
				Assert.fail("IllegalArgumentException should have been thrown for the null input");
			} catch (IllegalArgumentException expected) {
				// no action
			}
		}
	}

	protected static void testStreamedCryptingForAllSupportedConfigurations(BasicCipherCryptorFactory factory) throws Exception {
		for (EncryptionConfiguration configuration : encryptionConfigurations.values()) {
			testStreamedCrypting(factory.getCryptor(configuration), configuration.getAlgorithm(), false, TestDataProvider.inputA);
		}
	}

	protected static void testBytesCryptingForAllSupportedConfigurations(BasicCipherCryptorFactory factory) throws Exception {
		for (EncryptionConfiguration configuration : encryptionConfigurations.values()) {
			testBytesCrypting(factory.getCryptor(configuration), configuration.getAlgorithm(), false, TestDataProvider.inputA);
		}
	}

	protected static void testStringCryptingForAllSupportedConfigurations(BasicCipherCryptorFactory factory, Cryptor.Encoding stringEncoding) throws Exception {
		for (EncryptionConfiguration configuration : encryptionConfigurations.values()) {
			testStringCrypting(factory.getCryptor(configuration), configuration.getAlgorithm(), stringEncoding, false, TestDataProvider.inputAString);
		}
	}
}
