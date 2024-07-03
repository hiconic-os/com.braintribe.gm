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

import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.annotation.Initializer;
import com.braintribe.model.generic.annotation.meta.Description;
import com.braintribe.model.generic.annotation.meta.Mandatory;
import com.braintribe.model.generic.eval.EvalContext;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.resource.Resource;
import com.braintribe.model.service.api.AuthorizedRequest;
import com.braintribe.model.service.api.ServiceRequest;

import java.util.Map;

@Abstract
public interface ResourceStreamingRequest extends AuthorizedRequest {

	EntityType<ResourceStreamingRequest> T = EntityTypes.T(ResourceStreamingRequest.class);

	@Mandatory
	@Description("The external ID of the access where the resource that is being downloaded can be found.")
	@Initializer("'cortex'")
	String getAccessId();
	void setAccessId(String accessId);

	@Description("Context.")
	Map<String, String> getContext();
	void setContext(Map<String, String> context);

	@Override
	EvalContext<Resource> eval(Evaluator<ServiceRequest> evaluator);

}
