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

import static com.braintribe.model.processing.query.eval.tools.QueryEvaluationTools.emptyTupleIterator;
import static com.braintribe.utils.lcd.CollectionTools2.newList;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.braintribe.model.processing.query.eval.api.EvalTupleSet;
import com.braintribe.model.processing.query.eval.api.QueryEvaluationContext;
import com.braintribe.model.processing.query.eval.api.Tuple;
import com.braintribe.model.processing.query.eval.set.base.AbstractEvalTupleSet;
import com.braintribe.model.processing.query.eval.tools.TupleComparator;
import com.braintribe.model.queryplan.set.OrderedSetRefinement;
import com.braintribe.model.queryplan.value.Value;

/**
 * 
 */
public class EvalOrderedSetRefinement extends AbstractEvalTupleSet {


	protected final EvalTupleSet operandTupleSet;
	protected final OrderedSetRefinement orderedSet;
	protected final List<Value> groupValues;

	public EvalOrderedSetRefinement(OrderedSetRefinement orderedSet, QueryEvaluationContext context) {
		super(context);

		this.operandTupleSet = context.resolveTupleSet(orderedSet.getOperand());
		this.orderedSet = orderedSet;
		this.groupValues = orderedSet.getGroupValues();
	}

	@Override
	public Iterator<Tuple> iterator() {
		return new ExtendedOrderedSetIterator();
	}

	private class ExtendedOrderedSetIterator extends AbstractTupleIterator {

		private final Iterator<Tuple> operandIterator;

		private Tuple nextBulkFirstTuple;
		private Iterator<Tuple> currentBulkIterator = emptyTupleIterator();

		public ExtendedOrderedSetIterator() {
			operandIterator = operandTupleSet.iterator();
			nextBulkFirstTuple = operandIterator.hasNext() ? operandIterator.next() : null;
			
			prepareNextValue();
		}

		@Override
		protected void prepareNextValue() {
			if (currentBulkIterator.hasNext()) {
				next = currentBulkIterator.next();
				return;
			}

			if (operandIterator.hasNext() || nextBulkFirstTuple != null) {
				prepareNextBulk();
				next = currentBulkIterator.next();
				return;
			}

			next = null;
		}

		private void prepareNextBulk() {
			List<Tuple> tuples = loadEquivalentTuples();
			Collections.sort(tuples, new TupleComparator(orderedSet.getSortCriteria(), context));
			currentBulkIterator = tuples.iterator();
		}

		private List<Tuple> loadEquivalentTuples() {
			List<Object> groupValues = computeGroupValues(nextBulkFirstTuple);

			List<Tuple> result = newList();

			do {
				result.add(nextBulkFirstTuple.detachedCopy());

				if (operandIterator.hasNext()) {
					nextBulkFirstTuple = operandIterator.next();

				} else {
					nextBulkFirstTuple = null;
					return result;
				}

			} while (groupValues.equals(computeGroupValues(nextBulkFirstTuple)));

			return result;
		}

		private List<Object> computeGroupValues(Tuple tuple) {
			List<Object> result = newList(groupValues.size());

			for (Value value: groupValues)
				result.add(context.resolveValue(tuple, value));

			return result;
		}
	}

}
