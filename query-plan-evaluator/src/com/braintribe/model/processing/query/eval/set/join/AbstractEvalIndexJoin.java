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

import com.braintribe.model.processing.query.eval.api.QueryEvaluationContext;
import com.braintribe.model.processing.query.eval.api.Tuple;
import com.braintribe.model.processing.query.eval.tuple.ArrayBasedTuple;
import com.braintribe.model.queryplan.set.join.Join;

/**
 * 
 */
public abstract class AbstractEvalIndexJoin extends AbstractEvalJoin {

	protected final int joinIndex;

	public AbstractEvalIndexJoin(Join join, QueryEvaluationContext context) {
		super(join, context);

		this.joinIndex = join.getIndex();
	}

	@Override
	public Iterator<Tuple> iterator() {
		return new IndexJoinIterator();
	}

	protected class IndexJoinIterator extends AbstractJoinIterator {
		private Iterator<Tuple> joinTuplesIterator;

		protected IndexJoinIterator() {
			super.initialize();
		}

		@Override
		protected void onNewOperandTuple(ArrayBasedTuple tuple) {
			joinTuplesIterator = joinTuplesFor(tuple);
		}

		@Override
		protected boolean hasNextJoinedValue() {
			return joinTuplesIterator.hasNext();
		}

		@Override
		protected void setNextJoinedValue(ArrayBasedTuple tuple) {
			tuple.setValueDirectly(componentPosition, joinTuplesIterator.next().getValue(joinIndex));
		}

	}

	abstract protected Iterator<Tuple> joinTuplesFor(Tuple tuple);

}
