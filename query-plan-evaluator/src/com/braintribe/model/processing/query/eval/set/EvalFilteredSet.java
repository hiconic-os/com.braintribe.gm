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
import com.braintribe.model.processing.query.eval.set.base.AbstractEvalTupleSet;
import com.braintribe.model.queryplan.filter.Condition;
import com.braintribe.model.queryplan.set.FilteredSet;

/**
 * 
 */
public class EvalFilteredSet extends AbstractEvalTupleSet {

	protected final Condition filterCondition;
	protected final EvalTupleSet evalOperand;

	public EvalFilteredSet(FilteredSet filteredSet, QueryEvaluationContext context) {
		super(context);

		this.filterCondition = filteredSet.getFilter();
		this.evalOperand = context.resolveTupleSet(filteredSet.getOperand());
	}

	@Override
	public Iterator<Tuple> iterator() {
		return new FilteredSetIterator();
	}

	protected class FilteredSetIterator extends AbstractTupleIterator {

		protected Iterator<Tuple> delegateIterator;

		public FilteredSetIterator() {
			delegateIterator = evalOperand.iterator();
			prepareNextValue();
		}

		@Override
		protected void prepareNextValue() {
			while (delegateIterator.hasNext()) {
				Tuple tuple = delegateIterator.next();
				if (context.fulfillsCondition(tuple, filterCondition)) {
					next = tuple;
					return;
				}
			}
			next = null;
		}

	}
}
