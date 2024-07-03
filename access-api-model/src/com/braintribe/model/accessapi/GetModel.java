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


import com.braintribe.model.generic.annotation.meta.Description;
import com.braintribe.model.generic.annotation.meta.Mandatory;
import com.braintribe.model.generic.annotation.meta.Name;
import com.braintribe.model.generic.eval.EvalContext;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.service.api.ServiceRequest;


@Description("Returns the model that is currently deployed at an access.")
public interface GetModel extends PersistenceRequest {

	final EntityType<GetModel> T = EntityTypes.T(GetModel.class);

	
	@Override
	@Mandatory
	@Name("accessId")
	@Description("The id of the access that should be asked for its model environment.")
	String getServiceId();
	
	@Override
	EvalContext<? extends GmMetaModel> eval(Evaluator<ServiceRequest> evaluator);

	@Override
	default PersistenceRequestType persistenceRequestType() {
		return PersistenceRequestType.GetModel;
	}
}
