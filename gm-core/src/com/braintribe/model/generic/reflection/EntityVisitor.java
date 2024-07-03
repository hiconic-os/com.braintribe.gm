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

import com.braintribe.common.lcd.function.TriConsumer;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.criteria.BasicCriterion;
import com.braintribe.model.generic.pr.criteria.EntityCriterion;

/**
 * @author gunther.schenk
 */
public abstract class EntityVisitor implements TraversingVisitor {

	/** @see TraversingVisitor#visitTraversing(com.braintribe.model.generic.reflection.TraversingContext) */
	@Override
	final public void visitTraversing(TraversingContext traversingContext) {
		BasicCriterion criterion = traversingContext.getTraversingStack().peek();
		if (criterion instanceof EntityCriterion) {
			GenericEntity entity = (GenericEntity) traversingContext.getObjectStack().peek(); 
			visitEntity(entity, (EntityCriterion) criterion, traversingContext);
		}

	}

	protected abstract void visitEntity (GenericEntity entity, EntityCriterion criterion, TraversingContext traversingContext);

	public static EntityVisitor onVisitEntity(TriConsumer<GenericEntity, EntityCriterion, TraversingContext> onVisitEntity) {
		return new EntityVisitor() {
			@Override
			protected void visitEntity(GenericEntity entity, EntityCriterion criterion, TraversingContext traversingContext) {
				onVisitEntity.accept(entity, criterion, traversingContext);
			}
		};
	}

}
