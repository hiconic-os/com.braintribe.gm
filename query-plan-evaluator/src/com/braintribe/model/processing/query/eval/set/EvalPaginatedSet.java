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

import static com.braintribe.model.processing.query.eval.tools.QueryEvaluationTools.moreHas;

import java.util.Iterator;

import com.braintribe.model.processing.query.eval.api.EvalTupleSet;
import com.braintribe.model.processing.query.eval.api.QueryEvaluationContext;
import com.braintribe.model.processing.query.eval.api.RuntimeQueryEvaluationException;
import com.braintribe.model.processing.query.eval.api.Tuple;
import com.braintribe.model.processing.query.eval.set.base.AbstractEvalTupleSet;
import com.braintribe.model.queryplan.set.PaginatedSet;

/**
 * 
 */
public class EvalPaginatedSet extends AbstractEvalTupleSet implements HasMoreAwareSet {

	protected final int limit;
	protected final int offset;
	protected final int tupleSize;
	protected final boolean operandMayApplyPagination;
	protected final EvalTupleSet evalOperand;

	protected Boolean hasMore;

	public EvalPaginatedSet(PaginatedSet paginatedSet, QueryEvaluationContext context) {
		super(context);

		this.limit = nonPositiveToInf(paginatedSet.getLimit());
		this.offset = paginatedSet.getOffset();
		this.tupleSize = paginatedSet.getTupleSize();
		this.operandMayApplyPagination = paginatedSet.getOperandMayApplyPagination();
		this.evalOperand = context.resolveTupleSet(paginatedSet.getOperand());
	}

	private static int nonPositiveToInf(int i) {
		return i > 0 ? i : Integer.MAX_VALUE;
	}

	@Override
	public boolean hasMore() {
		if (hasMore == null) {
			throw new RuntimeQueryEvaluationException("Cannot resolve hasMore value until the iterator is run through at least once.");
		}

		return hasMore.booleanValue();
	}

	@Override
	public Iterator<Tuple> iterator() {
		return new PaginatedSetIterator();
	}

	protected class PaginatedSetIterator extends AbstractTupleIterator {

		protected Iterator<Tuple> delegateIterator;
		protected int remaining;

		@Override
		protected int totalComponentsCount() {
			return tupleSize == 0 ? context.resultComponentsCount() : tupleSize;
		}

		public PaginatedSetIterator() {
			delegateIterator = evalOperand.iterator();
			remaining = limit;
			prepareFirstValue();
		}

		protected void prepareFirstValue() {
			int counter = offset;

			while (delegateIterator.hasNext() && counter-- > 0) {
				delegateIterator.next();
			}

			if (!delegateIterator.hasNext()) {
				hasMore = false;
			}

			prepareNextValue();
		}

		@Override
		protected void prepareNextValue() {
			if (delegateIterator.hasNext() && remaining-- > 0) {
				next = delegateIterator.next();

			} else {
				hasMore = delegateIterator.hasNext() || (operandMayApplyPagination && moreHas(evalOperand));
				next = null;
			}
		}

	}
}
