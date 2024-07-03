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

import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.annotation.meta.Description;
import com.braintribe.model.generic.annotation.meta.Name;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

import com.braintribe.model.generic.GenericEntity;

@Abstract
public interface HasKeyStoreEntry extends GenericEntity {

	EntityType<HasKeyStoreEntry> T = EntityTypes.T(HasKeyStoreEntry.class);

	static final String keyStore = "keyStore";
	static final String keyEntryAlias = "keyEntryAlias";
	static final String keyEntryPassword = "keyEntryPassword";
	
	@Name("Key Store")
	@Description("A key store specification.")
	KeyStore getKeyStore();
	void setKeyStore(KeyStore keyStore);

	@Name("Key Store Entry Alias")
	@Description("The alias of the key within the keystore.")
	String getKeyEntryAlias();
	void setKeyEntryAlias(String keyEntryAlias);

	@Name("Key Entry Alias Password")
	@Description("The password (if required) to access the key in the keystore.")
	String getKeyEntryPassword();
	void setKeyEntryPassword(String keyEntryPassword);

}
