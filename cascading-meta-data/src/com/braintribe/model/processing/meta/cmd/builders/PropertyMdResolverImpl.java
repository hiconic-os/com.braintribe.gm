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
import com.braintribe.model.meta.GmProperty;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.processing.meta.cmd.context.MutableSelectorContext;
import com.braintribe.model.processing.meta.cmd.resolvers.PropertyMdAggregator;
import com.braintribe.model.processing.meta.cmd.result.PropertyMdResult;

public class PropertyMdResolverImpl extends EntityRelatedMdResolverImpl<PropertyMdResolver> implements PropertyMdResolver {

	protected PropertyMdAggregator propertyMdAggregator;

	protected PropertyMdResolverImpl(PropertyMdAggregator propertyMdAggregator, MutableSelectorContext selectorContext) {
		super(PropertyMdResolverImpl.class, selectorContext, propertyMdAggregator);

		this.propertyMdAggregator = propertyMdAggregator;
	}

	@Override
	public GmProperty getGmProperty() {
		return propertyMdAggregator.getGmProperty();
	}

	@Override
	public PropertyMdResolver fork() {
		return new PropertyMdResolverImpl(propertyMdAggregator, selectorContext.copy());
	}

	@Override
	public <M extends MetaData> PropertyMdResult<M> meta(EntityType<M> metaDataType) {
		return new MdResultImpl.PropertyMdResultImpl<>(metaDataType, propertyMdAggregator, selectorContext);
	}

}
