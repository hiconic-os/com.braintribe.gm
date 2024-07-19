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
package com.braintribe.model.processing.meta.cmd.empty;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EnumType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.meta.GmEnumConstant;
import com.braintribe.model.meta.GmEnumType;
import com.braintribe.model.meta.data.ExplicitPredicate;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.meta.data.Predicate;
import com.braintribe.model.meta.info.GmEntityTypeInfo;
import com.braintribe.model.meta.info.GmPropertyInfo;
import com.braintribe.model.processing.meta.cmd.builders.ConstantMdResolver;
import com.braintribe.model.processing.meta.cmd.builders.EntityMdResolver;
import com.braintribe.model.processing.meta.cmd.builders.EnumMdResolver;
import com.braintribe.model.processing.meta.cmd.builders.ModelMdResolver;
import com.braintribe.model.processing.meta.cmd.builders.PropertyMdResolver;
import com.braintribe.model.processing.meta.cmd.result.ModelMdResult;

public class EmptyModelMdResolver extends EmptyMdResolver<ModelMdResolver> implements ModelMdResolver {

	public static final EmptyModelMdResolver INSTANCE = new EmptyModelMdResolver();

	private EmptyModelMdResolver() {
	}

	@Override
	public boolean is(EntityType<? extends Predicate> predicateType) {
		boolean isExplicit = ExplicitPredicate.T.isAssignableFrom(predicateType);
		return !isExplicit; // if explicit, default is false
	}

	@Override
	public EntityMdResolver entity(GenericEntity genericEntity) {
		return EmptyEntityMdResolver.INSTANCE;
	}

	@Override
	public EntityMdResolver entityClass(Class<? extends GenericEntity> entityClass) {
		return EmptyEntityMdResolver.INSTANCE;
	}

	@Override
	public EntityMdResolver entityType(EntityType<? extends GenericEntity> entityType) {
		return EmptyEntityMdResolver.INSTANCE;
	}

	@Override
	public EntityMdResolver entityType(GmEntityTypeInfo gmEntityTypeInfo) {
		return EmptyEntityMdResolver.INSTANCE;
	}

	@Override
	public EntityMdResolver entityTypeSignature(String typeSignature) {
		return EmptyEntityMdResolver.INSTANCE;
	}

	@Override
	public ConstantMdResolver enumConstant(Enum<?> constant) {
		return EmptyConstantMdResolver.INSTANCE;
	}

	@Override
	public ConstantMdResolver enumConstant(GmEnumConstant constant) {
		return EmptyConstantMdResolver.INSTANCE;
	}

	@Override
	public EnumMdResolver enumClass(Class<? extends Enum<?>> entityClass) {
		return EmptyEnumMdResolver.INSTANCE;
	}

	@Override
	public EnumMdResolver enumType(EnumType<?> enumType) {
		return EmptyEnumMdResolver.INSTANCE;
	}

	@Override
	public EnumMdResolver enumType(GmEnumType gmEnumType) {
		return EmptyEnumMdResolver.INSTANCE;
	}

	@Override
	public EnumMdResolver enumTypeSignature(String typeSignature) {
		return EmptyEnumMdResolver.INSTANCE;
	}

	@Override
	public PropertyMdResolver property(GmPropertyInfo gmPropertyInfo) {
		return EmptyPropertyMdResolver.INSTANCE;
	}

	@Override
	public PropertyMdResolver property(Property property) {
		return EmptyPropertyMdResolver.INSTANCE;
	}

	@Override
	public final <M extends MetaData> ModelMdResult<M> meta(EntityType<M> metaDataType) {
		return EmptyMdResult.EmptyModelMdResult.singleton();
	}

}
