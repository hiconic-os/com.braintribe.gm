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
package com.braintribe.gm.config.wire.contract;

import com.braintribe.gm.config.api.ModeledConfiguration;
import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.wire.api.space.WireSpace;

public interface ModeledConfigurationContract extends WireSpace {

	/**
	 * @see ModeledConfiguration#config(EntityType)
	 */
	default <C extends GenericEntity> C config(EntityType<C> configType) {
		return config().config(configType);
	}
	
	/**
	 * @see ModeledConfiguration#configReasoned(EntityType)
	 */
	default <C extends GenericEntity> Maybe<C> configReasoned(EntityType<C> configType) {
		return config().configReasoned(configType);
	}
	
	/**
	 * Returns the {@link ModeledConfiguration} on which modeled configurations can be retrieved. 
	 */
	ModeledConfiguration config();
}