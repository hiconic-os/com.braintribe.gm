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
package com.braintribe.model.processing.query.planner;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.processing.query.eval.api.repo.Repository;
import com.braintribe.model.processing.query.planner.context.QueryPlannerContext;
import com.braintribe.model.processing.query.planner.core.QueryPlannerCore;
import com.braintribe.model.processing.query.tools.SelectQueryNormalizer;
import com.braintribe.model.query.SelectQuery;
import com.braintribe.model.queryplan.QueryPlan;

/**
 * A {@link SelectQuery} planner - builds a {@link QueryPlan} for given select query.
 * 
 * This class is thread-safe and concurrent as long as the provided {@link Repository repository} is thread-safe.
 */
public class QueryPlanner {

	private final Repository repository;
	private boolean ignorePartitions;

	public QueryPlanner(Repository repository) {
		this.repository = repository;
		this.ignorePartitions = repository.defaultPartition() != null;
	}

	/**
	 * Builds a {@link QueryPlan} for given {@link SelectQuery}.
	 */
	public QueryPlan buildQueryPlan(SelectQuery query) {
		/* We need this for (at least) the purpose of selecting collections. The normalizer replaces a selection of a
		 * collection with a join, thus forcing the result to have one row for each member of the collection, not just
		 * one row, where one slot of the tuple is the entire collection. This makes it compatible with hibernate. */
		query = new SelectQueryNormalizer(query, false, false) //
				.defaultPartition(defaultPartition()) //
				.mappedPropertyIndicator(this::isPropertyMapped)
				.normalize();

		QueryPlannerContext context = new QueryPlannerContext(query, repository);

		return new QueryPlannerCore(context, query).buildQueryPlan();
	}

	/**
	 * Ignoring partitions means ignoring {@link GenericEntity#partition} property of each entity, so in that case we
	 * actually want to do the replace.
	 */
	private String defaultPartition() {
		return ignorePartitions ? repository.defaultPartition() : null;
	}

	
	/** If ignoring partitions, we treat them as if they were not mapped in the query normalizer, thus their references are replaced. */
	private boolean isPropertyMapped(@SuppressWarnings("unused") String typeSignature, String property) {
		return ignorePartitions ? !GenericEntity.partition.equals(property) : true;
	}
	
	public void ignorePartitions(boolean ignorePartitions) {
		this.ignorePartitions = ignorePartitions;
	}

}
