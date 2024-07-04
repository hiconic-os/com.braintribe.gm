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
package com.braintribe.model.generic.value;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.annotation.ForwardDeclaration;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.EnumType;
import com.braintribe.model.generic.value.type.DynamicallyTypedDescriptor;

@ForwardDeclaration("com.braintribe.gm:value-descriptor-model")
@SuppressWarnings("unusable-by-js")
public interface EnumReference extends DynamicallyTypedDescriptor {

	EntityType<EnumReference> T = EntityTypes.T(EnumReference.class);

	void setConstant(String constant);
	String getConstant();

	default EnumType<?> enumType() {
		return GMF.getTypeReflection().getType(getTypeSignature());
	}

	default Enum<?> constant() {
		return enumType().getEnumValue(getConstant());
	}

	static EnumReference of(Enum<?> enumConstant) {
		return create(enumConstant.getDeclaringClass().getName(), enumConstant.name());
	}

	static EnumReference create(String enumTypeSignature, String constantName) {
		EnumReference ref = EnumReference.T.create();
		ref.setTypeSignature(enumTypeSignature);
		ref.setConstant(constantName);

		return ref;
	}

}
