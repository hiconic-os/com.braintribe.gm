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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.braintribe.model.generic.GenericEntity;

/**
 * Info about all referees for one entity (this entity is not referenced from within this class though).
 */
public class RefereeData {

	public Map<GenericEntity, Counter> referees = new HashMap<GenericEntity, Counter>();
	public int totalReferences;

	public void addReferee(GenericEntity referee, int count) {
		acquireCounterFor(referee).count += count;

		this.totalReferences += count;
	}

	public void removeReferee(GenericEntity referee, int count) {
		Counter counter = referees.get(referee);

		counter.count -= count;

		if (counter.count == 0) {
			referees.remove(referee);
		}

		this.totalReferences -= count;
	}

	public void add(RefereeData other) {
		for (Entry<GenericEntity, Counter> entry: other.referees.entrySet()) {
			GenericEntity referee = entry.getKey();
			Counter counter = entry.getValue();

			acquireCounterFor(referee).count += counter.count;
		}

		this.totalReferences += other.totalReferences;
	}

	public void subtract(RefereeData other) {
		for (Entry<GenericEntity, Counter> entry: other.referees.entrySet()) {
			GenericEntity referee = entry.getKey();
			Counter otherCounter = entry.getValue();

			Counter counter = referees.get(referee);
			counter.count -= otherCounter.count;

			if (counter.count == 0) {
				referees.remove(referee);
			}
		}

		this.totalReferences -= other.totalReferences;
	}

	private Counter acquireCounterFor(GenericEntity referee) {
		Counter counter = referees.get(referee);

		if (counter == null) {
			counter = new Counter();
			referees.put(referee, counter);
		}

		return counter;
	}

	@Override
	public String toString() {
		return referees.toString();
	}

}
