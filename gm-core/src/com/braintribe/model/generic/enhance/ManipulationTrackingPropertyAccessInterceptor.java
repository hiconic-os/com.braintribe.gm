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
package com.braintribe.model.generic.enhance;

import static com.braintribe.model.generic.manipulation.util.ManipulationBuilder.absenting;
import static com.braintribe.model.generic.manipulation.util.ManipulationBuilder.changeValue;
import static com.braintribe.model.generic.manipulation.util.ManipulationBuilder.localEntityProperty;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.manipulation.ChangeValueManipulation;
import com.braintribe.model.generic.manipulation.LocalEntityProperty;
import com.braintribe.model.generic.manipulation.Manipulation;
import com.braintribe.model.generic.manipulation.Owner;
import com.braintribe.model.generic.pr.AbsenceInformation;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.PropertyAccessInterceptor;
import com.braintribe.model.generic.reflection.VdHolder;
import com.braintribe.model.generic.session.GmSession;

public class ManipulationTrackingPropertyAccessInterceptor extends PropertyAccessInterceptor {

	public boolean ignoredNoChangeAssignments;
	
	@Override
	public Object setProperty(Property property, GenericEntity entity, Object value, boolean isVd) {
		/* We assume entity is enhanced, otherwise there would be no session to be notified with the manipulation... */
		GmSession session = entity.session();

		// Note: this has to be done before setting the owner because in case of the id property change it would
		// otherwise create a PersistentEntityReference which is then wrong
		Owner entityProperty = newLocalOwner(entity, property);

		Object oldValue = next.setProperty(property, entity, value, isVd);

		if (session == null)
			return oldValue;

		if (isVd)
			// TODO what to do here - track as Absenting, whose property should be of type ValueDescriptor, AbesetingManipulator should be of type VD
			return oldValue;

		// oldValue being VD is no problem. We know "value" is not VD. If oldValue was VD, it is actually a VdHolder and this if is false
		if (ignoredNoChangeAssignments && (value == oldValue || (value != null && value.equals(oldValue))))
			return oldValue;

		ChangeValueManipulation manipulation = changeValue(property.getType().getValueSnapshot(value), entityProperty);

		AbsenceInformation ai = VdHolder.getAbsenceInfoIfPossible(oldValue);
		Manipulation im = ai != null ? absenting(ai, entityProperty) : changeValue(oldValue, entityProperty);

		manipulation.linkInverse(im);

		session.noticeManipulation(manipulation);

		return oldValue;
	}

	private static LocalEntityProperty newLocalOwner(GenericEntity entity, Property p) {
		return localEntityProperty(entity, p.getName());
	}

}
