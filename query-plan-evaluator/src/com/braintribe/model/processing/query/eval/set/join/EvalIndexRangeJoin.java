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
package com.braintribe.model.processing.query.eval.set.join;

import java.util.Iterator;

import com.braintribe.model.processing.query.eval.api.QueryEvaluationContext;
import com.braintribe.model.processing.query.eval.api.Tuple;
import com.braintribe.model.processing.query.eval.tools.EvalRange;
import com.braintribe.model.processing.query.eval.tools.RangeEvaluator;
import com.braintribe.model.queryplan.index.MetricIndex;
import com.braintribe.model.queryplan.set.join.IndexRangeJoin;
import com.braintribe.model.queryplan.value.range.Range;

/**
 * 
 */
public class EvalIndexRangeJoin extends AbstractEvalIndexJoin {

	private final MetricIndex metricIndex;
	private final Range range;

	public EvalIndexRangeJoin(IndexRangeJoin join, QueryEvaluationContext context) {
		super(join, context);

		this.metricIndex = join.getMetricIndex();
		this.range = join.getRange();
	}

	@Override
	protected Iterator<Tuple> joinTuplesFor(Tuple tuple) {
		EvalRange er = RangeEvaluator.evaluate(range, tuple, context);

		return context.getIndexRange(metricIndex, er.lowerBound, er.lowerInclusive, er.upperBound, er.upperInclusive).iterator();
	}

}
