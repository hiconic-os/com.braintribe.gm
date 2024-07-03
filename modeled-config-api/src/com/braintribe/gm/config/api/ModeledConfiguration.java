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
package com.braintribe.gm.config.api;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.ReasonException;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;

/**
 * ModelledConfiguration allows to access configuration data that is defined by a root entity type. It may come from some persistence defined in a runtime system or from memory.
 * The fact that the configuration is solely given by modeled data supports generic serialization/persistence.
 * @author dirk.scheffler
 */
public interface ModeledConfiguration {
	/**
	 *	Returns a configuration for the given Type or throws a ReasonException in case that the configuration could not be retrieved.
	 *	If an explicit configuration cannot be found a default initialized instance of the configType will be returned. 
	 */
	<C extends GenericEntity> C config(EntityType<C> configType) throws ReasonException;
	
	/**
	 *	Returns a configuration for the given type or a reason why the configuration could not be retrieved.
	 *	If an explicit configuration cannot be found a default initialized instance of the configType will be returned. 
	 */
	<C extends GenericEntity> Maybe<C> configReasoned(EntityType<C> configType);
}
