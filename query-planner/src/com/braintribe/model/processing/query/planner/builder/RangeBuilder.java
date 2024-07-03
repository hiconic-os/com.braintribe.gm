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
package com.braintribe.model.processing.query.planner.builder;


import static com.braintribe.utils.lcd.CollectionTools2.newSet;

import java.util.List;
import java.util.Set;

import com.braintribe.model.processing.query.planner.context.QueryPlannerContext;
import com.braintribe.model.processing.query.planner.tools.Bound;
import com.braintribe.model.queryplan.value.range.RangeIntersection;
import com.braintribe.model.queryplan.value.range.SimpleRange;

/**
 * 
 */
public class RangeBuilder {

	public static RangeIntersection rangeForBounds(List<Bound> lowerBounds, List<Bound> upperBounds, QueryPlannerContext context) {
		RangeIntersection result = RangeIntersection.T.create();
		Set<SimpleRange> ranges = newSet();
		result.setRanges(ranges);

		for (Bound lowerBound: lowerBounds)
			ranges.add(RangeBuilder.rangeForBound(lowerBound, true, context));

		for (Bound upperBound: upperBounds)
			ranges.add(RangeBuilder.rangeForBound(upperBound, false, context));

		return result;
	}

	private static SimpleRange rangeForBound(Bound bound, boolean lower, QueryPlannerContext context) {
		if (lower)
			return rangeForBounds(bound, null, context);
		else
			return rangeForBounds(null, bound, context);
	}

	public static SimpleRange rangeForBounds(Bound lowerBound, Bound upperBound, QueryPlannerContext context) {
		SimpleRange result = SimpleRange.T.create();

		if (lowerBound != null) {
			result.setLowerBound(context.convertOperand(lowerBound.value));
			result.setLowerInclusive(lowerBound.inclusive);
		}

		if (upperBound != null) {
			result.setUpperBound(context.convertOperand(upperBound.value));
			result.setUpperInclusive(upperBound.inclusive);
		}

		return result;
	}

}
