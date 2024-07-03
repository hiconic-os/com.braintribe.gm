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

import com.braintribe.model.processing.query.eval.api.EvalTupleSet;
import com.braintribe.model.processing.query.eval.api.QueryEvaluationContext;
import com.braintribe.model.processing.query.eval.api.Tuple;
import com.braintribe.model.processing.query.eval.set.base.TransientGeneratorEvalTupleSet;
import com.braintribe.model.processing.query.eval.set.join.AbstractEvalIndexJoin.IndexJoinIterator;
import com.braintribe.model.processing.query.eval.set.join.AbstractEvalPropertyJoin.AbstractPropertyJoinIterator;
import com.braintribe.model.processing.query.eval.set.join.AbstractEvalPropertyJoin.AbstractPropertyJoinLeftOuterSupportingIterator;
import com.braintribe.model.processing.query.eval.tuple.ArrayBasedTuple;
import com.braintribe.model.queryplan.set.join.Join;

/**
 * 
 */
public abstract class AbstractEvalJoin extends TransientGeneratorEvalTupleSet {

	protected final EvalTupleSet operand;
	protected final int componentPosition;
	protected ArrayBasedTuple nextTuple;

	public AbstractEvalJoin(Join join, QueryEvaluationContext context) {
		super(context);

		this.operand = context.resolveTupleSet(join.getOperand());
		this.componentPosition = join.getIndex();
	}

	/**
	 * Base class for join iterators.
	 * 
	 * @see IndexJoinIterator
	 * @see AbstractPropertyJoinIterator
	 * @see AbstractPropertyJoinLeftOuterSupportingIterator
	 */
	protected abstract class AbstractJoinIterator extends AbstractTupleIterator {
		protected Iterator<Tuple> operandIterator;

		protected AbstractJoinIterator() {
			operandIterator = operand.iterator();
		}

		protected void initialize() {
			if (prepareNextOperandTuple())
				prepareNextValue();
		}

		@Override
		protected void prepareNextValue() {
			while (!hasNextJoinedValue()) {
				if (!prepareNextOperandTuple()) {
					next = null;
					return;
				}
			}

			setNextJoinedValue(nextTuple);
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

				onNewOperandTuple(nextTuple);
				return true;

			} else {
				return false;
			}
		}

		protected abstract void onNewOperandTuple(ArrayBasedTuple tuple);

		protected abstract boolean hasNextJoinedValue();

		protected abstract void setNextJoinedValue(ArrayBasedTuple tuple);
	}

}
