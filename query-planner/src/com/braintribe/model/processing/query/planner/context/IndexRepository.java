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

import static com.braintribe.utils.lcd.CollectionTools2.newMap;

import java.util.Map;

import com.braintribe.model.processing.query.eval.api.repo.IndexInfo;
import com.braintribe.model.query.From;
import com.braintribe.model.queryplan.index.Index;
import com.braintribe.model.queryplan.index.RepositoryIndex;
import com.braintribe.model.queryplan.index.RepositoryMetricIndex;

/**
 * 
 */
class IndexRepository {

	private final QueryPlannerContext context;
	private final Map<IndexInfo, Index> map;

	public IndexRepository(QueryPlannerContext context) {
		this.context = context;
		this.map = newMap();
	}

	public <T extends Index> T acquireIndex(From from, IndexInfo indexInfo) {
		Index result = map.get(indexInfo);

		if (result == null) {
			result = newIndexFor(from, indexInfo);
			map.put(indexInfo, result);
		}

		return (T) result;
	}

	private Index newIndexFor(From from, IndexInfo indexInfo) {
		RepositoryIndex index = indexInfo.hasMetric() ? RepositoryMetricIndex.T.create() : RepositoryIndex.T.create();

		index.setIndexId(indexInfo.getIndexId());
		index.setTupleComponentIndex(context.sourceManager().indexForSource(from));

		return index;
	}

}
