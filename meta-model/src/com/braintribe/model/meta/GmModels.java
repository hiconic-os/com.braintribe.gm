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
package com.braintribe.model.meta;

import java.util.Objects;

import com.braintribe.model.generic.reflection.Model;

/**
 * @author peter.gazdik
 */
public interface GmModels {

	/**
	 * Returns what the auto-generated globalId would be for given {@link GmModelElement}. This might differ from the actual globalId property value.
	 * <p>
	 * The point is to determine if given element has it's natural globalId, in which case in can be omitted, for example when generating sources.
	 */
	static String naturalGlobalId(GmModelElement element) {
		Objects.requireNonNull(element, "Model element cannot be null.");

		if (element instanceof GmType)
			return naturalTypeGlobalId((GmType) element);

		if (element instanceof GmProperty)
			return naturalPropertyGlobalId((GmProperty) element);

		if (element instanceof GmMetaModel)
			return naturalModelGlobalId((GmMetaModel) element);

		if (element instanceof GmEnumConstant)
			return naturalConstantGlobalId((GmEnumConstant) element);

		throw new IllegalArgumentException("Unsupported model element type: " + element.entityType().getShortName());
	}

	/** @see #naturalGlobalId(GmModelElement) */
	static String naturalModelGlobalId(GmMetaModel model) {
		return Model.modelGlobalId(model.getName());
	}

	/** @see #naturalGlobalId(GmModelElement) */
	static String naturalTypeGlobalId(GmType type) {
		return typeGlobalId(type.getTypeSignature());
	}

	/** @see #naturalGlobalId(GmModelElement) */
	static String naturalPropertyGlobalId(GmProperty property) {
		return propertyGlobalId(property.getDeclaringType().getTypeSignature(), property.getName());
	}

	/** @see #naturalGlobalId(GmModelElement) */
	static String naturalConstantGlobalId(GmEnumConstant constant) {
		return constantGlobalId(constant.getDeclaringType().getTypeSignature(), constant.getName());
	}

	static String typeGlobalId(String typeSignature) {
		return "type:" + typeSignature;
	}

	static String propertyGlobalId(String typeSignature, String propertyName) {
		return "property:" + typeSignature + "/" + propertyName;
	}

	static String constantGlobalId(String enumTypeSignature, String constantName) {
		return "enum:" + enumTypeSignature + "/" + constantName;
	}

	static String propertyOverrideGlobalId(String typeSignature, String propertyName) {
		return "propertyOverride:" + typeSignature + "/" + propertyName;
	}

}
