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
package com.braintribe.model.processing.am;

import com.braintribe.model.generic.GenericEntity;

public class ReferenceManager extends SimpleReferenceTracker {

	@Override
	public void addReference(GenericEntity referee, GenericEntity entity) {
		addReferenceImpl(referee, entity, 1);
	}

	@Override
	public void removeReference(GenericEntity referee, GenericEntity entity) {
		removeReference(referee, entity, 1);
	}

	protected void addReference(GenericEntity referee, GenericEntity entity, int count) {
		RefereeData refereeData = addReferenceImpl(referee, entity, count);
		
		if (refereeData.totalReferences == 1) {
			onJoin(entity);
		}
	}
	
	protected void removeReference(GenericEntity referee, GenericEntity entity, int count) {
		RefereeData refereeData = removeReferenceImpl(referee, entity, count);
		
		if (refereeData.totalReferences == 0) {
			onLeave(entity);
		}
	}
	
	protected boolean addReferences(GenericEntity entity, RefereeData otherRefereeData) {
		RefereeData refereeData = entities.get(entity);

		if (refereeData == null) {
			refereeData = new RefereeData();
			entities.put(entity, refereeData);
		}
		refereeData.add(otherRefereeData);

		if (refereeData.totalReferences == otherRefereeData.totalReferences) {
			onJoin(entity);
			return true;
		}

		return false;
	}

	protected boolean removeReferences(GenericEntity entity, RefereeData otherRefereeData) {
		RefereeData refereeData = entities.get(entity);

		if (refereeData == null)
			throw new IllegalStateException("counter of sync for " + entity);

		refereeData.subtract(otherRefereeData);

		if (refereeData.totalReferences == 0) {
			entities.remove(entity);
			onLeave(entity);
			return true;
		}

		return false;
	}

	@Override
	public boolean hasReference(GenericEntity entity) {
		return entities.containsKey(entity);
	}

	/**
	 * @param entity referencedEntity
	 */
	protected void onJoin(GenericEntity entity) {
		// implement in sub-type
	}

	/**
	 * @param entity referencedEntity
	 */
	protected void onLeave(GenericEntity entity) {
		// implement in sub-type
	}
}
