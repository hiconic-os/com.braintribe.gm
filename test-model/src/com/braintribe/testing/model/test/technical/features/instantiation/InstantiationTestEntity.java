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

import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * This entity has properties of uninstantiable types which, however, have instantiable (and uninstantiable) subtypes.
 * Clients are expected to allow for adding/creating all the instantiable subtypes but not the ones that cannot be
 * instantiated.
 *
 * @author michael.lafite
 */

public interface InstantiationTestEntity extends GenericEntity {

	EntityType<InstantiationTestEntity> T = EntityTypes.T(InstantiationTestEntity.class);

	UninstantiableSuperType getUninstantiableSuperType();
	void setUninstantiableSuperType(UninstantiableSuperType uninstantiableSuperType);

	Set<UninstantiableSubTypeC> getUninstantiableSubTypeCSet();
	void setUninstantiableSubTypeCSet(Set<UninstantiableSubTypeC> uninstantiableSubTypeCSet);
}
