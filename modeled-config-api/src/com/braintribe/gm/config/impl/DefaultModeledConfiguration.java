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
package com.braintribe.gm.config.impl;

import com.braintribe.gm.config.api.ModeledConfiguration;
import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;

/**
 * This default implementation of {@link ModeledConfiguration} always resolves configuration entity by returning a default initialized instance of the given config type.
 * @author dirk.scheffler
 */
public class DefaultModeledConfiguration implements ModeledConfiguration {

	@Override
	public <C extends GenericEntity> C config(EntityType<C> configType) {
		return configType.create();
	}

	@Override
	public <C extends GenericEntity> Maybe<C> configReasoned(EntityType<C> configType) {
		return Maybe.complete(config(configType));
	}

}
