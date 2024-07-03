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
package com.braintribe.model.processing.query.eval.set.base;

import java.util.Iterator;

import com.braintribe.model.processing.query.eval.api.EvalTupleSet;
import com.braintribe.model.processing.query.eval.api.QueryEvaluationContext;
import com.braintribe.model.processing.query.eval.api.Tuple;
import com.braintribe.model.processing.query.eval.tuple.ArrayBasedTuple;

/**
 * 
 */
public abstract class AbstractEvalTupleSet implements EvalTupleSet {

	protected QueryEvaluationContext context;

	protected AbstractEvalTupleSet(QueryEvaluationContext context) {
		this.context = context;
	}

	protected <T> T cast(Object o) {
		return (T) o;
	}

	protected abstract class AbstractTupleIterator implements Iterator<Tuple> {

		protected Tuple next;
		private final ArrayBasedTuple result = new ArrayBasedTuple(totalComponentsCount());

		protected int totalComponentsCount() {
			return context.totalComponentsCount();
		}

		@Override
		public boolean hasNext() {
			return next != null;
		}

		@Override
		public Tuple next() {
			if (!hasNext())
				throw new IllegalStateException("No next found for iterator!");

			result.acceptAllValuesFrom(next);
			prepareNextValue();

			return result;
		}

		protected abstract void prepareNextValue();

		@Override
		public final void remove() {
			throw new UnsupportedOperationException("Cannot remove a tuple from a tuple set!");
		}

	}

}
