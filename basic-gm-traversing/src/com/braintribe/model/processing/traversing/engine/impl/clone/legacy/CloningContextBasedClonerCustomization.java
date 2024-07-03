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
package com.braintribe.model.processing.traversing.engine.impl.clone.legacy;

import java.util.Stack;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.path.api.ModelPathElementType;
import com.braintribe.model.generic.pr.AbsenceInformation;
import com.braintribe.model.generic.pr.criteria.BasicCriterion;
import com.braintribe.model.generic.pr.criteria.PropertyCriterion;
import com.braintribe.model.generic.pr.criteria.RootCriterion;
import com.braintribe.model.generic.reflection.CloningContext;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.processing.traversing.api.GmTraversingContext;
import com.braintribe.model.processing.traversing.api.path.TraversingModelPathElement;
import com.braintribe.model.processing.traversing.api.path.TraversingPropertyModelPathElement;
import com.braintribe.model.processing.traversing.api.path.TraversingPropertyRelatedModelPathElement;
import com.braintribe.model.processing.traversing.engine.api.customize.ClonerCustomization;
import com.braintribe.model.processing.traversing.engine.api.customize.PropertyTransferContext;
import com.braintribe.model.processing.traversing.engine.impl.clone.BasicPropertyTransferExpert;

/**
 * @author peter.gazdik
 */
public class CloningContextBasedClonerCustomization implements ClonerCustomization {

	private final CloningContext cc;
	private final RootCriterion rootCriterion = RootCriterion.T.create();

	/**
	 * @param cc
	 *            a CloningContext which is compatible with GMT, most likely something that is explicitly marked as
	 *            {@link GmtCompatibleCloningContext}, or at least follows the same limitations.
	 */
	public CloningContextBasedClonerCustomization(CloningContext cc) {
		this.cc = cc;
	}

	@Override
	public void transferProperty(GenericEntity clonedEntity, Property property, Object clonedValue, PropertyTransferContext context) {
		BasicPropertyTransferExpert.INSTANCE.transferProperty(clonedEntity, property, clonedValue, context);
	}

	@Override
	public <T extends GenericEntity> T supplyRawClone(T instanceToBeCloned, GmTraversingContext context, TraversingModelPathElement pathElement,
			EntityType<T> entityType) {
		return (T) cc.supplyRawClone(entityType, instanceToBeCloned);
	}

	@Override
	public boolean isAbsenceResolvable(GenericEntity instanceToBeCloned, GmTraversingContext context,
			TraversingPropertyModelPathElement propertyPathElement, AbsenceInformation absenceInformation) {
		return cc.isAbsenceResolvable(propertyPathElement.getProperty(), instanceToBeCloned, absenceInformation);
	}

	@Override
	public AbsenceInformation createAbsenceInformation(GenericEntity instanceToBeCloned, GmTraversingContext context,
			TraversingPropertyModelPathElement propertyPathElement) {
		Property property = propertyPathElement.getProperty();
		return cc.createAbsenceInformation(property.getType(), instanceToBeCloned, property);
	}

	@Override
	public Object postProcessClonedPropertyRelatedValue( //
			Object clonedValue, GmTraversingContext context, TraversingPropertyRelatedModelPathElement pathElement) {

		BasicCriterion tc = resolveTcToPutOnStack(pathElement);
		Stack<BasicCriterion> traversingStack = cc.getTraversingStack();

		traversingStack.push(tc);

		try {
			return cc.postProcessCloneValue(pathElement.getType(), clonedValue);

		} finally {
			traversingStack.pop();

		}
	}

	private BasicCriterion resolveTcToPutOnStack(TraversingPropertyRelatedModelPathElement pathElement) {
		if (pathElement.getElementType() != ModelPathElementType.Property)
			return rootCriterion;

		Property property = pathElement.getProperty();

		PropertyCriterion result = PropertyCriterion.T.create();
		result.setPropertyName(property.getName());
		result.setTypeSignature(property.getType().getTypeSignature());
		return result;
	}

}
