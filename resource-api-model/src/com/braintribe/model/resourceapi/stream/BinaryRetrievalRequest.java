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
package com.braintribe.model.resourceapi.stream;

import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.eval.EvalContext;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.resource.Resource;
import com.braintribe.model.resourceapi.base.BinaryRequest;
import com.braintribe.model.resourceapi.stream.condition.StreamCondition;
import com.braintribe.model.resourceapi.stream.range.StreamRange;
import com.braintribe.model.service.api.ServiceRequest;

@Abstract
public interface BinaryRetrievalRequest extends BinaryRequest {

	EntityType<BinaryRetrievalRequest> T = EntityTypes.T(BinaryRetrievalRequest.class);

	Resource getResource();
	void setResource(Resource resource);

	StreamCondition getCondition();
	void setCondition(StreamCondition condition);
	
	StreamRange getRange();
	void setRange(StreamRange range);

	@Override
	EvalContext<? extends BinaryRetrievalResponse> eval(Evaluator<ServiceRequest> evaluator);

}
