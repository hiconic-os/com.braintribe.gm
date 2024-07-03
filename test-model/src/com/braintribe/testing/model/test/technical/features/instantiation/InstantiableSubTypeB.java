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
package com.braintribe.testing.model.test.technical.features.instantiation;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * An entity type that extends {@link UninstantiableSuperType} and can be instantiated.
 *
 * @author michael.lafite
 */

public interface InstantiableSubTypeB extends UninstantiableSuperType {

	EntityType<InstantiableSubTypeB> T = EntityTypes.T(InstantiableSubTypeB.class);

	String getInstantiableSubTypeBProperty();
	void setInstantiableSubTypeBProperty(String instantiableSubTypeBProperty);

}
