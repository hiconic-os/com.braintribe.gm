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
package com.braintribe.model.crypto.key.keystore;

import com.braintribe.model.crypto.common.HasProvider;
import com.braintribe.model.generic.annotation.meta.Description;
import com.braintribe.model.generic.annotation.meta.Name;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface KeyStore extends HasProvider {

	EntityType<KeyStore> T = EntityTypes.T(KeyStore.class);

	String filePath = "filePath";
	String systemProperty = "systemProperty";
	String type = "type";
	String password = "password";

	@Name("File Path")
	@Description("The path to the keystore.")
	String getFilePath();
	void setFilePath(String filePath);

	@Name("System Property")
	@Description("The system property that may contain the path to the keystore.")
	String getSystemProperty();
	void setSystemProperty(String systemProperty);

	@Name("Type")
	@Description("The type of the keystore. See https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#KeyStore for a list of supported types.")
	String getType();
	void setType(String type);

	@Name("Password")
	@Description("The password (if required) to access the keystore.")
	String getPassword();
	void setPassword(String password);

}
