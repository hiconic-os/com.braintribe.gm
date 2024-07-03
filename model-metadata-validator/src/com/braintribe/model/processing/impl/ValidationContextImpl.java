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
package com.braintribe.model.processing.impl;

import java.util.function.Consumer;

import com.braintribe.model.processing.ConstraintViolation;
import com.braintribe.model.processing.ValidationContext;
import com.braintribe.model.processing.traversing.api.path.TraversingModelPathElement;

/**
 * Contains various information about what is to be validated. Constraint violations can be reported by
 * {@link #notifyConstraintViolation(String)}.
 * 
 * @author Neidhart.Orlich
 *
 */
public class ValidationContextImpl implements ValidationContext {

	private final TraversingModelPathElement pathElement;
	private final Consumer<ConstraintViolation> constraintViolationConsumer;
	private final Object value;

	public ValidationContextImpl(TraversingModelPathElement pathElement, Consumer<ConstraintViolation> constraintViolationConsumer) {
		this.pathElement = pathElement;
		this.constraintViolationConsumer = constraintViolationConsumer;
		this.value = pathElement.getValue();
	}

	@Override
	public void notifyConstraintViolation(String message) {
		constraintViolationConsumer.accept(ConstraintViolation.create(message, pathElement));
	}

	@Override
	public TraversingModelPathElement getPathElement() {
		return pathElement;
	}

	@Override
	public Object getValue() {
		return value;
	}

}