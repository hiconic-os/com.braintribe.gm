// ============================================================================
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
// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
// ============================================================================
package com.braintribe.model.processing.query.eval.index;

import static com.braintribe.utils.lcd.CollectionTools2.newMap;
import static com.braintribe.utils.lcd.CollectionTools2.newSet;
import static com.braintribe.utils.lcd.CollectionTools2.nullSafe;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.braintribe.model.processing.query.eval.api.QueryEvaluationContext;
import com.braintribe.model.processing.query.eval.api.Tuple;
import com.braintribe.model.queryplan.index.GeneratedIndex;
import com.braintribe.model.queryplan.set.TupleSet;
import com.braintribe.model.queryplan.value.Value;

/**
 * 
 */
public class EvalGeneratedIndex implements EvalIndex {

	protected QueryEvaluationContext context;
	protected TupleSet operand;
	protected Value indexKey;
	protected Map<Object, Set<Tuple>> indexMap;

	public EvalGeneratedIndex(QueryEvaluationContext context, GeneratedIndex index) {
		this(context, index, newMap());
	}

	protected EvalGeneratedIndex(QueryEvaluationContext context, GeneratedIndex index, Map<Object, Set<Tuple>> emptyIndexMap) {
		this.context = context;
		this.operand = index.getOperand();
		this.indexKey = index.getIndexKey();
		this.indexMap = emptyIndexMap;

		buildIndexMap();
	}

	protected void buildIndexMap() {
		for (Tuple tuple: context.resolveTupleSet(operand)) {
			Object key = context.resolveValue(tuple, indexKey);
			Set<Tuple> set = indexMap.get(key);

			if (set == null)
				indexMap.put(key, set = newSet());

			set.add(tuple.detachedCopy());
		}
	}

	@Override
	public Iterable<Tuple> getAllValuesForIndex(Object indexValue) {
		return nullSafe(indexMap.get(indexValue));
	}

	@Override
	public Iterable<Tuple> getAllValuesForIndices(Collection<?> indexValues) {
		Set<Tuple> result = newSet();

		for (Object indexValue: indexValues)
			result.addAll(nullSafe(indexMap.get(indexValue)));

		return result;
	}

}
