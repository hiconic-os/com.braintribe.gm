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
import java.util.Set;

import com.braintribe.model.processing.query.eval.api.EvalTupleSet;
import com.braintribe.model.processing.query.eval.api.QueryEvaluationContext;
import com.braintribe.model.processing.query.eval.api.Tuple;
import com.braintribe.model.processing.query.eval.set.base.AbstractEvalTupleSet;
import com.braintribe.model.processing.query.eval.tools.QueryEvaluationTools;
import com.braintribe.model.queryplan.set.Intersection;
import com.braintribe.model.queryplan.set.TupleSet;

/**
 * 
 */
public class EvalIntersection extends AbstractEvalTupleSet {

	protected final Set<Tuple> tuples;

	public EvalIntersection(Intersection intersection, QueryEvaluationContext context) {
		super(context);

		Set<Tuple> firstTuples = QueryEvaluationTools.tupleHashSet(context.totalComponentsCount());
		Set<Tuple> intersectionTuples = QueryEvaluationTools.tupleHashSet(context.totalComponentsCount());

		addTuples(intersection.getFirstOperand(), firstTuples);
		retainAll(intersection.getSecondOperand(), firstTuples, intersectionTuples);

		tuples = intersectionTuples;
	}

	private void addTuples(TupleSet tupleSet, Set<Tuple> set) {
		QueryEvaluationTools.addAllTuples(tupleSet, set, context);
	}

	private void retainAll(TupleSet tupleSet, Set<Tuple> firstTuples, Set<Tuple> intersectionTuples) {
		EvalTupleSet evalTupleSet = context.resolveTupleSet(tupleSet);

		for (Tuple tuple: evalTupleSet) {
			if (firstTuples.contains(tuple)) {
				intersectionTuples.add(tuple.detachedCopy());
			}
		}
	}

	@Override
	public Iterator<Tuple> iterator() {
		return tuples.iterator();
	}

}
