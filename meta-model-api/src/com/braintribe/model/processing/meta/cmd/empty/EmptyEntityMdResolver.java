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

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.meta.GmEntityType;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.meta.info.GmPropertyInfo;
import com.braintribe.model.processing.meta.cmd.builders.EntityMdResolver;
import com.braintribe.model.processing.meta.cmd.builders.PropertyMdResolver;
import com.braintribe.model.processing.meta.cmd.result.EntityMdResult;

/**
 * @author peter.gazdik
 */
public class EmptyEntityMdResolver extends EmptyEntityRelatedMdResolver<EntityMdResolver> implements EntityMdResolver {

	public static final EmptyEntityMdResolver INSTANCE = new EmptyEntityMdResolver();

	private EmptyEntityMdResolver() {
	}

	@Override
	public final PropertyMdResolver property(GmPropertyInfo gmPropertyInfo) {
		return EmptyPropertyMdResolver.INSTANCE;
	}

	@Override
	public final PropertyMdResolver property(Property property) {
		return EmptyPropertyMdResolver.INSTANCE;
	}

	@Override
	public final PropertyMdResolver property(String propertyName) {
		return EmptyPropertyMdResolver.INSTANCE;
	}

	@Override
	public final <M extends MetaData> EntityMdResult<M> meta(EntityType<M> metaDataType) {
		return EmptyMdResult.EmptyEntityMdResult.singleton();
	}

	@Override
	public final GmEntityType getGmEntityType() {
		throw new UnsupportedOperationException("Method 'EmptyEntityMdResolver.getGmEntityType' is not supported!");
	}

}
