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

import java.util.List;
import java.util.Map;

import com.braintribe.cc.lcd.CodingMap;
import com.braintribe.model.processing.query.tools.AggregationAnalyzer;
import com.braintribe.model.processing.query.tools.AggregationAnalyzer.AggregationAnalysis;
import com.braintribe.model.processing.query.tools.OperandHashingComparator;
import com.braintribe.model.query.Operand;
import com.braintribe.model.query.SelectQuery;

/**
 * @author peter.gazdik
 */
public class QueryAggregationManager {

	private final Map<Operand, Integer> operandToPosition = CodingMap.create(OperandHashingComparator.INSTANCE);
	private final AggregationAnalysis aggAnalysis;

	public QueryAggregationManager(QueryPlannerContext context, SelectQuery query) {
		this.aggAnalysis = AggregationAnalyzer.analyze(context.evalExclusionCheck(), query);
	}

	public boolean hasAggregation() {
		return aggAnalysis.hasAggregation;
	}

	public List<Operand> getExtraOperands() {
		return aggAnalysis.extraOperands;
	}

	public void noticeTupleComponentIndex(Object o, int index) {
		if (o instanceof Operand)
			operandToPosition.putIfAbsent((Operand) o, index);
	}

	public Integer findTupleComponentIndexOf(Operand o) {
		return operandToPosition.get(o);
	}

	public int getTupleComponentIndexOf(Operand o) {
		return operandToPosition.computeIfAbsent(o, this::throwCannotFindAfIndexException);
	}

	private Integer throwCannotFindAfIndexException(Operand o) {
		throw new IllegalStateException("Cannot retrieve index of operand '" + o + "' as this doesn't seem to have been selected.");
	}

}
