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
package com.braintribe.model.processing.query.eval.tools;

import com.braintribe.model.processing.query.eval.api.QueryEvaluationContext;
import com.braintribe.model.processing.query.eval.api.RuntimeQueryEvaluationException;
import com.braintribe.model.processing.query.eval.api.Tuple;
import com.braintribe.model.queryplan.value.range.Range;
import com.braintribe.model.queryplan.value.range.RangeIntersection;
import com.braintribe.model.queryplan.value.range.SimpleRange;

/**
 * 
 */
public class RangeEvaluator {

	public static EvalRange evaluate(Range range, Tuple tuple, QueryEvaluationContext context) {
		switch (range.rangeType()) {
			case intersection:
				return evaluate((RangeIntersection) range, tuple, context);
			case simple:
				return evaluate((SimpleRange) range, tuple, context);
		}

		throw new RuntimeQueryEvaluationException("Unsupported Range: " + range + " of type: " + range.rangeType());
	}

	private static EvalRange evaluate(RangeIntersection range, Tuple tuple, QueryEvaluationContext context) {
		EvalRange result = new EvalRange();

		for (SimpleRange sr: range.getRanges())
			adjustBounds(result, sr, tuple, context);

		return result;

	}

	private static EvalRange evaluate(SimpleRange range, Tuple tuple, QueryEvaluationContext context) {
		EvalRange result = new EvalRange();
		adjustBounds(result, range, tuple, context);

		return result;
	}

	private static void adjustBounds(EvalRange result, SimpleRange sr, Tuple tuple, QueryEvaluationContext context) {
		if (sr.getLowerBound() != null) {
			Object value = context.resolveValue(tuple, sr.getLowerBound());

			if (result.lowerInclusive == null) {
				setLowerBound(result, value, sr.getLowerInclusive());

			} else {
				int cmp = ObjectComparator.compare(result.lowerBound, value);
				if (cmp < 0)
					setLowerBound(result, value, sr.getLowerInclusive());

				if (cmp == 0 && !sr.getLowerInclusive()) {
					result.lowerInclusive = false;
				}
			}
		}

		if (sr.getUpperBound() != null) {
			Object value = context.resolveValue(tuple, sr.getUpperBound());

			if (result.upperInclusive == null) {
				setUpperBound(result, value, sr.getUpperInclusive());

			} else {
				int cmp = ObjectComparator.compare(result.upperBound, value);
				if (cmp > 0)
					setUpperBound(result, value, sr.getUpperInclusive());

				if (cmp == 0 && !sr.getUpperInclusive()) {
					result.upperInclusive = false;
				}
			}
		}

	}

	private static void setLowerBound(EvalRange result, Object value, boolean inclusive) {
		result.lowerBound = value;
		result.lowerInclusive = inclusive;
	}

	private static void setUpperBound(EvalRange result, Object value, boolean inclusive) {
		result.upperBound = value;
		result.upperInclusive = inclusive;
	}
}
