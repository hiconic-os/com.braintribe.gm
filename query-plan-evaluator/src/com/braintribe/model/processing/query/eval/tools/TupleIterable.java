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
package com.braintribe.model.processing.query.eval.tools;

import java.util.Iterator;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.processing.query.eval.api.Tuple;
import com.braintribe.model.processing.query.eval.tuple.OneDimensionalTuple;

/**
 * 
 */
public class TupleIterable implements Iterable<Tuple> {

	protected final int componentIndex;
	protected final Iterable<? extends GenericEntity> entities;
	protected final OneDimensionalTuple singletonTuple;

	public TupleIterable(int componentIndex, Iterable<? extends GenericEntity> entities) {
		this.componentIndex = componentIndex;
		this.entities = entities;
		this.singletonTuple = new OneDimensionalTuple(componentIndex);
	}

	@Override
	public Iterator<Tuple> iterator() {
		return new TupleIterator();
	}

	protected class TupleIterator implements Iterator<Tuple> {
		protected final Iterator<? extends GenericEntity> entitiesIterator;

		protected TupleIterator() {
			entitiesIterator = entities.iterator();
		}

		@Override
		public boolean hasNext() {
			return entitiesIterator.hasNext();
		}

		@Override
		public Tuple next() {
			singletonTuple.setValueDirectly(componentIndex, entitiesIterator.next());

			return singletonTuple;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("Cannot remove a tuple from a tuple set!");
		}

	}

}
