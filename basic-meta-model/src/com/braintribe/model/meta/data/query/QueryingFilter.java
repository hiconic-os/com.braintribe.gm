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
package com.braintribe.model.meta.data.query;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.meta.data.EntityTypeMetaData;
import com.braintribe.model.query.Query;
import com.braintribe.model.query.QueryResult;
import com.braintribe.model.query.conditions.Condition;

/**
 * Specifies an extra {@link Condition} to be applied to the {@link QueryResult}, thus filtering the result of the
 * {@link Query}.
 */
public interface QueryingFilter extends EntityTypeMetaData {

	EntityType<QueryingFilter> T = EntityTypes.T(QueryingFilter.class);

	Condition getCondition();
	void setCondition(Condition condition);

}
