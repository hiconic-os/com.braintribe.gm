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

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.AbsenceInformation;


@SuppressWarnings("unusable-by-js")
public class StandardCloningContext extends StandardTraversingContext implements CloningContext {

	private StrategyOnCriterionMatch strategyOnCriterionMatch = StrategyOnCriterionMatch.partialize;
	
	@Override
	public GenericEntity supplyRawClone(EntityType<? extends GenericEntity> entityType, GenericEntity instanceToBeCloned) {
		return entityType.createRaw();
	}
	
	/**
	 * @deprecated use {@link #canTransferPropertyValue(EntityType, Property, GenericEntity, GenericEntity, AbsenceInformation)} instead
	 */
	@Deprecated
	@SuppressWarnings("unused")
	public boolean canTransferPropertyValue(EntityType<? extends GenericEntity> entityType, Property property, GenericEntity instanceToBeCloned) {
		return true;
	}

	@Override
	public boolean canTransferPropertyValue(
			EntityType<? extends GenericEntity> entityType, Property property,
			GenericEntity instanceToBeCloned, GenericEntity clonedInstance,
			AbsenceInformation sourceAbsenceInformation) {
		return canTransferPropertyValue(entityType, property, instanceToBeCloned);
	}

	@Override
	public Object postProcessCloneValue(GenericModelType propertyOrElementType, Object clonedValue) {
		return clonedValue;
	}
	
	/**
	 * @see com.braintribe.model.generic.reflection.CloningContext#createAbsenceInformation(com.braintribe.model.generic.reflection.GenericModelType, com.braintribe.model.generic.GenericEntity, com.braintribe.model.generic.reflection.Property)
	 */
	@Override
	public AbsenceInformation createAbsenceInformation(GenericModelType type, GenericEntity instanceToBeCloned, Property property) {
		return GMF.absenceInformation();
	}
	
	/**
	 * @see com.braintribe.model.generic.reflection.CloningContext#preProcessInstanceToBeCloned(com.braintribe.model.generic.GenericEntity)
	 */
	@Override
	public GenericEntity preProcessInstanceToBeCloned(GenericEntity instanceToBeCloned) {
		return instanceToBeCloned;
	}
	
	@Override
	public StrategyOnCriterionMatch getStrategyOnCriterionMatch() {
		return strategyOnCriterionMatch;
	}

	public void setStrategyOnCriterionMatch(StrategyOnCriterionMatch strategyOnCriterionMatch) {
		this.strategyOnCriterionMatch = strategyOnCriterionMatch;
	}

}
