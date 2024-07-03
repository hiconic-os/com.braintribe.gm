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

import java.security.KeyPair;

import javax.crypto.SecretKey;

import org.junit.Assert;
import org.junit.Test;

import com.braintribe.crypto.cipher.CipherCryptor;
import com.braintribe.model.crypto.configuration.encryption.AsymmetricEncryptionConfiguration;
import com.braintribe.model.crypto.configuration.encryption.EncryptionConfiguration;
import com.braintribe.model.crypto.configuration.encryption.SymmetricEncryptionConfiguration;
import com.braintribe.model.processing.cryptor.basic.cipher.BasicCipherCryptorFactory;

public class CipherCryptorFactoryTest extends CryptorFactoryTestBase {

	@Test
	public void testForAllSupportedConfigurations() throws Exception {
		testForAllSupportedConfigurations(cipherCryptorFactory);
	}
	
	@Test
	public void testForAllSupportedConfigurationsThroughBuilder() throws Exception {
		testForAllSupportedConfigurationsThroughBuilder(cipherCryptorFactory);
	}
	
	@Test
	public void testForAllSupportedConfigurationsThroughBuilderWithJcaToken() throws Exception {
		testForAllSupportedConfigurationsThroughBuilderWithJcaToken(cipherCryptorFactory);
	}

	private static void testForAllSupportedConfigurations(BasicCipherCryptorFactory factory) throws Exception {
		for (EncryptionConfiguration configuration : encryptionConfigurations.values()) {
			CipherCryptor cipherCryptor = factory.getCryptor(configuration);
			Assert.assertNotNull(cipherCryptor);
			assertCryptorType(cipherCryptor, configuration);
			testCryptor(cipherCryptor);
		}
	}

	private static void testForAllSupportedConfigurationsThroughBuilder(BasicCipherCryptorFactory factory) throws Exception {
		for (EncryptionConfiguration configuration : encryptionConfigurations.values()) {
			CipherCryptor cipherCryptor = factory.builder().configuration(configuration).build();
			Assert.assertNotNull(cipherCryptor);
			assertCryptorType(cipherCryptor, configuration);
			testCryptor(cipherCryptor);
		}
	}
	
	private static void testForAllSupportedConfigurationsThroughBuilderWithJcaToken(BasicCipherCryptorFactory factory) throws Exception {
		for (EncryptionConfiguration configuration : encryptionConfigurationsStandard.values()) {

			CipherCryptor cipherCryptorA = null;
			CipherCryptor cipherCryptorB = null;
			
			if (configuration instanceof SymmetricEncryptionConfiguration) {
				SymmetricEncryptionConfiguration config = (SymmetricEncryptionConfiguration)configuration;
				SecretKey secretKey = standardSecretKeyGenerator.generate((com.braintribe.model.crypto.key.SecretKey) config.getSymmetricEncryptionToken(), config.getProvider());
				cipherCryptorA = factory.builder().key(secretKey).mode(config.getMode()).padding(config.getPadding()).provider(config.getProvider()).build();
				cipherCryptorB = factory.builder().key(secretKey).mode(config.getMode()).padding(config.getPadding()).provider(config.getProvider()).build();
			} else {
				AsymmetricEncryptionConfiguration config = (AsymmetricEncryptionConfiguration)configuration;
				KeyPair keyPair = standardKeyPairGenerator.generate((com.braintribe.model.crypto.key.KeyPair) config.getAsymmetricEncryptionToken(), config.getProvider());
				cipherCryptorA = factory.builder().keyPair(keyPair).mode(config.getMode()).padding(config.getPadding()).provider(config.getProvider()).build();
				cipherCryptorB = factory.builder().keyPair(keyPair).mode(config.getMode()).padding(config.getPadding()).provider(config.getProvider()).build();
			}

			Assert.assertNotNull(cipherCryptorA);
			Assert.assertNotNull(cipherCryptorB);
			assertCryptorType(cipherCryptorA, configuration);
			assertCryptorType(cipherCryptorB, configuration);

			testCryptors(cipherCryptorA, cipherCryptorB);
			
		}
	}

}
