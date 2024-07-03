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
package com.braintribe.model.accessapi;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.manipulation.Manipulation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface ManipulationResponse extends GenericEntity {

	final EntityType<ManipulationResponse> T = EntityTypes.T(ManipulationResponse.class);

	void setInducedManipulation(Manipulation inducedManipulation);
	Manipulation getInducedManipulation();

	/**
	 * RollbackManipulation is an inverse to the manipulation which was sent in the corresponding
	 * {@link ManipulationRequest}, which can be used for rollback - typically in case this request/response is just one
	 * part of some bigger transaction.
	 */
	Manipulation getRollbackManipulation();
	void setRollbackManipulation(Manipulation rollbackManipulation);

}
