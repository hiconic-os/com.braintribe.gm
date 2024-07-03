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
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.PropertyAccessInterceptor;
import com.braintribe.model.generic.value.ValueDescriptor;

/**
 * Terminal {@link PropertyAccessInterceptor}, one that does not delegate to it's successor, but accesses the property of given entity (via
 * direct access, i.e. get-/setPropertyDirect).
 */
public class FieldAccessingPropertyAccessInterceptor extends PropertyAccessInterceptor {

	public static final FieldAccessingPropertyAccessInterceptor INSTANCE = new FieldAccessingPropertyAccessInterceptor();

	private FieldAccessingPropertyAccessInterceptor() {
	}

	@Override
	public Object getProperty(Property property, GenericEntity entity, boolean isVd) {
		return isVd ? property.getVdDirect(entity) : property.getDirectUnsafe(entity);
	}

	@Override
	public Object setProperty(Property property, GenericEntity entity, Object value, boolean isVd) {
		if (isVd) {
			return property.setVdDirect(entity, (ValueDescriptor) value);

		} else {
			return property.setDirect(entity, value);
		}
	}

}
