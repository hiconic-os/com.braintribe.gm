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
package com.braintribe.model.generic.sync;

import static com.braintribe.model.generic.manipulation.util.ManipulationBuilder.compound;

import java.util.HashSet;
import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.manipulation.CompoundManipulation;

public class SynchronizationContext {
	private Object syncedValue;
	private Set<GenericEntity> newEntities = new HashSet<GenericEntity>();
	private CompoundManipulation updateManipulation = compound();
	
	public CompoundManipulation getUpdateManipulation() {
		if (updateManipulation == null) {
			updateManipulation = compound();
		}

		return updateManipulation;
	}

	public void registerNewEntity(GenericEntity entity) {
		newEntities.add(entity);
	}
	
	public Set<GenericEntity> getNewEntities() {
		return newEntities;
	}
	
	public void setSyncedResult(Object syncedResult) {
		this.syncedValue = syncedResult;
	}

	@SuppressWarnings("unchecked")
	public <T> T getSyncedResult() {
		return (T)syncedValue;
	}
}
 
