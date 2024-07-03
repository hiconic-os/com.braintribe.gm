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
import com.braintribe.model.generic.eval.EvalContext;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.resource.Resource;
import com.braintribe.model.service.api.AuthorizedRequest;
import com.braintribe.model.service.api.DispatchableRequest;
import com.braintribe.model.service.api.DomainRequest;
import com.braintribe.model.service.api.ServiceRequest;

public interface GetPreview extends DomainRequest, DispatchableRequest, AuthorizedRequest {

	EntityType<GetPreview> T = EntityTypes.T(GetPreview.class);

	String getInstanceTypeSignature();
	void setInstanceTypeSignature(String instanceTypeSignature);

	String getInstanceId();
	void setInstanceId(String instanceId);

	String getTimestamp();
	void setTimestamp(String timestamp);

	Integer getPreferredHeight();
	void setPreferredHeight(Integer preferredHeight);

	Integer getPreferredWidth();
	void setPreferredWidth(Integer preferredWidth);

	String getPreferredMimeType();
	void setPreferredMimeType(String preferredMimeType);

	@Initializer("enum(com.braintribe.model.resourceapi.request.PreviewType,STANDARD)")
	PreviewType getPreviewType();
	void setPreviewType(PreviewType previewType);

	@Override
	EvalContext<? extends Resource> eval(Evaluator<ServiceRequest> evaluator);

}
