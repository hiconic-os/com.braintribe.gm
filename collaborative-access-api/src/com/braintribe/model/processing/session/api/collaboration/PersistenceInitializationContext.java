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
package com.braintribe.model.processing.session.api.collaboration;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.processing.session.api.managed.ManagedGmSession;
import com.braintribe.model.smoodstorage.stages.PersistenceStage;

/**
 * @author peter.gazdik
 */
public interface PersistenceInitializationContext {

	void setCurrentPersistenceStage(PersistenceStage stage);

	/**
	 * Manipulations applied on the returned session are directly reflected in the persistence layer that is being
	 * initialized.
	 */
	ManagedGmSession getSession();
	
	String getAccessId();

	PersistenceStage getStage(GenericEntity entity);
	
	default <T> T getAttribute(@SuppressWarnings("unused") String key) {
		return null;
	}

}
