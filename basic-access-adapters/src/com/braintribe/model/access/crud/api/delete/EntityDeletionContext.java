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
package com.braintribe.model.access.crud.api.delete;

import com.braintribe.model.access.BasicAccessAdapter.AdapterManipulationReport;
import com.braintribe.model.access.crud.api.DataWritingContext;
import com.braintribe.model.generic.GenericEntity;

/**
* {@link DataWritingContext} implementation provided to {@link EntityDeleter} experts containing 
* necessary informations on deleted instances.
*  
* @author gunther.schenk
*/
public interface EntityDeletionContext<T extends GenericEntity> extends DataWritingContext<T> {

	/**
	 * @return the local instance that should be deleted.  
	 */
	T getDeleted();

	/**
	 * Static helper method to build a new {@link EntityDeletionContext} instance.
	 */
	static <T extends GenericEntity> EntityDeletionContext<T> create (T deleted, AdapterManipulationReport report) {
		return new EntityDeletionContext<T>() {
			@Override
			public T getDeleted() {
				return deleted;
			}
			@Override
			public AdapterManipulationReport getManipulationReport() {
				return report;
			}
		};
	}
	
	

}
