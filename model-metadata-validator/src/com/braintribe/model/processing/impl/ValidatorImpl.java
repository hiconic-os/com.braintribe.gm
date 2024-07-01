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
package com.braintribe.model.processing.impl;

import java.util.ArrayList;
import java.util.List;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.path.api.ModelPathElementType;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.typecondition.TypeCondition;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.processing.ConstraintViolation;
import com.braintribe.model.processing.PropertyValidationContext;
import com.braintribe.model.processing.PropertyValidationExpert;
import com.braintribe.model.processing.ValidationContext;
import com.braintribe.model.processing.ValidationExpert;
import com.braintribe.model.processing.ValidationExpertRegistry;
import com.braintribe.model.processing.Validator;
import com.braintribe.model.processing.meta.cmd.CmdResolver;
import com.braintribe.model.processing.meta.cmd.builders.ModelMdResolver;
import com.braintribe.model.processing.traversing.api.GmTraversingContext;
import com.braintribe.model.processing.traversing.api.GmTraversingException;
import com.braintribe.model.processing.traversing.api.GmTraversingVisitor;
import com.braintribe.model.processing.traversing.api.path.TraversingModelPathElement;
import com.braintribe.model.processing.traversing.api.path.TraversingPropertyModelPathElement;
import com.braintribe.model.processing.traversing.engine.GMT;

/**
 * Validates an entity and its properties transitively by traversing it with the {@link GMT} api. What exactly is
 * validated depends on the implementations of the experts: {@link ValidationExpert}s and
 * {@link PropertyValidationExpert}s which can be passed via an {@link ValidationExpertRegistry}.
 */
public class ValidatorImpl implements GmTraversingVisitor, Validator {

	private final ModelMdResolver modelMdResolver;
	private final ValidationExpertRegistry validationExperts;

	private final List<ConstraintViolation> constraintViolations = new ArrayList<>();

	/**
	 * The constructor allows you maximum customization. However if you just need default validation experts consider
	 * using the static factory methods {@link Validator#create(GenericModelType, GmMetaModel)} and
	 * {@link Validator#create(TypeCondition, GmMetaModel)}.
	 * 
	 * @param cmdResolver
	 *            will be used to determine the metadata of the validated object
	 * @param validationExperts
	 *            Experts to be used for validation
	 */
	public ValidatorImpl(CmdResolver cmdResolver, ValidationExpertRegistry validationExperts) {
		this.validationExperts = validationExperts;
		this.modelMdResolver = cmdResolver.getMetaData();
	}

	@Override
	public List<ConstraintViolation> checkConstraints(Object rootValue) {
		doWalk(rootValue);
		return constraintViolations;
	}

	private void doWalk(Object rootValue) {
		constraintViolations.clear();
		GMT.traverse().depthFirstWalk().visitor(this).doFor(rootValue);
	}

	@Override
	public void validate(Object rootValue) {
		doWalk(rootValue);

		if (!constraintViolations.isEmpty()) {
			StringBuilder stringBuilder = new StringBuilder("Validation failed: ");
			constraintViolations.forEach(c -> stringBuilder.append("\n" + c.toString()));

			throw new IllegalArgumentException(stringBuilder.toString());
		}
	}

	@Override
	public void onElementEnter(GmTraversingContext context, TraversingModelPathElement pathElement) throws GmTraversingException {
		if (pathElement.getElementType() == ModelPathElementType.Root) {
			ValidationContext rootContext = new ValidationContextImpl(pathElement, constraintViolations::add);
			validationExperts.getRootEntityExperts().forEach(e -> e.validate(rootContext));
		}

		if (pathElement.getType().isEntity()) {

			EntityType<?> pathElementType = pathElement.getType();
			GenericEntity value = pathElement.getValue();

			if (value != null) {
				for (Property property : pathElementType.getProperties()) {
					TraversingPropertyModelPathElement propertyPathElement = new TraversingPropertyModelPathElement(pathElement, value,
							pathElementType, property, false);
					PropertyValidationContext propertyValidationContext = new PropertyValidationContextImpl(propertyPathElement, modelMdResolver,
							constraintViolations::add);

					validationExperts.getPropertyExperts().forEach(e -> e.validate(propertyValidationContext));
				}
			}
		}

	}

	@Override
	public void onElementLeave(GmTraversingContext context, TraversingModelPathElement pathElement) throws GmTraversingException {
		// Unused functionality of implemented interface
		// We only use onElementEnter()
	}

	public ValidationExpertRegistry getValidationExpertRegistry() {
		return validationExperts;
	}
}
