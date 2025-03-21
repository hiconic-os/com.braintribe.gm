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
package com.braintribe.testing.internal.tribefire.helpers.comparison;

import java.util.HashMap;
import java.util.Map;

import com.braintribe.common.lcd.Pair;
import com.braintribe.model.generic.GenericEntity;

/**
 * @deprecated this class was moved to {@link com.braintribe.model.processing.test.tools.comparison.ComparedEntitiesRegistry}
 */
@Deprecated
class ComparedEntitiesRegistry {
	private final Map<Pair<GenericEntity, GenericEntity>, Status> alreadyCheckedEntities = new HashMap<>();

	enum Status {
		EQUAL,
		NOT_EQUAL,
		BEING_COMPARED,
		NOT_CHECKED
	}

	/**
	 * @return the pair of entities from the registry map, regardless of the order - or null if not registered yet
	 */
	private Pair<GenericEntity, GenericEntity> getPair(GenericEntity first, GenericEntity second) {
		Pair<GenericEntity, GenericEntity> pairStraight = new Pair<>(first, second);
		Pair<GenericEntity, GenericEntity> pairReversed = new Pair<>(second, first);

		if (alreadyCheckedEntities.containsKey(pairStraight)) {
			return pairStraight;
		} else if (alreadyCheckedEntities.containsKey(pairReversed)) {
			return pairReversed;
		} else {
			return null;
		}
	}

	/**
	 * order of parameters does not matter
	 */
	public void registerAs(GenericEntity first, GenericEntity second, Status status) {
		Pair<GenericEntity, GenericEntity> pair = getPair(first, second);

		if (pair == null) {
			pair = new Pair<>(first, second);
		}

		alreadyCheckedEntities.put(pair, status);
	}

	/**
	 * order of parameters does not matter
	 */
	public Status getStatus(GenericEntity first, GenericEntity second) {
		Pair<GenericEntity, GenericEntity> pair = getPair(first, second);

		if (pair == null) {
			return Status.NOT_CHECKED;
		}

		return alreadyCheckedEntities.get(pair);
	}
	
	public void clear() {
		alreadyCheckedEntities.clear();
	}
	
	/**
	 * Registers the two given entities in the registry as currently being compared if they are not already in the registry yet 
	 * 
	 * @return <b>false</b>: the entities are equal or already currently being compared -> comparison should be canceled and for the moment 
	 * assumed that they are equal<br>
	 * <b>true:</b> entities are known to be not equal or not known at all -> go on with comparison
	 */
	public boolean startCompare(GenericEntity first, GenericEntity second) {

		Status registeredEqualityStatus = getStatus(first, second);
		if (registeredEqualityStatus != Status.NOT_CHECKED) {
			return registeredEqualityStatus == Status.NOT_EQUAL;
		} else {
			registerAs(first, second, Status.BEING_COMPARED);
			return true;
		}

	}
}
