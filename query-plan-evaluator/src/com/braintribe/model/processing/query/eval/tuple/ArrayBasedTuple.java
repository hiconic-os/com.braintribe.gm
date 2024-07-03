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

import java.util.Arrays;

import com.braintribe.model.processing.query.eval.api.Tuple;

/**
 * 
 */
public class ArrayBasedTuple implements Tuple {

	protected Object[] data;

	public ArrayBasedTuple(int size) {
		this.data = new Object[size];
	}

	public ArrayBasedTuple(Object[] data) {
		this.data = data;
	}

	public void setValueDirectly(int position, Object value) {
		data[position] = value;
	}

	@Override
	public Object getValue(int index) {
		return data[index];
	}

	public void acceptAllValuesFrom(Tuple other) {
		for (int i = 0; i < data.length; i++) {
			this.data[i] = other.getValue(i);
		}
	}

	public void acceptValuesFrom(Tuple other) {
		for (int i = 0; i < data.length; i++) {
			Object value = other.getValue(i);
			if (value != null)
				this.data[i] = value;
		}
	}

	public void clear() {
		for (int i = 0; i < data.length; i++)
			this.data[i] = null;
	}

	@Override
	public String toString() {
		return "TUPLE" + Arrays.toString(data);
	}

	@Override
	public Tuple detachedCopy() {
		DetachedTuple result = new DetachedTuple(this.data);
		this.data = new Object[this.data.length];

		return result;
	}

	protected static class DetachedTuple extends ArrayBasedTuple {
		DetachedTuple(Object[] data) {
			super(data);
		}

		@Override
		public Tuple detachedCopy() {
			return this;
		}
	}

}
