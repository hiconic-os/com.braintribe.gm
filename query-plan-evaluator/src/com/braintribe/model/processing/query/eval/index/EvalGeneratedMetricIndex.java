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
package com.braintribe.model.processing.query.eval.index;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Stream;

import com.braintribe.model.processing.query.eval.api.QueryEvaluationContext;
import com.braintribe.model.processing.query.eval.api.Tuple;
import com.braintribe.model.queryplan.index.GeneratedMetricIndex;

/**
 * 
 */
public class EvalGeneratedMetricIndex extends EvalGeneratedIndex implements EvalMetricIndex {

	protected NavigableMap<Object, Set<Tuple>> treeMap;

	public EvalGeneratedMetricIndex(QueryEvaluationContext context, GeneratedMetricIndex index) {
		super(context, index, new TreeMap<>());

		this.treeMap = (NavigableMap<Object, Set<Tuple>>) indexMap;
	}

	@Override
	public Iterable<Tuple> getIndexRange(Object from, Boolean fromInclusive, Object to, Boolean toInclusive) {
		Map<Object, Set<Tuple>> subMap;

		if (fromInclusive == null)
			subMap = treeMap.headMap(to, toInclusive);
		else if (toInclusive == null)
			subMap = treeMap.tailMap(from, fromInclusive);
		else
			subMap = treeMap.subMap(from, fromInclusive, to, toInclusive);

		return asIterable(subMap);
	}

	@Override
	public Iterable<Tuple> getFullRange(boolean reverseOrder) {
		Map<Object, Set<Tuple>> fullMap = reverseOrder ? treeMap.descendingMap() : treeMap;

		return asIterable(fullMap);
	}

	private CollectionOfCollectionsIterable<Tuple> asIterable(Map<Object, Set<Tuple>> map) {
		return new CollectionOfCollectionsIterable<>(map.values());
	}

	static class CollectionOfCollectionsIterable<T> implements Iterable<T> {

		private final Collection<? extends Collection<? extends T>> collection2D;

		public CollectionOfCollectionsIterable(Collection<? extends Collection<? extends T>> collection2D) {
			this.collection2D = collection2D;
		}

		@Override
		public Iterator<T> iterator() {
			return stream().iterator();
		}

		public Stream<T> stream() {
			return collection2D.stream().flatMap(Collection::stream);
		}

	}
}
