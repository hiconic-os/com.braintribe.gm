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
import com.braintribe.model.generic.reflection.VdHolder;
import com.braintribe.model.generic.value.ValueDescriptor;

/**
 * @author peter.gazdik
 */
public class VdePropertyAccessInterceptor extends PropertyAccessInterceptor {

	private Object vdeContext;

	// Once we want this feature this will have a correct type
	public void setVdeContext(Object vdeContext) {
		this.vdeContext = vdeContext;
	}

	@Override
	public Object getProperty(Property property, GenericEntity entity, boolean isVd) {
		Object result = next.getProperty(property, entity, isVd); // TODO this is then a little weird

		if (VdHolder.isVdHolder(result)) {
			if (vdeContext != null)
				return evaluate(((VdHolder) result).vd);

			return property.getDefaultRawValue();
		}

		return result;
	}

	private Object evaluate(@SuppressWarnings("unused") ValueDescriptor vd) {
		throw new UnsupportedOperationException("Method 'VdePropertyAccessInterceptor.evaluate' is not supported yet!");
	}

}
