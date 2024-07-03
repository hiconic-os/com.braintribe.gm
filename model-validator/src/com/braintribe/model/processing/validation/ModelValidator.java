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
package com.braintribe.model.processing.validation;

import java.util.List;

import com.braintribe.model.processing.validation.expert.ValidationTask;

/**
 * <p>
 * The main driver of the model validation.
 * </p>
 * 
 *
 */
public class ModelValidator {

	/**
	 * <p>
	 * Executes validation tasks that are enqueued in the {@link ValidationContext
	 * validation context} starting from the provided root task.
	 * </p>
	 * 
	 * @param context
	 * @param rootValidationTask
	 *            - validation task that corresponds to the model element being
	 *            validated
	 * @return the list of resulting validation messages
	 */
	public List<ValidationMessage> validate(ValidationContext context, ValidationTask rootValidationTask) {
		context.addValidationTask(rootValidationTask);

		while (!context.getValidationTasks().isEmpty()) {
			context.pollValidationTask().execute(context);
		}
		return context.getValidationMessages();
	}
}
