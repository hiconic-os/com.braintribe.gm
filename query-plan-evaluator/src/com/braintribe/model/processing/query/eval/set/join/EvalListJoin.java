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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import com.braintribe.model.processing.query.eval.api.QueryEvaluationContext;
import com.braintribe.model.processing.query.eval.api.Tuple;
import com.braintribe.model.processing.query.eval.tuple.ArrayBasedTuple;
import com.braintribe.model.queryplan.set.join.JoinedListIndex;
import com.braintribe.model.queryplan.set.join.ListJoin;

/**
 * 
 */
public class EvalListJoin extends AbstractEvalPropertyJoin {

	protected final JoinedListIndex listIndex;

	public EvalListJoin(ListJoin join, QueryEvaluationContext context) {
		super(join, context);

		this.listIndex = join.getListIndex();
	}

	@Override
	public Iterator<Tuple> iterator() {
		return new ListIterator();
	}

	protected class ListIterator extends AbstractPropertyJoinIterator {
		private Iterator<?> indexedValuesIterator;
		private int counter;

		@Override
		protected void onNewOperandTuple$LeftInner(ArrayBasedTuple tuple) {
			Collection<?> c = context.resolveValue(tuple, valueProperty);
			indexedValuesIterator = c != null ? c.iterator() : Collections.emptySet().iterator();
			counter = 0;
		}

		@Override
		protected boolean hasNextJoinedValue$LeftInner() {
			return indexedValuesIterator.hasNext();
		}

		@Override
		protected void setNextJoinedValue$LeftInner(ArrayBasedTuple tuple) {
			tuple.setValueDirectly(listIndex.getIndex(), counter++);
			setRightValue(tuple, indexedValuesIterator.next());
		}

		@Override
		protected void setNextJoinedValueAsVoid(ArrayBasedTuple tuple) {
			tuple.setValueDirectly(listIndex.getIndex(), null);
			tuple.setValueDirectly(componentPosition, null);
		}

	}

}
