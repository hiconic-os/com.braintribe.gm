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
package com.braintribe.model.generic.reflection;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GmCoreApiInteropNamespaces;

import jsinterop.annotations.JsType;

/**
 * All the possible essential {@link CollectionType} instances in form of static fields. In this case this means that given types are parameterized
 * with Objects (i.e. {@link BaseType}).
 */
@JsType(namespace = GmCoreApiInteropNamespaces.reflection)
public interface Types extends EssentialTypes {

	static ListType listOf(GenericModelType elementType) {
		return GMF.getTypeReflection().getListType(elementType);
	}

	static SetType setOf(GenericModelType elementType) {
		return GMF.getTypeReflection().getSetType(elementType);
	}

	static MapType mapOf(GenericModelType keyType, GenericModelType valueType) {
		return GMF.getTypeReflection().getMapType(keyType, valueType);
	}

}
