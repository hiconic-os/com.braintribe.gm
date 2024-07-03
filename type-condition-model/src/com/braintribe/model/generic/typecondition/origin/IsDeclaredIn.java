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
package com.braintribe.model.generic.typecondition.origin;

import com.braintribe.model.generic.reflection.CustomType;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.GenericModelTypeReflection;
import com.braintribe.model.generic.reflection.Model;
import com.braintribe.model.generic.typecondition.TypeCondition;
import com.braintribe.model.generic.typecondition.TypeConditionType;
import com.braintribe.model.meta.GmCustomType;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.meta.GmType;

/**
 * Experimental. Not fully supported yet, use at your own risk!!!
 * 
 * Matches a {@link GmCustomType}, iff it is declared in the model of given name.
 */
public interface IsDeclaredIn extends TypeCondition {

	EntityType<IsDeclaredIn> T = EntityTypes.T(IsDeclaredIn.class);

	String getModelName();
	void setModelName(String modelName);

	@Override
	default boolean matches(GenericModelType type) {
		if (type.isCollection())
			return false;

		String modelName = getModelName();
		if (modelName == null)
			return false;

		if (type.isBase() || type.isSimple())
			return GenericModelTypeReflection.rootModelName.equals(modelName);

		CustomType ct = (CustomType) type;
		Model model = ct.getModel();
		if (model == null)
			throw new IllegalArgumentException("Cannot evaluate 'IsDeclaredIn' on a type that doesn't have a model: " + type.getTypeSignature());

		return modelName.equals(model.name());
	}

	@Override
	default boolean matches(GmType type) {
		String modelName = getModelName();
		if (modelName == null)
			return false;

		if (type.isGmCollection())
			return false;

		GmMetaModel m = type.getDeclaringModel();
		return m != null && modelName.equals(m.getName());

	}

	@Override
	default TypeConditionType tcType() {
		return TypeConditionType.isDeclaredIn;
	}

}
