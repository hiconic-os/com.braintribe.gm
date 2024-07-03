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

import com.braintribe.model.meta.data.constraint.MaxLength;
import com.braintribe.model.processing.PropertyValidationContext;
import com.braintribe.model.processing.PropertyValidationExpert;

public class MaxLengthMetadataExpert implements PropertyValidationExpert {

	@Override
	public void validate(PropertyValidationContext context) {
		if (context.getPropertyValue() instanceof String) {
			String propertyStringValue = (String) context.getPropertyValue();

			MaxLength maxLength = context.getPropertyMdResolver().meta(MaxLength.T).exclusive();
			int length = propertyStringValue.length();
			if (maxLength != null && maxLength.getLength() < length) {
				context.notifyConstraintViolation(
						"String has " + length + " chars and is longer than its allowed maximum length: " + maxLength.getLength());
			}
		}
	}

}
