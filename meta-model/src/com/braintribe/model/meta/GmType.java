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

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.annotation.meta.Mandatory;
import com.braintribe.model.generic.annotation.meta.Unique;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.weaving.ProtoGmType;

@Abstract
public interface GmType extends ProtoGmType, GmModelElement {

	EntityType<GmType> T = EntityTypes.T(GmType.class);

	@Override
	@Unique
	@Mandatory
	String getTypeSignature();
	@Override
	void setTypeSignature(String typeSignature);

	/** TODO This makes no sense for a collection. */
	@Override
	GmMetaModel getDeclaringModel();
	void setDeclaringModel(GmMetaModel declaringModel);

	@Override
	GmTypeKind typeKind();

	/** @return true iff this is an instance of {@link GmBaseType}. */
	@Override
	default boolean isGmBase() {
		return false;
	}

	/** @return true iff this is an instance of {@link GmSimpleType}. */
	@Override
	default boolean isGmSimple() {
		return false;
	}

	/** @return true iff this is an instance of {@link GmEntityType}. */
	@Override
	default boolean isGmEntity() {
		return false;
	}

	/** @return true iff this is an instance of {@link GmEnumType}. */
	@Override
	default boolean isGmEnum() {
		return false;
	}

	/** @return true iff this is an instance of {@link GmCollectionType}. */
	@Override
	default boolean isGmCollection() {
		return false;
	}

	/** Tells if the type is either {@link GmEnumType} or {@link GmEntityType} */
	@Override
	default boolean isGmCustom() {
		return false;
	}

	/** Tells if the type is either {@link GmSimpleType} or {@link GmEnumType} */
	@Override
	default boolean isGmScalar() {
		return false;
	}

	/** Tells if the type is either {@link GmSimpleType} or {@link GmEnumType} */
	@Override
	default boolean isGmNumber() {
		return false;
	}

	default <T extends GenericModelType> T reflectionType() {
		return GMF.getTypeReflection().getType(getTypeSignature());
	}
}
