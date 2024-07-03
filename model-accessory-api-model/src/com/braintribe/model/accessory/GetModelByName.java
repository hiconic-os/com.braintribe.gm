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
package com.braintribe.model.accessory;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.meta.GmMetaModel;

/**
 * Resolves a model by it's {@link GmMetaModel#getName() name}.
 * 
 * @author peter.gazdik
 */
public interface GetModelByName extends ModelRetrievingRequest {

	EntityType<GetModelByName> T = EntityTypes.T(GetModelByName.class);

	static GetModelByName create(String name, String perspective) {
		GetModelByName result = GetModelByName.T.create();
		result.setName(name);
		result.setPerspective(perspective);

		return result;
	}

	/** Name of the model we are getting. */
	String getName();
	void setName(String name);

}
