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

import com.braintribe.model.processing.query.eval.api.repo.IndexInfo;
import com.braintribe.model.query.From;
import com.braintribe.model.queryplan.set.IndexOrderedSet;

/**
 * @author peter.gazdik
 */
public class OrderedSourceDescriptor {

	public final From from;
	public final IndexInfo indexInfo;
	public final boolean descending;
	public final int index;

	public OrderedSourceDescriptor(From from, IndexInfo indexInfo, boolean descending, int index) {
		this.from = from;
		this.indexInfo = indexInfo;
		this.descending = descending;
		this.index = index;
	}

	public String orderedProperty() {
		return indexInfo.getEntitySignature() + "#" + indexInfo.getPropertyName() + "(" + index + ")";
	}

	public IndexOrderedSet toIndexOrderedSet(QueryPlannerContext context) {
		IndexOrderedSet result = toIos(context);
		context.orderingManager().onNewIndexOrderedSet(this, result);

		return result;
	}

	private IndexOrderedSet toIos(QueryPlannerContext context) {
		IndexOrderedSet result = IndexOrderedSet.T.create();

		result.setTypeSignature(indexInfo.getEntitySignature());
		result.setPropertyName(indexInfo.getPropertyName());
		result.setIndex(context.sourceManager().indexForSource(from));
		result.setMetricIndex(context.getIndex(from, indexInfo));
		result.setDescending(descending);

		return result;
	}

}
