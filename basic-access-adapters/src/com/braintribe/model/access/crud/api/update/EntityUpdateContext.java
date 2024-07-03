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
package com.braintribe.model.access.crud.api.update;

import java.util.Set;

import com.braintribe.model.access.BasicAccessAdapter;
import com.braintribe.model.access.BasicAccessAdapter.AdapterManipulationReport;
import com.braintribe.model.access.crud.api.DataWritingContext;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.Property;

/**
 * A {@link DataWritingContext} implementation provided to {@link EntityUpdater} experts containing 
 * necessary informations on updated entities.
 *  
 * @author gunther.schenk
 */
public interface EntityUpdateContext<T extends GenericEntity> extends DataWritingContext<T> {

	/**
	 * @return the locally updated instance.  
	 */
	T getUpdated();
	
	/**
	 * @return the collection of properties which have been updated.
	 */
	Set<Property> getUpdatedProperties();

	/**
	 * Static helper method to build a new {@link EntityUpdateContext} instance.
	 *  
	 * @param updated the locally updated instance.
	 * @param report the {@link AdapterManipulationReport} provided by the {@link BasicAccessAdapter}
	 */	
	static <T extends GenericEntity> EntityUpdateContext<T> create(T updated, AdapterManipulationReport report) {
		return new EntityUpdateContext<T>() {
			@Override
			public T getUpdated() {
				return updated;
			}
			@Override
			public Set<Property> getUpdatedProperties() {
				return report.getTouchedPropertiesOfEntities().get(updated);
			}
			@Override
			public AdapterManipulationReport getManipulationReport() {
				return report;
			}
		};
	}
	

}
