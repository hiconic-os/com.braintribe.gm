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


import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.meta.GmType;
import com.braintribe.model.meta.data.ModelSkeletonCompatible;


public interface TypeSpecification extends TypeRestriction, ModelSkeletonCompatible {

	EntityType<TypeSpecification> T = EntityTypes.T(TypeSpecification.class);

	// NOTE this might not exist, so if needed, create it 
	String STRING_TYPE_SPECIFICATION_GLOBAL_ID = "typeSpecification:string";
	
	GmType getType();
	void setType(GmType type);
	
	@Override
	default boolean isInstance(Object instance) {
		return Objects.requireNonNull(getType(), "type property may not be null").reflectionType().isInstance(instance);
	}
}
