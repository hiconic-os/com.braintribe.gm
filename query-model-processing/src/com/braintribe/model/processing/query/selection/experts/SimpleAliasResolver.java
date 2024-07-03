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
package com.braintribe.model.processing.query.selection.experts;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.CollectionType;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.query.From;
import com.braintribe.model.query.Join;
import com.braintribe.model.query.Source;

public class SimpleAliasResolver extends AbstractAliasResolver {
	

	@Override
	public String getAliasForSource(Source source) {
		EntityType<GenericEntity> entityType = getEntityType(source);
		if (entityType != null) {
			return resolveTypeSignature(entityType.getTypeSignature());
		}
		return null;
	}

	protected EntityType<GenericEntity> getEntityType(Source source) {
		String typeSignature = null;
		
		if (source instanceof From) {
			typeSignature = ((From) source).getEntityTypeSignature();
		} else if (source instanceof Join) {
			Join join = (Join) source;
			EntityType<GenericEntity> joinType = getEntityType(join.getSource());
			String joinPropertyName = join.getProperty();
			Property property = joinType.findProperty(joinPropertyName);
				
			if (property != null) {
				GenericModelType propertyType = property.getType();
				switch (propertyType.getTypeCode()) {
				case entityType:
					@SuppressWarnings("unchecked")
					EntityType<GenericEntity> propertyEntityType = (EntityType<GenericEntity>) propertyType;
					typeSignature = propertyEntityType.getTypeSignature();
					break;
				case listType:
				case mapType:
				case setType:
					CollectionType propertyCollectionType = (CollectionType) propertyType;
					GenericModelType collectionElementType = propertyCollectionType.getCollectionElementType();
					if (collectionElementType instanceof EntityType<?>) {
						typeSignature = collectionElementType.getTypeSignature();
					}
					break;
				default:
					break;

				}
			} 
		}
		
		if (typeSignature != null) {
			return GMF.getTypeReflection().getEntityType(typeSignature);
		}
		return null;
	}

}
