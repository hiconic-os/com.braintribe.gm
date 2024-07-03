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
import com.braintribe.model.generic.eval.EvalContext;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.processing.session.api.managed.ModelAccessoryFactory;
import com.braintribe.model.service.api.AuthorizedRequest;
import com.braintribe.model.service.api.ServiceRequest;

/**
 * Resolves a {@link GmMetaModel model} based on some parameters.
 * 
 * @see GetComponentModel
 * @see GetModelByName
 * 
 * @author peter.gazdik
 */
@Abstract
public interface ModelRetrievingRequest extends AuthorizedRequest {

	EntityType<ModelRetrievingRequest> T = EntityTypes.T(ModelRetrievingRequest.class);

	/**
	 * Perspective information is used to cut irrelevant meta-data.
	 * 
	 * @see ModelAccessoryFactory#forPerspective(String)
	 */
	String getPerspective();
	void setPerspective(String perspective);

	@Override
	EvalContext<? extends GmMetaModel> eval(Evaluator<ServiceRequest> evaluator);

}
