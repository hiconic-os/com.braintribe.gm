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

import com.braintribe.model.accessapi.AccessRequest;
import com.braintribe.model.generic.eval.EvalContext;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.service.api.ServiceRequest;

/**
 * <tt>SqlSource</tt>s got a new property <tt>blobId</tt> which must now be set in every sql based access. This request
 * can be used to trigger such an update round for an access. This request is usually triggered automatically and can be
 * removed again as soon as we are confident that all accesses are up to date.
 * 
 * @author Neidhart.Orlich
 *
 */
public interface FixSqlSources extends AccessRequest {

	EntityType<FixSqlSources> T = EntityTypes.T(FixSqlSources.class);

	@Override
	EvalContext<? extends FixSqlSourcesResponse> eval(Evaluator<ServiceRequest> evaluator);

	boolean getForceUpdate();
	void setForceUpdate(boolean forceUpdate);
}
