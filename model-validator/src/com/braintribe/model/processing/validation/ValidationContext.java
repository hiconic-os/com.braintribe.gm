// ============================================================================
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
// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
// ============================================================================
package com.braintribe.model.processing.validation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.processing.validation.expert.ValidationTask;

/**
 * <p>
 * Used during the execution of the validation tasks.
 * </p>
 * <p>
 * It carries central information like {@link ValidationMode validation mode}
 * and {@link ValidationType validation type}, and acts as a buffer for
 * validation messages, as well as further validation tasks.
 * </p>
 * 
 *
 */
public class ValidationContext {

	private ValidationType type;
	private ValidationMode mode;

	private List<ValidationMessage> validationMessages = new ArrayList<>();
	private Queue<ValidationTask> validationTasks = new LinkedList<>();

	public ValidationContext(ValidationType type, ValidationMode mode) {
		this.type = type;
		this.mode = mode;
	}

	public ValidationType getType() {
		return type;
	}

	public ValidationMode getMode() {
		return mode;
	}

	public List<ValidationMessage> getValidationMessages() {
		return validationMessages;
	}

	public void addValidationMessage(GenericEntity element, ValidationMessageLevel level, String message) {
		ValidationMessage validationMessage = ValidationMessage.T.create();
		validationMessage.setElement(element);
		validationMessage.setLevel(level);
		validationMessage.setMessage(message);
		this.validationMessages.add(validationMessage);
	}

	public Queue<ValidationTask> getValidationTasks() {
		return validationTasks;
	}

	public void addValidationTask(ValidationTask validationTask) {
		this.validationTasks.add(validationTask);
	}

	public ValidationTask pollValidationTask() {
		return validationTasks.poll();
	}
}
