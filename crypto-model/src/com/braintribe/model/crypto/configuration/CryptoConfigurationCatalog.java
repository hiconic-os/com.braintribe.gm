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
package com.braintribe.model.crypto.configuration;

import java.util.Map;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.meta.Description;
import com.braintribe.model.generic.annotation.meta.Name;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;


public interface CryptoConfigurationCatalog extends GenericEntity {

	EntityType<CryptoConfigurationCatalog> T = EntityTypes.T(CryptoConfigurationCatalog.class);

	String cryptoConfigurationMap = "cryptoConfigurationMap";

	@Name("Crypto Configuration Map")
	@Description("A set of Crypto Configurations, identified by a unique key.")
	Map<String, CryptoConfiguration> getCryptoConfigurationMap();
	void setCryptoConfigurationMap(Map<String, CryptoConfiguration> cryptoConfigurationMap);

}
