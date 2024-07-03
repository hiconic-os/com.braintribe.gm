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

import com.braintribe.model.processing.query.eval.api.QueryEvaluationContext;
import com.braintribe.model.processing.query.eval.api.Tuple;
import com.braintribe.model.queryplan.index.GeneratedIndex;
import com.braintribe.model.queryplan.set.MergeLookupJoin;
import com.braintribe.model.queryplan.value.Value;

/**
 * 
 */
public class EvalMergeLookupJoin extends AbstractEvalMergeJoin {

	protected final GeneratedIndex lookupIndex;
	protected final Value lookupValue;

	public EvalMergeLookupJoin(MergeLookupJoin tupleSet, QueryEvaluationContext context) {
		super(tupleSet, context);

		this.lookupIndex = buildIndex(tupleSet);
		this.lookupValue = tupleSet.getValue();
	}

	private GeneratedIndex buildIndex(MergeLookupJoin tupleSet) {
		GeneratedIndex generatedIndex = GeneratedIndex.T.create();
		generatedIndex.setIndexKey(tupleSet.getOtherValue());
		generatedIndex.setOperand(tupleSet.getOtherOperand());
		
		return generatedIndex;
	}

	@Override
	protected Iterator<Tuple> joinTuplesFor(Tuple tuple) {
		Object value = context.resolveValue(tuple, lookupValue);

		return context.getAllValuesForIndex(lookupIndex, value).iterator();
	}

}
