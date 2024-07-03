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
package com.braintribe.model.resourceapi.request;

import com.braintribe.model.generic.annotation.meta.Description;
import com.braintribe.model.generic.annotation.meta.Mandatory;
import com.braintribe.model.generic.annotation.meta.Name;
import com.braintribe.model.generic.eval.EvalContext;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.resource.Resource;
import com.braintribe.model.service.api.ServiceRequest;

@Description("Resource delete request.")
@Name("Resource Delete")
public interface ResourceDeleteRequest extends ResourceStreamingRequest {

	EntityType<ResourceDeleteRequest> T = EntityTypes.T(ResourceDeleteRequest.class);

	@Mandatory
	@Description("The ID of the resource that should be downloaded. This can be obtained by viewing the resource in Control Center "
			+ "or using a REST query to discover said resource.")
	String getResourceId();
	void setResourceId(String resourceId);

	@Description("Resource delete use case.")
	String getUseCase();
	void setUseCase(String useCase);

	@Override
	EvalContext<Resource> eval(Evaluator<ServiceRequest> evaluator);

}
