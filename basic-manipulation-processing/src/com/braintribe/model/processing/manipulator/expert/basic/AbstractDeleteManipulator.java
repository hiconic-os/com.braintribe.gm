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
package com.braintribe.model.processing.manipulator.expert.basic;

import java.util.Set;

import com.braintribe.common.lcd.UnknownEnumException;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.manipulation.DeleteManipulation;
import com.braintribe.model.generic.manipulation.DeleteMode;
import com.braintribe.model.generic.reflection.GenericModelException;
import com.braintribe.model.meta.data.QualifiedProperty;
import com.braintribe.model.processing.manipulator.api.Manipulator;
import com.braintribe.model.processing.manipulator.api.ManipulatorContext;
import com.braintribe.model.processing.manipulator.api.PropertyReferenceAnalyzer;
import com.braintribe.model.processing.manipulator.api.ReferenceDetacher;
import com.braintribe.model.processing.manipulator.api.ReferenceDetacherException;

/**
 * 
 */
public abstract class AbstractDeleteManipulator<D> implements Manipulator<DeleteManipulation> {

	private PropertyReferenceAnalyzer referenceAnalyzer;

	public void setPropertyReferenceAnalyzer(PropertyReferenceAnalyzer referenceAnalyzer) {
		this.referenceAnalyzer = referenceAnalyzer;
	}

	@Override
	public void apply(DeleteManipulation manipulation, ManipulatorContext context) {
		GenericEntity entity = manipulation.getEntity();

		GenericEntity entityToDelete = context.resolveValue(entity);
		deleteEntity(entityToDelete, manipulation.getDeleteMode());
		context.deleteEntityIfPreliminary(entity);
	}

	public void deleteEntity(GenericEntity entityToDelete, DeleteMode deleteMode) {
		String entitySignature = entityToDelete.entityType().getTypeSignature();

		D deleteContext = onBeforeDelete();

		if (deleteMode != DeleteMode.ignoreReferences) {
			Set<QualifiedProperty> propertiesToDetach = referenceAnalyzer.findReferencingRootProperties(entitySignature);

			switch (deleteMode) {
				case dropReferences:
					detachEntity(entityToDelete, deleteContext, propertiesToDetach, true);
					break;
				case dropReferencesIfPossible:
					detachEntity(entityToDelete, deleteContext, propertiesToDetach, false);
					break;
				case failIfReferenced:
					failIfEntityReferenced(entityToDelete, propertiesToDetach);
					break;
				default:
					throw new UnknownEnumException(deleteMode);
			}
		}

		deleteActualEntity(entityToDelete, deleteMode, deleteContext);
	}

	private void detachEntity(GenericEntity entityToDelete, D deleteContext, Set<QualifiedProperty> propertiesToDetach, boolean force) {
		try {
			onBeforeDetach(deleteContext);

			ReferenceDetacher referenceDetacher = getReferenceDetacher();

			for (QualifiedProperty property : propertiesToDetach) {
				try {
					referenceDetacher.detachReferences(property, entityToDelete, force);

				} catch (Exception e) {
					throw new GenericModelException("Unable to detach entity: " + entityToDelete + " for property: " + property, e);
				}
			}
		} finally {
			onAfterDetach(deleteContext);
		}
	}

	protected void failIfEntityReferenced(GenericEntity entityToDelete, Set<QualifiedProperty> propertiesToDetach) {
		ReferenceDetacher referenceDetacher = getReferenceDetacher();

		for (QualifiedProperty property : propertiesToDetach) {
			try {
				// include info about referees?
				if (referenceDetacher.existsReference(property, entityToDelete))
					throw new GenericModelException("References of entity: " + entityToDelete + "found. Property: " + property);

			} catch (ReferenceDetacherException e) {
				throw new GenericModelException("Unable to resolve references for entity: " + entityToDelete + ", property: " + property, e);
			}
		}
	}

	protected abstract D onBeforeDelete();

	protected abstract void onBeforeDetach(D deleteContext);

	protected abstract void onAfterDetach(D deleteContext);

	protected abstract ReferenceDetacher getReferenceDetacher();

	protected abstract void deleteActualEntity(GenericEntity entityToDelete, DeleteMode deleteMode, D deleteContext);

}
