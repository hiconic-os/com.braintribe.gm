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

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.meta.GmEntityType;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.meta.info.GmPropertyInfo;
import com.braintribe.model.processing.meta.cmd.context.MutableSelectorContext;
import com.braintribe.model.processing.meta.cmd.empty.EmptyPropertyMdResolver;
import com.braintribe.model.processing.meta.cmd.resolvers.EntityMdAggregator;
import com.braintribe.model.processing.meta.cmd.resolvers.PropertyMdAggregator;
import com.braintribe.model.processing.meta.cmd.result.EntityMdResult;
import com.braintribe.model.processing.meta.oracle.proxy.DynamicEntityTypeOracle;

/**
 * 
 */
public class EntityMdResolverImpl extends EntityRelatedMdResolverImpl<EntityMdResolver> implements EntityMdResolver {

	protected EntityMdAggregator entityMdAggregator;

	protected EntityMdResolverImpl(EntityMdAggregator entityMdAggregator, MutableSelectorContext selectorContext) {
		super(EntityMdResolverImpl.class, selectorContext, entityMdAggregator);

		this.entityMdAggregator = entityMdAggregator;
	}

	@Override
	public PropertyMdResolver property(GmPropertyInfo gmPropertyInfo) {
		return property(gmPropertyInfo.relatedProperty().getName());
	}

	@Override
	public PropertyMdResolver property(Property property) {
		return property(property.getName());
	}

	@Override
	public PropertyMdResolver property(String propertyName) {
		if (entityMdAggregator.getEntityOracle().findProperty(propertyName) == null) {
			return lenientOrThrowException(() -> EmptyPropertyMdResolver.INSTANCE, () -> "Property not found: " + propertyName);
		}

		// I am not sure how else to check if our entity type is a dynamic one
		if (entityMdAggregator.getEntityOracle() instanceof DynamicEntityTypeOracle) {
			PropertyMdAggregator propertyMdAggregator = entityMdAggregator.getDynamicPropertyMdAggregator(propertyName);
			propertyMdAggregator.addLocalContextTo(selectorContext);

			return getPropertyMetaDataContextBuilder(propertyMdAggregator);
		}

		PropertyMdAggregator propertyMdAggregator = entityMdAggregator.acquirePropertyMdAggregator(propertyName);
		propertyMdAggregator.addLocalContextTo(selectorContext);

		return getPropertyMetaDataContextBuilder(propertyMdAggregator);
	}

	@Override
	public GmEntityType getGmEntityType() {
		return entityMdAggregator.getGmEntityType();
	}

	protected PropertyMdResolver getPropertyMetaDataContextBuilder(PropertyMdAggregator propertyMdAggregator) {
		return new PropertyMdResolverImpl(propertyMdAggregator, selectorContext.copy());
	}

	@Override
	public EntityMdResolver fork() {
		return new EntityMdResolverImpl(entityMdAggregator, selectorContext.copy());
	}

	@Override
	public <M extends MetaData> EntityMdResult<M> meta(EntityType<M> metaDataType) {
		return new MdResultImpl.EntityMdResultImpl<>(metaDataType, entityMdAggregator, selectorContext);
	}

}
