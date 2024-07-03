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
package com.braintribe.model.generic.enhance;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.collection.PlainList;
import com.braintribe.model.generic.collection.PlainMap;
import com.braintribe.model.generic.collection.PlainSet;
import com.braintribe.model.generic.reflection.CollectionType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.ListType;
import com.braintribe.model.generic.reflection.MapType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.PropertyAccessInterceptor;
import com.braintribe.model.generic.reflection.SetType;

public class NonNullCollectionEnsuringPropertyAccessInterceptor extends PropertyAccessInterceptor {

	@Override
	public Object getProperty(Property property, GenericEntity entity, boolean isVd) {
		Object result = next.getProperty(property, entity, isVd);
		if (result != null)
			return result;

		GenericModelType propertyType = property.getType();
		if (propertyType.isCollection()) {
			if (property.isAbsent(entity))
				return null;

			result = newCollection((CollectionType) propertyType);

			// We must set the value that we have created here!
			next.setProperty(property, entity, result, false);
		}

		return result;
	}

	private static Object newCollection(CollectionType collectionType) {
		switch (collectionType.getTypeCode()) {
			case listType:
				return new PlainList<>((ListType) collectionType);
			case mapType:
				return new PlainMap<>((MapType) collectionType);
			case setType:
				return new PlainSet<>((SetType) collectionType);
			default:
				throw new RuntimeException("Unsupported collection type: " + collectionType.getTypeCode());
		}
	}

}
