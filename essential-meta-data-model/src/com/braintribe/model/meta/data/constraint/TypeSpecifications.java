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
package com.braintribe.model.meta.data.constraint;

import java.util.Objects;
import java.util.Set;


import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.meta.GmType;


public interface TypeSpecifications extends TypeRestriction {

	EntityType<TypeSpecifications> T = EntityTypes.T(TypeSpecifications.class);

	Set<GmType> getTypes();
	void setTypes(Set<GmType> types);
	
	@Override
	default boolean isInstance(Object instance) {
		for (GmType type: Objects.requireNonNull(getTypes(), "types may not be null")) {
			if (type.reflectionType().isInstance(instance)) {
				return true;
			}
		}
		
		return false;
	}
}
