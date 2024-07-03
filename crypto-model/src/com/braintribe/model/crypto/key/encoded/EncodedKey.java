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
package com.braintribe.model.crypto.key.encoded;

import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.annotation.meta.Description;
import com.braintribe.model.generic.annotation.meta.Name;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

import com.braintribe.model.crypto.key.Key;

@Abstract
public interface EncodedKey extends Key {

	EntityType<EncodedKey> T = EntityTypes.T(EncodedKey.class);

	static final String encodedKey = "encodedKey";
	static final String encodingFormat = "encodingFormat";
	static final String encodingStringFormat = "encodingStringFormat";

	@Name("Encoded Key")
	@Description("A String-representation of the key.")
	String getEncodedKey();
	void setEncodedKey(String encodedKey);

	@Name("Encoding Format")
	@Description("Specifies the format of the String-representation of the key (e.g., pkcs#8, x509).")
	KeyEncodingFormat getEncodingFormat();
	void setEncodingFormat(KeyEncodingFormat encodingFormat);

	@Name("Encoding String Format")
	@Description("Specifies the String-encoding of the key (e.g., base64, hex).")
	KeyEncodingStringFormat getEncodingStringFormat();
	void setEncodingStringFormat(KeyEncodingStringFormat keyEncodingStringFormat);

}
