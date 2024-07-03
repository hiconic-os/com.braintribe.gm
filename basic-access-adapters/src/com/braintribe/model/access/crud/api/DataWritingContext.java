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
package com.braintribe.model.access.crud.api;

import com.braintribe.model.access.BasicAccessAdapter;
import com.braintribe.model.access.BasicAccessAdapter.AdapterManipulationReport;
import com.braintribe.model.access.crud.api.create.EntityCreationContext;
import com.braintribe.model.access.crud.api.delete.EntityDeletionContext;
import com.braintribe.model.access.crud.api.update.EntityUpdateContext;
import com.braintribe.model.generic.GenericEntity;

/**
 * Base type for all context objects passed to individual {@link DataWriter}
 * implementations.
 * 
 * @see EntityCreationContext
 * @see EntityUpdateContext
 * @see EntityDeletionContext
 * 
 * @author gunther.schenk
 */
public interface DataWritingContext<T extends GenericEntity> extends CrudExpertContext<T> {

	/**
	 * @return the full {@link AdapterManipulationReport} provided by the
	 *         {@link BasicAccessAdapter}
	 */
	AdapterManipulationReport getManipulationReport();
}
