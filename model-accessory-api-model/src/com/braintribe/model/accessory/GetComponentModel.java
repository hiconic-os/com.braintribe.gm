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

import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.annotation.meta.Mandatory;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.meta.data.components.ModelExtension;

/**
 * Resolves a model of a deployable component, identified by it's {@link #getExternalId() externalId}.
 * 
 * @see GetAccessModel
 * @see GetServiceModel
 * 
 * @author peter.gazdik
 */
@Abstract
public interface GetComponentModel extends ModelRetrievingRequest {

	EntityType<GetComponentModel> T = EntityTypes.T(GetComponentModel.class);

	/** ExternalId of the deployable for which we are getting the model. */
	@Mandatory
	String getExternalId();
	void setExternalId(String externalId);

	/**
	 * If <tt>true</tt>, the extended model will be returned, i.e. one obtained after {@link ModelExtension} meta-data has been applied on top of the
	 * model configured on the deployable.
	 */
	boolean getExtended();
	void setExtended(boolean extended);

}
