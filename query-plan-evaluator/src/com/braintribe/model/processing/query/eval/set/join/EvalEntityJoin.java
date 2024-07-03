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
import com.braintribe.model.queryplan.set.join.EntityJoin;

/**
 * 
 */
public class EvalEntityJoin extends AbstractEvalPropertyJoin {

	public EvalEntityJoin(EntityJoin join, QueryEvaluationContext context) {
		super(join, context);
	}

	@Override
	public Iterator<Tuple> iterator() {
		return new EntityJoinIterator();
	}

	protected class EntityJoinIterator extends AbstractPropertyJoinIterator {
		private boolean hasNextJoinedValue = false;

		@Override
		protected void onNewOperandTuple$LeftInner(ArrayBasedTuple tuple) {
			hasNextJoinedValue = true;
		}

		@Override
		protected boolean hasNextJoinedValue$LeftInner() {
			return hasNextJoinedValue;
		}

		@Override
		protected void setNextJoinedValue$LeftInner(ArrayBasedTuple tuple) {
			Object joinValue = context.resolveValue(tuple, valueProperty);
			setRightValue(tuple, joinValue);
			hasNextJoinedValue = false;
		}

		@Override
		protected void setNextJoinedValueAsVoid(ArrayBasedTuple tuple) {
			tuple.setValueDirectly(componentPosition, null);
		}

	}

}
