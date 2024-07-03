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
package com.braintribe.model.processing.query.eval.tuple;

import com.braintribe.model.processing.query.eval.api.RuntimeQueryEvaluationException;
import com.braintribe.model.processing.query.eval.api.Tuple;

/**
 * Just a memory optimization for the most simple types of tuples. Not sure whether this is even needed.
 */
public class OneDimensionalTuple implements Tuple {

	protected Object data;
	protected final int position;

	public OneDimensionalTuple(int position) {
		this.position = position;
	}

	public void setValueDirectly(int index, Object value) {
		if (position != index)
			throw new RuntimeQueryEvaluationException(
					"Cannot set value on position '" + index + "'. This tuple represents only the position: " + position);

		data = value;
	}

	@Override
	public Object getValue(int index) {
		return position == index ? data : null;
	}

	@Override
	public Tuple detachedCopy() {
		DetachedTuple result = new DetachedTuple(position);
		result.data = this.data;

		return result;
	}

	protected static class DetachedTuple extends OneDimensionalTuple {
		DetachedTuple(int position) {
			super(position);
		}

		@Override
		public Tuple detachedCopy() {
			return this;
		}
	}

}
