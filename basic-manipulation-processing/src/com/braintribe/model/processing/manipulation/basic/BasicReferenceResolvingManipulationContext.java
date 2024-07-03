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
package com.braintribe.model.processing.manipulation.basic;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.builder.vd.VdBuilder;
import com.braintribe.model.generic.manipulation.AtomicManipulation;
import com.braintribe.model.generic.manipulation.ChangeValueManipulation;
import com.braintribe.model.generic.manipulation.ManipulationType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.value.EntityReference;
import com.braintribe.model.generic.value.PersistentEntityReference;
import com.braintribe.model.processing.manipulation.api.ReferenceResolvingManipulationContext;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;

/**
 * This only works with REMOTE manipulations.
 * 
 * @see ReferenceResolvingManipulationContext
 */
public class BasicReferenceResolvingManipulationContext extends BasicMutableManipulationContext implements ReferenceResolvingManipulationContext {

	protected PersistenceGmSession session;
	protected EntityReference normalizedNewReference;

	private GenericEntity targetInstance;
	private boolean isTargetInstanceResolved;

	public BasicReferenceResolvingManipulationContext(PersistenceGmSession session) {
		this.session = session;
	}

	@Override
	public void setCurrentManipulation(AtomicManipulation currentManipulation) {
		super.setCurrentManipulation(currentManipulation);

		normalizedNewReference = normalizedNewReference();

		targetInstance = null;
		isTargetInstanceResolved = false;
	}

	private EntityReference normalizedNewReference() {
		if (currentManipulationType != ManipulationType.CHANGE_VALUE) {
			return null;
		}

		if (!GenericEntity.id.equals(getTargetPropertyName())) {
			return null;
		}

		ChangeValueManipulation cvm = (ChangeValueManipulation) currentManipulation;
		Object newId = cvm.getNewValue();

		if (newId == null) {
			/* We could actually throw an exception here, does it make any sense to set an id to null (in a stack of
			 * normalized manipulations)? That can only happen if there is no Instantiation in the stack. The following
			 * manipulation would have to reference the entity with a preliminary id, but how do we know which one it
			 * is, if we did not have any Instantiation that could be used to bind the id to the instance? */
			return null;
		}

		PersistentEntityReference ref = VdBuilder.persistentReference(getTargetSignature(), newId, null);

		return getNormalizeReference(ref);
	}

	@Override
	public <T extends GenericEntity> T getTargetInstance() {
		if (isTargetInstanceResolved)
			return (T) targetInstance;

		isTargetInstanceResolved = true;

		return (T) (targetInstance = targetInstance());
	}

	private GenericEntity targetInstance() {
		EntityReference ref = getTargetReference();

		return ref != null ? resolveReference(ref) : null;
	}

	@Override
	public GenericEntity resolveReference(EntityReference reference) {
		return session.query().entity(reference).find();
	}

	@Override
	public <T> T getTargetPropertyValue() {
		Property p = getTargetProperty();

		if (p == null || getTargetInstance() == null)
			return null;

		return p.get(targetInstance);
	}

	@Override
	public EntityReference getNormalizedNewReference() {
		return normalizedNewReference;
	}

}
