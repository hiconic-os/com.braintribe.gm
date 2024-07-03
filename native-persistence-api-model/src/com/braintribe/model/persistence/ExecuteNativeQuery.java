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
package com.braintribe.model.persistence;

import java.util.List;

import com.braintribe.model.generic.eval.EvalContext;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.generic.pr.criteria.TraversingCriterion;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.service.api.ServiceRequest;

/**
 * @author peter.gazdik
 */
public interface ExecuteNativeQuery extends NativePersistenceRequest {

	EntityType<ExecuteNativeQuery> T = EntityTypes.T(ExecuteNativeQuery.class);

	String getQuery();
	void setQuery(String query);

	Integer getMaxResults();
	void setMaxResults(Integer maxResults);

	Integer getFirstResultOffset();
	void setFirstResultOffset(Integer firstResultOffset);

	List<NativeQueryParameter> getParameters();
	void setParameters(List<NativeQueryParameter> parameters);

	TraversingCriterion getTraversingCriterion();
	void setTraversingCriterion(TraversingCriterion traversingCriterion);

	@Override
	EvalContext<List<Object>> eval(Evaluator<ServiceRequest> evaluator);

}
