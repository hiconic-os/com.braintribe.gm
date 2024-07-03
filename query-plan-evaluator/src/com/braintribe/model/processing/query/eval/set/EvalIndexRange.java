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
package com.braintribe.model.processing.query.eval.set;

import java.util.Iterator;

import com.braintribe.model.processing.query.eval.api.EvalTupleSet;
import com.braintribe.model.processing.query.eval.api.QueryEvaluationContext;
import com.braintribe.model.processing.query.eval.api.Tuple;
import com.braintribe.model.queryplan.set.IndexRange;
import com.braintribe.model.queryplan.value.Value;
import com.braintribe.model.queryplan.value.range.SimpleRange;

/**
 * 
 */
public class EvalIndexRange implements EvalTupleSet {

	protected final Iterable<Tuple> tuples;

	public EvalIndexRange(IndexRange indexRange, QueryEvaluationContext context) {
		SimpleRange range = indexRange.getRange();

		Object lowerBound = resolveValue(range.getLowerBound(), context);
		Object upperBound = resolveValue(range.getUpperBound(), context);

		Boolean lowerInclusive = resolveInclusive(range.getLowerBound(), range.getLowerInclusive());
		Boolean upperInclusive = resolveInclusive(range.getUpperBound(), range.getUpperInclusive());

		tuples = context.getIndexRange(indexRange.getMetricIndex(), lowerBound, lowerInclusive, upperBound, upperInclusive);
	}

	private Object resolveValue(Value v, QueryEvaluationContext context) {
		// the value better be a StaticValue
		return v != null ? context.resolveValue(null, v) : null;
	}

	private Boolean resolveInclusive(Value v, boolean inclusive) {
		return v != null ? inclusive : null;
	}

	@Override
	public Iterator<Tuple> iterator() {
		return tuples.iterator();
	}

}
