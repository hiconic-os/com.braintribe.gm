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
package com.braintribe.model.generic.reflection;

import com.braintribe.model.generic.GenericEntity;

/**
 * This visitor propagates only such visit situations that introduce another entity reference.
 * Entity reference can appear more than once because the same entity can appear on properties
 * more than once. This is the essential difference to the {@link EntityVisitor}
 * @author dirk.scheffler
 *
 */
public abstract class EntityReferencesVisitor implements TraversingVisitor {

	// **************************************************************************
	// Constructor
	// **************************************************************************

	/**
	 * Default constructor
	 */
	public EntityReferencesVisitor() {
	}

	// **************************************************************************
	// Interface methods
	// **************************************************************************

	/**
	 * @see com.braintribe.model.generic.reflection.TraversingVisitor#visitTraversing(com.braintribe.model.generic.reflection.TraversingContext)
	 */
	@Override
	final public void visitTraversing(TraversingContext traversingContext) {
		switch(traversingContext.getCurrentCriterionType()) {
		case ENTITY:
			if (traversingContext.getObjectStack().size() == 1) {
				Object value = traversingContext.getObjectStack().peek();
				GenericEntity entity = (GenericEntity)value;
				visitEntityReference(entity, traversingContext);
			}
			break;
		case ROOT:
		case PROPERTY:
		case MAP_KEY:
		case MAP_VALUE:
		case SET_ELEMENT:
		case LIST_ELEMENT:
			Object value = traversingContext.getObjectStack().peek();
			if (value instanceof GenericEntity) {
				GenericEntity entity = (GenericEntity)value;
				visitEntityReference(entity, traversingContext);
			}
			break;
		default:
			break;
		}
	}

	protected abstract void visitEntityReference (GenericEntity entity, TraversingContext traversingContext);
	
}
