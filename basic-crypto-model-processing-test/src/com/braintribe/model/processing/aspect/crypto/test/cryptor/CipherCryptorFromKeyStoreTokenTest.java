// ============================================================================
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
// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
// ============================================================================
package com.braintribe.model.processing.aspect.crypto.test.cryptor;

import org.junit.Test;

import com.braintribe.model.crypto.configuration.encryption.EncryptionConfiguration;
import com.braintribe.model.processing.aspect.crypto.test.TestBase;
import com.braintribe.model.processing.aspect.crypto.test.commons.TestDataProvider;
import com.braintribe.crypto.Cryptor;

public class CipherCryptorFromKeyStoreTokenTest extends TestBase {

	// ########################
	// ## .. AES tests ..... ##
	// ########################

	@Test
	public void testHexadecimalEncodedAesKeyEncryptingHexadecimalString() throws Exception {
		EncryptionConfiguration config = getEncryptionConfiguration("AES","keystore");
		Cryptor cipherCryptor = cipherCryptorFactory.getCryptor(config);
		testStringCrypting(cipherCryptor, "AES", Cryptor.Encoding.hex, false, TestDataProvider.inputAString);
	}

	@Test
	public void testHexadecimalEncodedAesKeyEncryptingBase64String() throws Exception {
		EncryptionConfiguration config = getEncryptionConfiguration("AES","keystore");
		Cryptor cipherCryptor = cipherCryptorFactory.getCryptor(config);
		testStringCrypting(cipherCryptor, "AES", Cryptor.Encoding.base64, false, TestDataProvider.inputAString);
	}

	@Test
	public void testHexadecimalEncodedAesKeyEncryptingByteArray() throws Exception {
		EncryptionConfiguration config = getEncryptionConfiguration("AES","keystore");
		Cryptor cipherCryptor = cipherCryptorFactory.getCryptor(config);
		testBytesCrypting(cipherCryptor, "AES", false, TestDataProvider.inputA);
	}


	// ########################
	// ## .. DES tests ..... ##
	// ########################

	@Test
	public void testHexadecimalEncodedDesKeyEncryptingHexadecimalString() throws Exception {
		EncryptionConfiguration config = getEncryptionConfiguration("DES","keystore");
		Cryptor cipherCryptor = cipherCryptorFactory.getCryptor(config);
		testStringCrypting(cipherCryptor, "DES", Cryptor.Encoding.hex, false, TestDataProvider.inputAString);
	}

	@Test
	public void testHexadecimalEncodedDesKeyEncryptingBase64String() throws Exception {
		EncryptionConfiguration config = getEncryptionConfiguration("DES","keystore");
		Cryptor cipherCryptor = cipherCryptorFactory.getCryptor(config);
		testStringCrypting(cipherCryptor, "DES", Cryptor.Encoding.base64, false, TestDataProvider.inputAString);
	}

	@Test
	public void testHexadecimalEncodedDesKeyEncryptingByteArray() throws Exception {
		EncryptionConfiguration config = getEncryptionConfiguration("DES","keystore");
		Cryptor cipherCryptor = cipherCryptorFactory.getCryptor(config);
		testBytesCrypting(cipherCryptor, "DES", false, TestDataProvider.inputA);
	}


	// ########################
	// ## .. DESede tests .. ##
	// ########################

	@Test
	public void testHexadecimalEncodedDesEdeKeyEncryptingHexadecimalString() throws Exception {
		EncryptionConfiguration config = getEncryptionConfiguration("DESede","keystore");
		Cryptor cipherCryptor = cipherCryptorFactory.getCryptor(config);
		testStringCrypting(cipherCryptor, "DESede", Cryptor.Encoding.hex, false, TestDataProvider.inputAString);
	}

	@Test
	public void testHexadecimalEncodedDesEdeKeyEncryptingBase64String() throws Exception {
		EncryptionConfiguration config = getEncryptionConfiguration("DESede","keystore");
		Cryptor cipherCryptor = cipherCryptorFactory.getCryptor(config);
		testStringCrypting(cipherCryptor, "DESede", Cryptor.Encoding.base64, false, TestDataProvider.inputAString);
	}

	@Test
	public void testHexadecimalEncodedDesEdeKeyEncryptingByteArray() throws Exception {
		EncryptionConfiguration config = getEncryptionConfiguration("DESede","keystore");
		Cryptor cipherCryptor = cipherCryptorFactory.getCryptor(config);
		testBytesCrypting(cipherCryptor, "DESede", false, TestDataProvider.inputA);
	}
	
	// ########################
	// ## .. AES tests ..... ##
	// ########################

	@Test
	public void testHexadecimalEncodedRsaKeyPairEncryptingHexadecimalString() throws Exception {
		EncryptionConfiguration config = getEncryptionConfiguration("RSA","keystore");
		Cryptor cipherCryptor = cipherCryptorFactory.getCryptor(config);
		testStringCrypting(cipherCryptor, "RSA", Cryptor.Encoding.hex, false, TestDataProvider.inputAString);
	}

	@Test
	public void testHexadecimalEncodedRsaKeyPairEncryptingBase64String() throws Exception {
		EncryptionConfiguration config = getEncryptionConfiguration("RSA","keystore");
		Cryptor cipherCryptor = cipherCryptorFactory.getCryptor(config);
		testStringCrypting(cipherCryptor, "RSA", Cryptor.Encoding.base64, false, TestDataProvider.inputAString);
	}

	@Test
	public void testHexadecimalEncodedRsaKeyPairEncryptingByteArray() throws Exception {
		EncryptionConfiguration config = getEncryptionConfiguration("RSA","keystore");
		Cryptor cipherCryptor = cipherCryptorFactory.getCryptor(config);
		testBytesCrypting(cipherCryptor, "RSA", false, TestDataProvider.inputA);
	}


}
