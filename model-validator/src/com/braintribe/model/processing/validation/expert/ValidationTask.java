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
package com.braintribe.model.processing.validation.expert;

import com.braintribe.model.processing.validation.ValidationContext;

/**
 * <p>
 * The base type of the validation tasks used to validate model elements i.e.
 * check whether they adhere to defined constraints.
 * </p>
 * 
 *
 */
public interface ValidationTask {

	/**
	 * <p>
	 * Hosts model element validation implementation.
	 * </p>
	 * 
	 * @param context
	 */
	public void execute(ValidationContext context);
}
