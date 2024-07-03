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
package com.braintribe.model.exchangeapi;

import com.braintribe.model.descriptive.HasDescription;
import com.braintribe.model.descriptive.HasName;
import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.eval.EvalContext;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.service.api.ServiceRequest;

@Abstract
public interface ExportRequest extends ExchangeRequest, HasName, HasDescription {

	EntityType<ExportRequest> T = EntityTypes.T(ExportRequest.class);
	
	void setDescriptor(ExportDescriptor descriptor);
	ExportDescriptor getDescriptor();

	@Override
	EvalContext<? extends ExportResponse> eval(Evaluator<ServiceRequest> evaluator);
}
