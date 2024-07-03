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
import com.braintribe.model.queryplan.index.Index;
import com.braintribe.model.queryplan.set.join.IndexLookupJoin;
import com.braintribe.model.queryplan.value.Value;

/**
 * 
 */
public class EvalIndexLookupJoin extends AbstractEvalIndexJoin {

	private final Index lookupIndex;
	private final Value lookupValue;

	public EvalIndexLookupJoin(IndexLookupJoin join, QueryEvaluationContext context) {
		super(join, context);

		this.lookupIndex = join.getLookupIndex();
		this.lookupValue = join.getLookupValue();
	}

	@Override
	protected Iterator<Tuple> joinTuplesFor(Tuple tuple) {
		Object value = context.resolveValue(tuple, lookupValue);

		return context.getAllValuesForIndex(lookupIndex, value).iterator();
	}

}
