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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.braintribe.model.generic.GenericEntity;

public class SimpleReferenceTracker implements ReferenceTracker {

	protected Map<GenericEntity, RefereeData> entities = new HashMap<GenericEntity, RefereeData>();

	protected Set<GenericEntity> entitiesView = null;
	protected Map<GenericEntity, RefereeData> entitiesDataView = null;

	public Set<GenericEntity> getEntities() {
		if (entitiesView == null) {
			entitiesView = Collections.unmodifiableSet(entities.keySet());
		}

		return entitiesView;
	}

	public Map<GenericEntity, RefereeData> getReferenceMap() {
		if (entitiesDataView == null) {
			entitiesDataView = Collections.unmodifiableMap(entities);
		}

		return entitiesDataView;
	}

	public int getReferenceCount(GenericEntity entity) {
		RefereeData refereeData = entities.get(entity);
		return refereeData != null ? refereeData.totalReferences : 0;
	}

	@Override
	public void addReference(GenericEntity referee, GenericEntity entity) {
		addReferenceImpl(referee, entity, 1);
	}

	protected RefereeData addReferenceImpl(GenericEntity referee, GenericEntity entity, int count) {
		RefereeData refereeData = entities.get(entity);

		if (refereeData == null) {
			refereeData = new RefereeData();
			entities.put(entity, refereeData);
		}

		refereeData.addReferee(referee, count);

		return refereeData;
	}

	@Override
	public void removeReference(GenericEntity referee, GenericEntity entity) {
		removeReferenceImpl(referee, entity, 1);
	}

	protected RefereeData removeReferenceImpl(GenericEntity referee, GenericEntity entity, int count) {
		RefereeData refereeData = entities.get(entity);
		refereeData.removeReferee(referee, count);
		
		if (refereeData.totalReferences == 0) {
			entities.remove(entity);
			
		} else if (refereeData.totalReferences < 0) {
			throw new IllegalStateException("We hava somehow achieved a negative number of references.");
		}
		
		return refereeData;
	}

	public boolean hasReference(GenericEntity entity) {
		return entities.containsKey(entity);
	}
}
