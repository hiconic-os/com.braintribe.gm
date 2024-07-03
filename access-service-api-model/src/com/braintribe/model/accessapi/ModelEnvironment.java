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

import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * Model Environment is a wrapper entity used to bundle the {@link GmMetaModel}s of a data access and its corresponding workbench access as well as
 * related access IDs.
 * 
 * 
 */
public interface ModelEnvironment extends ModelEnvironmentServices, WorkbenchEnvironment {

	EntityType<ModelEnvironment> T = EntityTypes.T(ModelEnvironment.class);

	GmMetaModel getDataModel();
	void setDataModel(GmMetaModel dataModel);

	GmMetaModel getWorkbenchModel();
	void setWorkbenchModel(GmMetaModel workbenchModel);

	GmMetaModel getServiceModel();
	void setServiceModel(GmMetaModel serviceModel);

}
