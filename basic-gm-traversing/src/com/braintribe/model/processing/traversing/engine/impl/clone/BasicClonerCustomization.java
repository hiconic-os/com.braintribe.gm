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
package com.braintribe.model.processing.traversing.engine.impl.clone;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.AbsenceInformation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.session.GmSession;
import com.braintribe.model.processing.traversing.api.GmTraversingContext;
import com.braintribe.model.processing.traversing.api.path.TraversingModelPathElement;
import com.braintribe.model.processing.traversing.api.path.TraversingPropertyModelPathElement;
import com.braintribe.model.processing.traversing.api.path.TraversingPropertyRelatedModelPathElement;
import com.braintribe.model.processing.traversing.engine.api.customize.ClonerCustomization;
import com.braintribe.model.processing.traversing.engine.api.customize.PropertyTransferContext;
import com.braintribe.model.processing.traversing.engine.api.customize.PropertyTransferExpert;

public class BasicClonerCustomization implements ClonerCustomization {

	protected GmSession session;
	protected AbsenceInformation absenceInformation = GMF.absenceInformation();
	protected boolean absenceResolvable = false;
	protected PropertyTransferExpert propertyTransferExpert = BasicPropertyTransferExpert.INSTANCE;

	public void setSession(GmSession session) {
		this.session = session;
	}

	public void setPropertyTransferExpert(PropertyTransferExpert propertyTransferExpert) {
		this.propertyTransferExpert = propertyTransferExpert;
	}

	@Override
	public AbsenceInformation createAbsenceInformation(GenericEntity instanceToBeCloned, GmTraversingContext context,
			TraversingPropertyModelPathElement propertyPathElement) {
		return absenceInformation;
	}

	@Override
	public <T extends GenericEntity> T supplyRawClone(T instanceToBeCloned, GmTraversingContext context,
			TraversingModelPathElement pathElement, EntityType<T> entityType) {
		if (session != null)
			return session.create(entityType);
		else
			return entityType.create();
	}

	@Override
	public Object postProcessClonedPropertyRelatedValue(Object clonedValue, GmTraversingContext context,
			TraversingPropertyRelatedModelPathElement pathElement) {
		return clonedValue;
	}

	public void setAbsenceResolvable(boolean absenceResolvable) {
		this.absenceResolvable = absenceResolvable;
	}

	@Override
	public boolean isAbsenceResolvable(GenericEntity instanceToBeCloned, GmTraversingContext context,
			TraversingPropertyModelPathElement propertyPathElement, AbsenceInformation absenceInformation) {
		return absenceResolvable;
	}

	@Override
	public void transferProperty(GenericEntity clonedEntity, Property property, Object clonedValue, PropertyTransferContext context) {
		propertyTransferExpert.transferProperty(clonedEntity, property, clonedValue, context);
	}

}
