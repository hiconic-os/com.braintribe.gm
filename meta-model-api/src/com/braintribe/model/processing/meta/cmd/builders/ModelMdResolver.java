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
package com.braintribe.model.processing.meta.cmd.builders;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EnumType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.meta.GmEnumConstant;
import com.braintribe.model.meta.GmEnumType;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.meta.info.GmEntityTypeInfo;
import com.braintribe.model.meta.info.GmPropertyInfo;
import com.braintribe.model.processing.meta.cmd.result.ModelMdResult;

/**
 * Resolver for meta data defined on a {@link GmMetaModel}.
 * 
 * @see MdResolver
 */
public interface ModelMdResolver extends MdResolver<ModelMdResolver> {

	EntityMdResolver entity(GenericEntity genericEntity);

	EntityMdResolver entityClass(Class<? extends GenericEntity> entityClass);

	EntityMdResolver entityType(EntityType<? extends GenericEntity> entityType);

	EntityMdResolver entityType(GmEntityTypeInfo gmEntityTypeInfo);

	EntityMdResolver entityTypeSignature(String typeSignature);

	ConstantMdResolver enumConstant(Enum<?> constant);

	ConstantMdResolver enumConstant(GmEnumConstant constant);

	EnumMdResolver enumClass(Class<? extends Enum<?>> entityClass);

	EnumMdResolver enumType(EnumType<?> enumType);

	EnumMdResolver enumType(GmEnumType gmEnumType);

	EnumMdResolver enumTypeSignature(String typeSignature);

	PropertyMdResolver property(GmPropertyInfo gmPropertyInfo);

	PropertyMdResolver property(Property property);

	@Override
	<M extends MetaData> ModelMdResult<M> meta(EntityType<M> metaDataType);

}
