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

import com.braintribe.model.generic.annotation.Initializer;
import com.braintribe.model.generic.annotation.meta.Description;
import com.braintribe.model.generic.annotation.meta.Name;
import com.braintribe.model.generic.eval.EvalContext;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.resource.Resource;
import com.braintribe.model.service.api.ServiceRequest;

@Description("Resource upload request.")
@Name("Resource Upload")
public interface ResourceUploadRequest extends ResourceStreamingRequest {

	EntityType<ResourceUploadRequest> T = EntityTypes.T(ResourceUploadRequest.class);

	@Description("Determines the name of the new Resource created in tribefire. This parameter is only required when the upload is performed with "
			+ "the <b>application/x-www-form-urlencoded</b> body content type.")
	String getFileName();
	void setFileName(String fileName);

	@Description("When uploading a new resource it will be created in tribefire associated with a Resource entity instance. "
			+ "This new Resource instance is returned in the body of the response and is by default of the type <b>application/json</b>. "
			+ "You can determine the type that response should take by using this parameter.")
	@Initializer("'application/json'")
	String getResponseMimeType();
	void setResponseMimeType(String responseMimeType);

	@Description("Resource upload resource use case.")
	String getUseCase();
	void setUseCase(String useCase);

	@Description("Mime Type a resource format identifier.")
	String getMimeType();
	void setMimeType(String mimeType);

	@Description("The MD5 digital fingerprint of a file.")
	String getMd5();
	void setMd5(String md5);

	@Description("Fully qualified name of EntityType that extends com.braintribe.model.resource.source.ResourceSource.")
	String getSourceType();
	void setSourceType(String sourceType);

	@Override
	EvalContext<Resource> eval(Evaluator<ServiceRequest> evaluator);

}
