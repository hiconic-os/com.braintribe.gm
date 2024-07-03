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
package com.braintribe.model.processing.vde.evaluator.impl.bvd.navigation;

import com.braintribe.model.bvd.navigation.PropertyPath;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.processing.vde.evaluator.api.ValueDescriptorEvaluator;
import com.braintribe.model.processing.vde.evaluator.api.VdeContext;
import com.braintribe.model.processing.vde.evaluator.api.VdeResult;
import com.braintribe.model.processing.vde.evaluator.api.VdeRuntimeException;
import com.braintribe.model.processing.vde.evaluator.impl.VdeResultImpl;

/**
 * {@link ValueDescriptorEvaluator} for {@link PropertyPath}
 * 
 */
public class PropertyPathVde implements ValueDescriptorEvaluator<PropertyPath> {

	@Override
	public VdeResult evaluate(VdeContext context, PropertyPath valueDescriptor) throws VdeRuntimeException {

		String[] propertyNames = valueDescriptor.getPropertyPath().split("\\.");

		Object result = context.evaluate(valueDescriptor.getEntity());
		GenericModelType type = GMF.getTypeReflection().getType(result);

		for (String propertyName : propertyNames) {
			if (result == null) {
				return new VdeResultImpl(null, false);
			}

			GenericEntity ge = (GenericEntity) result;
			EntityType<?> et = (EntityType<?>) type;

			Property property = et.findProperty(propertyName);
			
			if (property != null) {
				result = property.get(ge);
				type = property.getType();
			}

		}

		return new VdeResultImpl(result, false);
	}

}
