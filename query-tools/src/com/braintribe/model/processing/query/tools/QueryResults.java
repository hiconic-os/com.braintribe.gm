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
package com.braintribe.model.processing.query.tools;

import java.util.List;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.query.EntityQueryResult;
import com.braintribe.model.query.PropertyQueryResult;
import com.braintribe.model.query.SelectQueryResult;

/**
 * @author peter.gazdik
 */
public interface QueryResults {

	public static SelectQueryResult selectQueryResult(List<?> results, boolean hasMore) {
		SelectQueryResult result = SelectQueryResult.T.create();
		result.setResults((List<Object>) results);
		result.setHasMore(hasMore);

		return result;
	}

	static EntityQueryResult entityQueryResult(List<?> entities, boolean hasMore) {
		EntityQueryResult entityQueryResult = EntityQueryResult.T.create();
		entityQueryResult.setEntities((List<GenericEntity>) entities);
		entityQueryResult.setHasMore(hasMore);

		return entityQueryResult;
	}

	static PropertyQueryResult propertyQueryResult(Object value, boolean hasMore) {
		PropertyQueryResult result = PropertyQueryResult.T.create();
		result.setPropertyValue(value);
		result.setHasMore(hasMore);

		return result;
	}
}
