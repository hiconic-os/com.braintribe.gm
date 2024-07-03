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
import com.braintribe.model.queryplan.set.DistinctSet;

/**
 * 
 */
public class EvalDistinctSet extends AbstractEvalTupleSet {

	protected final EvalTupleSet evalOperand;
	protected final int tupleSize;

	public EvalDistinctSet(DistinctSet distinctSet, QueryEvaluationContext context) {
		super(context);

		int _tupleSize = distinctSet.getTupleSize();

		this.evalOperand = context.resolveTupleSet(distinctSet.getOperand());
		this.tupleSize = _tupleSize == 0 ? context.resultComponentsCount() : _tupleSize;
	}

	@Override
	public Iterator<Tuple> iterator() {
		return new DistinctSetIterator();
	}

	protected class DistinctSetIterator extends AbstractTupleIterator {
		protected final Set<Tuple> alreadyReturnedTuples;
		protected final Iterator<Tuple> delegateIterator;

		public DistinctSetIterator() {
			alreadyReturnedTuples = QueryEvaluationTools.tupleHashSet(tupleSize);
			delegateIterator = evalOperand.iterator();

			next = delegateIterator.hasNext() ? delegateIterator.next() : null;
		}

		@Override
		protected int totalComponentsCount() {
			return tupleSize;
		}

		@Override
		protected void prepareNextValue() {
			alreadyReturnedTuples.add(next.detachedCopy());

			while (delegateIterator.hasNext()) {
				Tuple tuple = delegateIterator.next();
				if (!alreadyReturnedTuples.contains(tuple)) {
					next = tuple;
					return;
				}
			}
			next = null;
		}
	}

}
