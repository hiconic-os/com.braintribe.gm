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
package com.braintribe.model.access.security.manipulation.experts;

import com.braintribe.model.generic.manipulation.PropertyManipulation;
import com.braintribe.model.generic.value.EntityReference;
import com.braintribe.model.generic.value.EntityReferenceType;
import com.braintribe.model.meta.data.constraint.Modifiable;
import com.braintribe.model.processing.security.manipulation.ManipulationSecurityContext;
import com.braintribe.model.processing.security.manipulation.ManipulationSecurityExpert;
import com.braintribe.model.processing.security.manipulation.ManipulationSecurityExpositionContext;
import com.braintribe.model.processing.security.manipulation.SecurityViolationEntry;

/**
 * {@link ManipulationSecurityExpert} for {@link Modifiable} constraint.
 */
public class PropertyModifiableExpert implements ManipulationSecurityExpert {

	@Override
	public Object createExpertContext(ManipulationSecurityContext context) {
		return null;
	}

	@Override
	public void expose(ManipulationSecurityExpositionContext context) {
		EntityReference ref = context.getTargetReference();
		if (ref == null)
			return;

		if (!(context.getCurrentManipulation() instanceof PropertyManipulation))
			return;

		String typeSignature = ref.getTypeSignature();
		String propertyName = context.getTargetPropertyName();
		// TODO FIX - just another example where preliminary entity could have persistent ref if id set by user
		boolean isPreliminary = ref.referenceType() == EntityReferenceType.preliminary;

		if (!context.getCmdResolver().getMetaData() //
				.entityTypeSignature(typeSignature) //
				.property(propertyName) //
				.preliminary(isPreliminary) //
				.is(Modifiable.T)) {

			SecurityViolationEntry violationEntry = SecurityViolationEntry.T.create();

			violationEntry.setCausingManipulation(context.getCurrentManipulation());
			violationEntry.setEntityReference(ref);
			violationEntry.setDescription("[EntityPropertyNotEditable] Property: " + typeSignature + "." + propertyName);
			violationEntry.setPropertyName(propertyName);

			context.addViolationEntry(violationEntry);
		}
	}

	@Override
	public void validate(ManipulationSecurityContext context) {
		// Intentionally left blank
		return;
	}

}
