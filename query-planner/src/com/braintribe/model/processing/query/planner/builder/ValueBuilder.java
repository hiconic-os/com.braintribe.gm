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

import java.util.Collections;
import java.util.Map;

import com.braintribe.model.processing.query.planner.RuntimeQueryPlannerException;
import com.braintribe.model.processing.query.planner.core.index.IndexKeys;
import com.braintribe.model.processing.query.planner.core.index.ResolvedLookupIndexKeys;
import com.braintribe.model.processing.query.planner.core.index.StaticIndexKeys;
import com.braintribe.model.query.functions.QueryFunction;
import com.braintribe.model.queryplan.value.AggregateFunction;
import com.braintribe.model.queryplan.value.AggregationFunctionType;
import com.braintribe.model.queryplan.value.ConstantValue;
import com.braintribe.model.queryplan.value.IndexValue;
import com.braintribe.model.queryplan.value.QueryFunctionValue;
import com.braintribe.model.queryplan.value.StaticValue;
import com.braintribe.model.queryplan.value.TupleComponent;
import com.braintribe.model.queryplan.value.Value;
import com.braintribe.model.queryplan.value.ValueProperty;

/**
 * 
 */
public class ValueBuilder {

	public static AggregateFunction aggregateFunction(Value operand, AggregationFunctionType type) {
		AggregateFunction result = AggregateFunction.T.create();
		result.setOperand(operand);
		result.setAggregationFunctionType(type);

		return result;
	}

	public static QueryFunctionValue queryFunctionNoMappings(QueryFunction queryFunction) {
		return queryFunction(queryFunction, Collections.<Object, Value> emptyMap());
	}

	public static QueryFunctionValue queryFunction(QueryFunction queryFunction, Map<Object, Value> operandMappings) {
		QueryFunctionValue result = QueryFunctionValue.T.create();
		result.setQueryFunction(queryFunction);
		result.setOperandMappings(operandMappings);

		return result;
	}

	public static StaticValue staticValue(Object actualValue) {
		StaticValue result = StaticValue.T.create();
		result.setValue(actualValue);

		return result;
	}

	public static ValueProperty valueProperty(Value value, String propertyPath) {
		ValueProperty result = ValueProperty.T.create();

		result.setValue(value);
		result.setPropertyPath(propertyPath);

		return result;
	}

	public static TupleComponent tupleComponent(int index) {
		TupleComponent result = TupleComponent.T.create();
		result.setTupleComponentIndex(index);

		return result;
	}

	public static ConstantValue indexKeyValue(IndexKeys keys) {
		if (keys instanceof StaticIndexKeys) {
			return staticValue(((StaticIndexKeys) keys).keys);
		}

		if (keys instanceof ResolvedLookupIndexKeys) {
			ResolvedLookupIndexKeys rlik = (ResolvedLookupIndexKeys) keys;
			return indexValue(rlik.index.getIndexId(), indexKeyValue(rlik.keys));
		}

		// TODO CHAIN+RANGE add support for ResolvedMetricIndexKeys

		throw new RuntimeQueryPlannerException("Unsupported IndexKey " + keys + " of type: " + keys.getClass().getName());
	}

	private static IndexValue indexValue(String indexId, ConstantValue keys) {
		IndexValue result = IndexValue.T.create();
		result.setIndexId(indexId);
		result.setKeys(keys);

		return result;
	}

}
