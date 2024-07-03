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
package com.braintribe.gm.model.marshaller.api.request;

import com.braintribe.gm.model.marshaller.api.data.MarshallQualification;
import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.eval.EvalContext;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.resource.Resource;
import com.braintribe.model.service.api.ServiceRequest;

/**
 * MarshallRequest is the base type for all types that are intended to marshall (serialize) data into a {@link Resource} based on a marshaller that is selected by mimetype.
 * It inherits from {@link MarshallQualification} in order to let the caller parameterize the marshalling. 
 * @author Dirk Scheffler
 */
@Abstract
public interface MarshallRequest extends MarshallQualification, AbstractMarshallRequest {
	EntityType<MarshallRequest> T = EntityTypes.T(MarshallRequest.class);

    @Override
	EvalContext<? extends Resource> eval(Evaluator<ServiceRequest> evaluator);
}
