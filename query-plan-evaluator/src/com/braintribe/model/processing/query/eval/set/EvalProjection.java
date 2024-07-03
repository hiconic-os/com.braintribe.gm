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
import java.util.List;

import com.braintribe.model.processing.query.eval.api.EvalTupleSet;
import com.braintribe.model.processing.query.eval.api.QueryEvaluationContext;
import com.braintribe.model.processing.query.eval.api.Tuple;
import com.braintribe.model.processing.query.eval.set.base.AbstractEvalTupleSet;
import com.braintribe.model.processing.query.eval.tuple.ArrayBasedTuple;
import com.braintribe.model.queryplan.set.Projection;
import com.braintribe.model.queryplan.value.Value;

/**
 * 
 */
public class EvalProjection extends AbstractEvalTupleSet implements HasMoreAwareSet {

	protected final EvalTupleSet evalOperand;
	protected final List<Value> values;
	protected final ArrayBasedTuple singletonTuple;

	public EvalProjection(Projection projection, QueryEvaluationContext context) {
		super(context);

		this.evalOperand = context.resolveTupleSet(projection.getOperand());
		this.values = projection.getValues();
		this.singletonTuple = new ArrayBasedTuple(values.size());
	}

	@Override
	public boolean hasMore() {
		return moreHas(evalOperand);
	}

	@Override
	public Iterator<Tuple> iterator() {
		return new ProjectionIterator();
	}

	protected class ProjectionIterator extends AbstractTupleIterator {

		protected Iterator<Tuple> delegateIterator;

		public ProjectionIterator() {
			delegateIterator = evalOperand.iterator();
			prepareNextValue();
		}

		@Override
		protected int totalComponentsCount() {
			return values.size();
		}

		@Override
		protected void prepareNextValue() {
			if (delegateIterator.hasNext()) {
				Tuple tuple = delegateIterator.next();

				int index = 0;
				for (Value value: values) {
					Object resolvedValue = context.resolveValue(tuple, value);
					singletonTuple.setValueDirectly(index++, resolvedValue);
				}

				next = singletonTuple;

			} else {
				next = null;
			}
		}

	}
}
