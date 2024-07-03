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

import static com.braintribe.utils.lcd.CollectionTools2.newList;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.braintribe.model.processing.query.eval.api.QueryEvaluationContext;
import com.braintribe.model.processing.query.eval.api.Tuple;
import com.braintribe.model.processing.query.eval.set.base.AbstractEvalTupleSet;
import com.braintribe.model.processing.query.eval.tools.QueryEvaluationTools;
import com.braintribe.model.processing.query.eval.tools.TupleComparator;
import com.braintribe.model.queryplan.set.OrderedSet;

/**
 * 
 */
public class EvalOrderedSet extends AbstractEvalTupleSet {

	protected final List<Tuple> tuples;

	public EvalOrderedSet(OrderedSet orderedSet, QueryEvaluationContext context) {
		super(context);

		tuples = newList();

		QueryEvaluationTools.addAllTuples(orderedSet.getOperand(), tuples, context);
		Collections.sort(tuples, new TupleComparator(orderedSet.getSortCriteria(), context));
	}

	@Override
	public Iterator<Tuple> iterator() {
		return tuples.iterator();
	}
}
