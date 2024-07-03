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

import com.braintribe.model.processing.query.eval.api.EvalTupleSet;
import com.braintribe.model.processing.query.eval.api.QueryEvaluationContext;
import com.braintribe.model.processing.query.eval.api.Tuple;
import com.braintribe.model.processing.query.eval.set.base.TransientGeneratorEvalTupleSet;
import com.braintribe.model.processing.query.eval.tuple.ArrayBasedTuple;
import com.braintribe.model.queryplan.set.MergeJoin;

/**
 * 
 */
public abstract class AbstractEvalMergeJoin extends TransientGeneratorEvalTupleSet {

	protected final EvalTupleSet operand;
	protected ArrayBasedTuple nextTuple;

	public AbstractEvalMergeJoin(MergeJoin tupleSet, QueryEvaluationContext context) {
		super(context);

		this.operand = context.resolveTupleSet(tupleSet.getOperand());
	}

	@Override
	public Iterator<Tuple> iterator() {
		return new IndexJoinIterator();
	}

	protected class IndexJoinIterator extends AbstractTupleIterator {
		private Iterator<Tuple> operandIterator;
		private Iterator<Tuple> joinTuplesIterator;

		protected IndexJoinIterator() {
			this.operandIterator = operand.iterator();

			this.initialize();
		}

		protected void initialize() {
			if (prepareNextOperandTuple()) {
				prepareNextValue();
			}
		}

		@Override
		protected void prepareNextValue() {
			while (!joinTuplesIterator.hasNext()) {
				if (!prepareNextOperandTuple()) {
					next = null;
					return;
				}
			}

			nextTuple.acceptValuesFrom(joinTuplesIterator.next());
			next = nextTuple;
		}

		protected boolean prepareNextOperandTuple() {
			if (operandIterator.hasNext()) {
				Tuple operandNext = operandIterator.next();

				if (operandNext instanceof ArrayBasedTuple) {
					nextTuple = (ArrayBasedTuple) operandNext;

				} else {
					singletonTuple.acceptAllValuesFrom(operandNext);
					nextTuple = singletonTuple;
				}

				joinTuplesIterator = joinTuplesFor(nextTuple);
				return true;

			} else {
				return false;
			}
		}

	}

	protected abstract Iterator<Tuple> joinTuplesFor(Tuple tuple);

}
