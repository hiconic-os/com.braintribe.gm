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
package com.braintribe.model.processing.query.planner.context;

import com.braintribe.model.query.SelectQuery;

/**
 * @deprecated You are probably using this with other deprecated classes. Get rid of them and you won't need this either.
 */
@Deprecated
public interface QueryValueResolver {

	/**
	 * Returns true iff given instance if member of {@link SelectQuery#getEvaluationExcludes()}.
	 * 
	 * @see SelectQuery#setEvaluationExcludes(java.util.Set)
	 */
	boolean isEvaluationExclude(Object value);

}
