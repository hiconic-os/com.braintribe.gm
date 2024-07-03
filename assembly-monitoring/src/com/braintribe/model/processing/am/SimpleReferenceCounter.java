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

public class SimpleReferenceCounter implements ReferenceCounter {
	private Map<GenericEntity, Counter> entities = new HashMap<GenericEntity, Counter>();
	
	public Set<GenericEntity> getEntities() {
		return Collections.unmodifiableSet(entities.keySet());
	}
	
	public Map<GenericEntity, Counter> getReferenceMap() {
		return Collections.unmodifiableMap(entities);
	}
	
	public int getReferenceCount(GenericEntity entity) {
		Counter counter = entities.get(entity);
		return counter != null? counter.count: 0;
	}
	
	@Override
	public void addReference(GenericEntity entity) {
		Counter counter = entities.get(entity);
		
		if (counter == null) {
			counter = new Counter();
			entities.put(entity, counter);
		}
		counter.count++;
	}
	
	@Override
	public void removeReference(GenericEntity entity) {
		Counter counter = entities.get(entity);
		
		counter.count--;
		
		if (counter.count == 0) {
			entities.remove(entity);
		}
	}
	
	public boolean hasReference(GenericEntity entity) {
		return entities.containsKey(entity);
	}
}
