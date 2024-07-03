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

import java.util.Collection;
import java.util.Stack;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.AbsenceInformation;
import com.braintribe.model.generic.pr.criteria.BasicCriterion;
import com.braintribe.model.generic.pr.criteria.CriterionType;

public class DelegatingCloningContext implements CloningContext {
	private final TraversingContext traversingContextDelegate;
	
	@Override
	public Stack<BasicCriterion> getTraversingStack() {
		return traversingContextDelegate.getTraversingStack();
	}

	@Override
	public Stack<Object> getObjectStack() {
		return traversingContextDelegate.getObjectStack();
	}

	@Override
	public void pushTraversingCriterion(BasicCriterion criterion, Object object) {
		traversingContextDelegate.pushTraversingCriterion(criterion, object);
	}

	@Override
	public BasicCriterion popTraversingCriterion() {
		return traversingContextDelegate.popTraversingCriterion();
	}

	@Override
	public boolean isTraversionContextMatching() {
		return traversingContextDelegate.isTraversionContextMatching();
	}

	@Override
	public void registerAsVisited(GenericEntity entity, Object associate) {
		traversingContextDelegate.registerAsVisited(entity, associate);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getAssociated(GenericEntity entity) {
		return (T)traversingContextDelegate.getAssociated(entity);
	}

	@Override
	public Collection<GenericEntity> getVisitedObjects() {
		return traversingContextDelegate.getVisitedObjects();
	}

	@Override
	public <T> Collection<T> getAssociatedObjects() {
		return traversingContextDelegate.getAssociatedObjects();
	}

	@Override
	public boolean isVisited(GenericEntity entity) {
		return traversingContextDelegate.isVisited(entity);
	}

	public DelegatingCloningContext(TraversingContext traversingContextDelegate) {
		this.traversingContextDelegate = traversingContextDelegate;
	}
	
	@Override
	public GenericEntity supplyRawClone(EntityType<? extends GenericEntity> entityType, GenericEntity instanceToBeCloned) {
		return entityType.create();
	}
	
	@Override
	public boolean canTransferPropertyValue(
			EntityType<? extends GenericEntity> entityType, Property property,
			GenericEntity instanceToBeCloned, GenericEntity clonedInstance,
			AbsenceInformation sourceAbsenceInformation) {
		return true;
	}
	
	@Override
	public Object postProcessCloneValue(GenericModelType propertyType,
			Object clonedValue) {
		return clonedValue;
	}
	
	@Override
	public AbsenceInformation createAbsenceInformation(GenericModelType type, GenericEntity instanceToBeCloned, Property property) {
		return GMF.absenceInformation();
	}
	
	@Override
	public GenericEntity preProcessInstanceToBeCloned(GenericEntity instanceToBeCloned) {
		return instanceToBeCloned;
	}
	
	/**
	 * @see com.braintribe.model.generic.reflection.CloningContext#isAbsenceResolvable(com.braintribe.model.generic.reflection.Property,
	 *      com.braintribe.model.generic.GenericEntity, com.braintribe.model.generic.pr.AbsenceInformation)
	 */
	@Override
	public boolean isAbsenceResolvable(Property property, GenericEntity instanceToBeCloned, AbsenceInformation absenceInformation) {
		return false;
	}
	
	@Override
	public boolean isPropertyValueUsedForMatching(EntityType<?> type, GenericEntity entity, Property property) {
		return true;
	}

	@Override
	public CriterionType getCurrentCriterionType() {
		return traversingContextDelegate.getCurrentCriterionType();
	}

	@Override
	public StrategyOnCriterionMatch getStrategyOnCriterionMatch() {
		return StrategyOnCriterionMatch.partialize;
	}
	
}
