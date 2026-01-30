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
package com.braintribe.model.meta.data.mapping;

import com.braintribe.model.descriptive.HasName;
import com.braintribe.model.generic.annotation.Initializer;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.meta.data.ModelSkeletonCompatible;
import com.braintribe.model.meta.data.UniversalMetaData;
import com.braintribe.model.meta.data.prompt.Name;

/**
 * Specifies alternative names for types, properties, and enum constants.
 * <p>
 * It differs from {@link Name} MD in two ways. Each element has at most one Name, but can have multiple Aliases, and Name is meant to be used for
 * output, while aliases are input.
 */
public interface Alias extends UniversalMetaData, HasName, ModelSkeletonCompatible {

	EntityType<Alias> T = EntityTypes.T(Alias.class);

	@Override
	@Initializer("true")
	boolean getInherited();

	default Alias name(String name) {
		setName(name);
		return this;
	}

	static Alias create(String name) {
		return T.create().name(name);
	}
}
