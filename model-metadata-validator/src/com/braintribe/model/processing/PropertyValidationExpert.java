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
package com.braintribe.model.processing;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.processing.impl.PropertyValidationContextImpl;

/**
 * An expert for the {@link Validator} to validate a concrete {@link Property} value of a concrete {@link GenericEntity}
 * against a specific characteristic (e.g. certain metadata).
 * 
 * @author Neidhart.Orlich
 *
 */
public interface PropertyValidationExpert {

	/**
	 * Perform a validation action.
	 * 
	 * @see PropertyValidationContextImpl
	 */
	void validate(PropertyValidationContext context);

}
