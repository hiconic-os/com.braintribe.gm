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
import com.braintribe.model.generic.GmCoreApiInteropNamespaces;
import com.braintribe.model.generic.pr.AbsenceInformation;

import jsinterop.annotations.JsType;

@JsType(namespace = GmCoreApiInteropNamespaces.reflection)
@SuppressWarnings("unusable-by-js")
public interface CloningContext extends TraversingContext {

	GenericEntity supplyRawClone(EntityType<? extends GenericEntity> entityType, GenericEntity instanceToBeCloned);

	/**
	 * Checks whether the property on original entity should even be handled. This is the very first check, before the property value is even looked
	 * at.
	 * <p>
	 * If this method returns
	 * <ul>
	 * <li><tt>false</tt>, the property is ignored and the next one is considered
	 * <li><tt>true</tt>, the property value is retrieved and another check is made with {@link #isTraversionContextMatching()}
	 * </ul>
	 * 
	 * @see #isAbsenceResolvable
	 * @see #isTraversionContextMatching()
	 */
	boolean canTransferPropertyValue(EntityType<? extends GenericEntity> entityType, Property property, GenericEntity instanceToBeCloned,
			GenericEntity clonedInstance, AbsenceInformation sourceAbsenceInformation);

	/**
	 * This method is called after a property value or a collection element are cloned to allow additional adjustments.
	 */
	Object postProcessCloneValue(GenericModelType propertyOrElementType, Object clonedValue);

	AbsenceInformation createAbsenceInformation(GenericModelType type, GenericEntity instanceToBeCloned, Property property);

	GenericEntity preProcessInstanceToBeCloned(GenericEntity instanceToBeCloned);

	StrategyOnCriterionMatch getStrategyOnCriterionMatch();

}
