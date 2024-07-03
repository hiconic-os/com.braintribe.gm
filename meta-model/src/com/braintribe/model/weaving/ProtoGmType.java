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
package com.braintribe.model.weaving;

import com.braintribe.model.meta.GmTypeKind;

public interface ProtoGmType extends ProtoGmModelElement {

	String getTypeSignature();
	void setTypeSignature(String typeSignature);

	/** TODO This makes no sense for a collection. */
	ProtoGmMetaModel getDeclaringModel();

	GmTypeKind typeKind();

	/** @return true iff this is an instance of {@link ProtoGmBaseType}. */
	default boolean isGmBase() {
		return false;
	}

	/** @return true iff this is an instance of {@link ProtoGmSimpleType}. */
	default boolean isGmSimple() {
		return false;
	}

	/** @return true iff this is an instance of {@link ProtoGmEntityType}. */
	default boolean isGmEntity() {
		return false;
	}

	/** @return true iff this is an instance of {@link ProtoGmEnumType}. */
	default boolean isGmEnum() {
		return false;
	}

	/** @return true iff this is an instance of {@link ProtoGmCollectionType}. */
	default boolean isGmCollection() {
		return false;
	}

	/** Tells if the type is either {@link ProtoGmEnumType} or {@link ProtoGmEntityType} */
	default boolean isGmCustom() {
		return false;
	}

	/** Tells if the type is either {@link ProtoGmSimpleType} or {@link ProtoGmEnumType} */
	default boolean isGmScalar() {
		return false;
	}

	/** Tells if the type is either {@link ProtoGmSimpleType} or {@link ProtoGmEnumType} */
	default boolean isGmNumber() {
		return false;
	}

}
