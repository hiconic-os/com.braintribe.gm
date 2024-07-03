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

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import com.braintribe.crypto.Cryptor;
import com.braintribe.crypto.Decryptor;
import com.braintribe.crypto.Encryptor;
import com.braintribe.model.crypto.configuration.encryption.EncryptionConfiguration;
import com.braintribe.model.processing.aspect.crypto.test.TestBase;

public class CipherCryptorProfilingTest extends TestBase {

	private static final byte[] symmetricEncryptionTestData = generateTestData(1024 * 256);
	private static final byte[] asymmetricEncryptionTestData = generateTestData(244);

	@Test
	public void testAesEncryption() throws Exception {
		EncryptionConfiguration config = getEncryptionConfiguration("AES", "hex");
		testStressedRoundtrip(config, symmetricEncryptionTestData, 50, false, true);
	}

	@Test
	public void testAesStreamedEncryption() throws Exception {
		EncryptionConfiguration config = getEncryptionConfiguration("AES", "hex");
		testStressedRoundtrip(config, symmetricEncryptionTestData, 50, true, true);
	}

	@Test
	public void testDesEncryption() throws Exception {
		EncryptionConfiguration config = getEncryptionConfiguration("DES", "hex");
		testStressedRoundtrip(config, symmetricEncryptionTestData, 50, false, true);
	}

	@Test
	public void testDesStreamedEncryption() throws Exception {
		EncryptionConfiguration config = getEncryptionConfiguration("DES", "hex");
		testStressedRoundtrip(config, symmetricEncryptionTestData, 50, true, true);
	}

	@Test
	public void testDesEdeEncryption() throws Exception {
		EncryptionConfiguration config = getEncryptionConfiguration("DESede", "hex");
		testStressedRoundtrip(config, symmetricEncryptionTestData, 50, false, true);
	}

	@Test
	public void testDesEdeStreamedEncryption() throws Exception {
		EncryptionConfiguration config = getEncryptionConfiguration("DESede", "hex");
		testStressedRoundtrip(config, symmetricEncryptionTestData, 50, true, true);
	}

	@Test
	public void testRsaEncryption() throws Exception {
		EncryptionConfiguration config = getEncryptionConfiguration("RSA", "hex");
		testStressedRoundtrip(config, asymmetricEncryptionTestData, 50, false, true);
	}

	@Test
	public void testRsaStreamedEncryption() throws Exception {
		EncryptionConfiguration config = getEncryptionConfiguration("RSA", "hex");
		testStressedRoundtrip(config, asymmetricEncryptionTestData, 50, true, true);
	}

	protected void testStressedRoundtrip(EncryptionConfiguration config, byte[] testData, int runs, boolean streaming, boolean assertData) throws Exception {

		Cryptor cipherCryptor = cipherCryptorFactory.getCryptor(config);

		long n = System.currentTimeMillis();
		for (int i = runs; i > 0; i--) {
			if (streaming) {
				byte[] encryptedData = encryptStreaming((Encryptor) cipherCryptor, testData);
				byte[] decryptedData = decryptStreaming((Decryptor) cipherCryptor, encryptedData);
				if (assertData && (i == runs || i == 1)) {
					Assert.assertArrayEquals("Decryption unexpected for " + config.getAlgorithm(), testData, decryptedData);
				}
			} else {
				byte[] encryptedData = ((Encryptor) cipherCryptor).encrypt(testData).result().asBytes();
				byte[] decryptedData = ((Decryptor) cipherCryptor).decrypt(encryptedData).result().asBytes();
				if (assertData && (i == runs || i == 1)) {
					Assert.assertArrayEquals("Decryption unexpected for " + config.getAlgorithm(), testData, decryptedData);
				}
			}
		}

		n = System.currentTimeMillis() - n;
		System.out.println("Ran " + runs + " roundtrips of " + config.getAlgorithm() + " encryption of " + testData.length + " bytes in " + n + " ms" + (streaming ? ", using streaming" : ""));

	}

	protected static byte[] generateTestData(int size) {
		byte[] testData = new byte[size];
		new Random().nextBytes(testData);
		return testData;
	}

}
