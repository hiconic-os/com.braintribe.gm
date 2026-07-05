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

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.manipulation.ManipulationType;
import com.braintribe.model.generic.reflection.GenericModelTypeReflection;
import com.braintribe.model.meta.data.constraint.Deletable;
import com.braintribe.model.processing.meta.cmd.CmdResolver;
import com.braintribe.model.processing.security.manipulation.ManipulationSecurityContext;
import com.braintribe.model.processing.security.manipulation.ManipulationSecurityExpert;
import com.braintribe.model.processing.security.manipulation.ManipulationSecurityExpositionContext;
import com.braintribe.model.processing.security.manipulation.SecurityViolationEntry;

/**
 * {@link ManipulationSecurityExpert} for {@link Deletable} constraint.
 */
public class EntityDeletionExpert implements ManipulationSecurityExpert {

	GenericModelTypeReflection typeReflection = GMF.getTypeReflection();

	@Override
	public Object createExpertContext(ManipulationSecurityContext context) {
		return null;
	}

	@Override
	public void expose(ManipulationSecurityExpositionContext context) {
		if (context.getTargetReference() == null) {
			return;
		}

		if (context.getCurrentManipulationType() == ManipulationType.DELETE) {
			if (!deletable(context.getTargetReference().getTypeSignature(), context.getCmdResolver())) {
				SecurityViolationEntry violationEntry = SecurityViolationEntry.T.create();

				violationEntry.setCausingManipulation(context.getCurrentManipulation());
				violationEntry.setEntityReference(context.getTargetReference());
				violationEntry.setDescription("[EntityNotDeletable] Entity: " + context.getTargetReference().getTypeSignature());

				context.addViolationEntry(violationEntry);
			}
		}
	}

	protected boolean deletable(String typeSignature, CmdResolver cmdResolver) {
		return cmdResolver.getMetaData().entityTypeSignature(typeSignature).is(Deletable.T);
	}

	@Override
	public void validate(ManipulationSecurityContext context) {
		// Intentionally left blank
		return;
	}

}
