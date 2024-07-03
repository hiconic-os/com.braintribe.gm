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

import static com.braintribe.utils.lcd.CollectionTools2.newList;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.braintribe.cc.lcd.CodingSet;
import com.braintribe.model.processing.query.eval.api.EvalTupleSet;
import com.braintribe.model.processing.query.eval.api.QueryEvaluationContext;
import com.braintribe.model.processing.query.eval.api.Tuple;
import com.braintribe.model.processing.query.eval.set.HasMoreAwareSet;
import com.braintribe.model.queryplan.set.TupleSet;

/**
 * General class for various util methods.
 */
public class QueryEvaluationTools {

	public static List<EvalTupleSet> resolveTupleSets(List<TupleSet> tupleSets, QueryEvaluationContext context) {
		List<EvalTupleSet> result = newList(tupleSets.size());

		for (TupleSet ts: tupleSets)
			result.add(context.resolveTupleSet(ts));

		return result;
	}

	public static void addAllTuples(TupleSet tupleSet, Collection<Tuple> tuples, QueryEvaluationContext context) {
		addAllTuples(context.resolveTupleSet(tupleSet), tuples);
	}

	public static void addAllTuples(EvalTupleSet evalTupleSet, Collection<Tuple> tuples) {
		for (Tuple tuple: evalTupleSet)
			tuples.add(tuple.detachedCopy());
	}

	public static Set<Tuple> tupleHashSet(int tupleSize) {
		return CodingSet.create(new TupleHashingComparator(tupleSize));
	}

	/**
	 * I'm using the Yoda-style name so that it does not collide with {@link HasMoreAwareSet#hasMore()} (cause in that
	 * case we would not be able to make static imports inside classes implementing that interface).
	 * 
	 * May the force be with you!
	 */
	public static boolean moreHas(EvalTupleSet tupleSet) {
		return tupleSet instanceof HasMoreAwareSet && ((HasMoreAwareSet) tupleSet).hasMore();
	}

	public static Iterator<Tuple> emptyTupleIterator() {
		return Collections.<Tuple> emptySet().iterator();
	}

}
