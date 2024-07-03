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

import static com.braintribe.utils.lcd.CollectionTools2.newList;
import static com.braintribe.utils.lcd.CollectionTools2.newMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.braintribe.model.processing.query.tools.QueryFunctionAnalyzer;
import com.braintribe.model.query.Operand;
import com.braintribe.model.query.functions.QueryFunction;
import com.braintribe.model.queryplan.value.StaticValue;
import com.braintribe.model.queryplan.value.Value;

/**
 * @author peter.gazdik
 */
public class QueryFunctionManager {

	private final QueryPlannerContext context;

	private final Map<QueryFunction, List<Operand>> functionOperands = newMap();
	private final Map<QueryFunction, Map<Object, Value>> queryModelOperandValues = newMap();

	public QueryFunctionManager(QueryPlannerContext context) {
		this.context = context;
	}

	public Map<Object, Value> noticeQueryFunction(QueryFunction function) {
		Map<Object, Value> operandMappings = queryModelOperandValues.get(function);

		if (operandMappings == null) {
			operandMappings = resolveOperandMappings(function);
			queryModelOperandValues.put(function, operandMappings);
		}

		return operandMappings;
	}

	private Map<Object, Value> resolveOperandMappings(QueryFunction function) {
		Map<Object, Value> result = newMap();

		for (Operand operand: listOperands(function)) {
			if (operand instanceof QueryFunction)
				noticeQueryFunction((QueryFunction) operand);

			Value operandValue = context.convertOperand(operand);

			if (!(operandValue instanceof StaticValue))
				result.put(operand, operandValue);
		}

		return result;
	}

	public List<Operand> listOperands(QueryFunction function) {
		List<Operand> result = functionOperands.get(function);

		if (result == null) {
			result = newList();
			functionOperands.put(function, result);

			for (Object operand: listAllOperands(function))
				if (operand instanceof Operand)
					result.add((Operand) operand);
		}

		return result;
	}

	private Collection<?> listAllOperands(QueryFunction function) {
		return QueryFunctionAnalyzer.findOperands(function);
	}

}
