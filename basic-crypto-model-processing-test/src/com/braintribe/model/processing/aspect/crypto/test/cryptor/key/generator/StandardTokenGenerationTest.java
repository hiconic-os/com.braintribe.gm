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
package com.braintribe.model.processing.aspect.crypto.test.cryptor.key.generator;

import java.security.KeyPair;

import javax.crypto.SecretKey;

import org.junit.Assert;
import org.junit.Test;

import com.braintribe.crypto.Cryptor;
import com.braintribe.model.crypto.configuration.encryption.AsymmetricEncryptionConfiguration;
import com.braintribe.model.crypto.configuration.encryption.SymmetricEncryptionConfiguration;
import com.braintribe.model.processing.aspect.crypto.test.TestBase;

public class StandardTokenGenerationTest extends TestBase {

	// ########################
	// ## .. RSA tests ..... ##
	// ########################

	@Test
	public void testRsa() throws Exception {
		testAsymmetricTokenGeneration("RSA");
	}

	// ########################
	// ## .. AES tests ..... ##
	// ########################

	@Test
	public void testAes() throws Exception {
		testSymmetricTokenGeneration("AES");
	}

	// ########################
	// ## .. DES tests ..... ##
	// ########################

	@Test
	public void testDes() throws Exception {
		testSymmetricTokenGeneration("DES");
	}

	// ###########################
	// ## .. DESede tests ..... ##
	// ###########################

	@Test
	public void testDesEde() throws Exception {
		testSymmetricTokenGeneration("DESede");
	}

	// ######################
	// ## .. Commons ..... ##
	// ######################

	protected void testAsymmetricTokenGeneration(String algorithm) throws Exception {

		AsymmetricEncryptionConfiguration config = getEncryptionConfiguration(algorithm);

		KeyPair keyPair = standardKeyPairGenerator.generate((com.braintribe.model.crypto.key.KeyPair) config.getAsymmetricEncryptionToken(), config.getProvider());

		Cryptor cryptor1 = cipherCryptorFactory.builder().keyPair(keyPair).mode(config.getMode()).padding(config.getPadding()).provider(config.getProvider()).build();
		Cryptor cryptor2 = cipherCryptorFactory.builder().keyPair(keyPair).mode(config.getMode()).padding(config.getPadding()).provider(config.getProvider()).build();

		Assert.assertNotEquals("Cryptor instances created based on " + KeyPair.class.getName() + "(s) must not be cached", cryptor1, cryptor2);

		testCryptors(cryptor1, cryptor2);

	}

	protected void testSymmetricTokenGeneration(String algorithm) throws Exception {

		SymmetricEncryptionConfiguration config = getEncryptionConfiguration(algorithm);

		SecretKey secretkey = standardSecretKeyGenerator.generate((com.braintribe.model.crypto.key.SecretKey) config.getSymmetricEncryptionToken(), config.getProvider());

		Cryptor cryptor1 = cipherCryptorFactory.builder().key(secretkey).mode(config.getMode()).padding(config.getPadding()).provider(config.getProvider()).build();
		Cryptor cryptor2 = cipherCryptorFactory.builder().key(secretkey).mode(config.getMode()).padding(config.getPadding()).provider(config.getProvider()).build();

		Assert.assertNotEquals("Cryptor instances created based on " + SecretKey.class.getName() + "(s) must not be cached", cryptor1, cryptor2);

		testCryptors(cryptor1, cryptor2);

	}

}
