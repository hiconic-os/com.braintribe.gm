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
package com.braintribe.model.processing.validation.expert.property;

import com.braintribe.model.meta.data.constraint.Min;
import com.braintribe.model.processing.PropertyValidationContext;
import com.braintribe.model.processing.PropertyValidationExpert;

public class MinMetadataExpert implements PropertyValidationExpert {

	@Override
	public void validate(PropertyValidationContext context) {
		Object propertyValue = context.getPropertyValue();
		if (propertyValue instanceof Number) {
			Min min = context.getPropertyMdResolver().meta(Min.T).exclusive();
			if (min != null) {

				Object limit = min.getLimit();
				boolean constraintViolated;

				if (limit.getClass() != propertyValue.getClass()) {
					context.notifyConstraintViolation("Error while resolving " + Min.T.getShortName() + " metadata: Property has a different class ("
							+ propertyValue.getClass() + ") than its declared limit (" + limit.getClass() + ")");
					return;
				}

				switch (context.getPropertyType().getTypeCode()) {
					case integerType:
						constraintViolated = (int) limit > (int) propertyValue;
						break;
					case longType:
						constraintViolated = (long) limit > (long) propertyValue;
						break;
					case floatType:
						constraintViolated = (float) limit > (float) propertyValue;
						break;
					case doubleType:
						constraintViolated = (double) limit > (double) propertyValue;
						break;
					default:
						throw new IllegalStateException("Found " + min.entityType().getTypeSignature() + " metadata on a property of type "
								+ context.getEntityType().getTypeSignature() + " which is not supported.");
				}

				if (constraintViolated) {
					context.notifyConstraintViolation("Property exceeds its allowed minimum value. Min: " + limit);
				}
			}
		}
	}

}
