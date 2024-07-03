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

import static com.braintribe.utils.lcd.CollectionTools2.newSet;

import java.util.Iterator;
import java.util.Set;

import com.braintribe.model.processing.query.eval.api.QueryEvaluationContext;
import com.braintribe.model.processing.query.eval.api.Tuple;
import com.braintribe.model.processing.query.eval.set.base.AbstractEvalTupleSet;
import com.braintribe.model.processing.query.eval.tools.PopulationAsTupleIterator;
import com.braintribe.model.queryplan.set.StaticSet;

/**
 * 
 */
public class EvalStaticSet extends AbstractEvalTupleSet {

	protected final int index;
	protected final Set<Object> staticValues;

	public EvalStaticSet(StaticSet staticSet, QueryEvaluationContext context) {
		super(context);

		this.index = staticSet.getIndex();
		this.staticValues = resolveStaticValues(staticSet, context);
	}

	private Set<Object> resolveStaticValues(StaticSet staticSet, QueryEvaluationContext context) {
		Set<Object> result = newSet();

		for (Object value : staticSet.getValues()) {
			Object resolvedValue = context.resolveStaticValue(value);
			if (resolvedValue != null || value == null)
				result.add(resolvedValue);
		}

		return result;
	}

	@Override
	public Iterator<Tuple> iterator() {
		return new PopulationAsTupleIterator(staticValues, index);
	}

}
