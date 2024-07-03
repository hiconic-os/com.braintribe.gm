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
package com.braintribe.model.access.crud.api.create;

import com.braintribe.model.access.BasicAccessAdapter.AdapterManipulationReport;
import com.braintribe.model.access.crud.api.DataWritingContext;
import com.braintribe.model.generic.GenericEntity;

/**
 * A {@link DataWritingContext} implementation provided to {@link EntityCreator}
 * experts containing necessary informations on newly created instances.
 * 
 * @author gunther.schenk
 */
public interface EntityCreationContext<T extends GenericEntity> extends DataWritingContext<T> {

	/**
	 * @return the locally newly created instance.
	 */
	T getCreated();

	/**
	 * Static helper method to build a new {@link EntityCreationContext} instance.
	 */
	static <T extends GenericEntity> EntityCreationContext<T> create(T created, AdapterManipulationReport report) {
		return new EntityCreationContext<T>() {
			@Override
			public T getCreated() {
				return created;
			}

			@Override
			public AdapterManipulationReport getManipulationReport() {
				return report;
			}
		};
	}

}
