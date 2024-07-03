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

import com.braintribe.model.access.crud.api.DataWriter;
import com.braintribe.model.generic.GenericEntity;

/**
 * A {@link DataWriter} expert that is responsible for persisting deletions of instances of the
 * type he is registered for. 
 * 
 * @author gunther.schenk
 */
public interface EntityDeleter<T extends GenericEntity> extends DataWriter<T> {

	/**
	 * Delete an instance of registered type using the informations provided by the passed context. <br/>
	 * Note, that the entity provided by the context {@link EntityDeletionContext#getDeleted()} is bound to a local session and
	 * any modification on it is tracked and finally reported back as an induced manipulation to the caller. 
	 */	
	void deleteEntity (EntityDeletionContext<T> context);
}
