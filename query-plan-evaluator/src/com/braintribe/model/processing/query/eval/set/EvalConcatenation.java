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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.braintribe.model.processing.query.eval.api.EvalTupleSet;
import com.braintribe.model.processing.query.eval.api.QueryEvaluationContext;
import com.braintribe.model.processing.query.eval.api.Tuple;
import com.braintribe.model.processing.query.eval.set.base.AbstractEvalTupleSet;
import com.braintribe.model.processing.query.eval.tools.QueryEvaluationTools;
import com.braintribe.model.queryplan.set.Concatenation;
import com.braintribe.model.queryplan.set.TupleSet;

/**
 * 
 */
public class EvalConcatenation extends AbstractEvalTupleSet {

	protected final List<EvalTupleSet> operands;
	protected final int tupleSize;

	public EvalConcatenation(Concatenation concatenation, QueryEvaluationContext context) {
		super(context);
		this.operands = QueryEvaluationTools.resolveTupleSets(listOfTupleSets(concatenation), context);
		this.tupleSize = concatenation.getTupleSize();
	}

	private List<TupleSet> listOfTupleSets(Concatenation c) {
		return Arrays.asList(c.getFirstOperand(), c.getSecondOperand());
	}

	@Override
	public Iterator<Tuple> iterator() {
		return new ConcatenationIterator();
	}

	protected class ConcatenationIterator extends AbstractTupleIterator {

		protected Iterator<? extends Iterable<Tuple>> operandsIterator;
		protected Iterator<Tuple> currentTupleIterator;

		public ConcatenationIterator() {
			operandsIterator = operands.iterator();
			currentTupleIterator = operandsIterator.next().iterator();

			prepareNextValue();
		}

		@Override
		protected int totalComponentsCount() {
			return tupleSize == 0 ? super.totalComponentsCount() : tupleSize;
		}

		@Override
		protected void prepareNextValue() {
			while (true) {
				if (currentTupleIterator.hasNext()) {
					next = currentTupleIterator.next();
					return;
				}

				if (operandsIterator.hasNext()) {
					currentTupleIterator = operandsIterator.next().iterator();

				} else {
					next = null;
					return;
				}
			}
		}

	}
}
