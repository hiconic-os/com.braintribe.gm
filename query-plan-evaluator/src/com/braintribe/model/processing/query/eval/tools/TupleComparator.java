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

import java.util.Comparator;
import java.util.List;

import com.braintribe.model.generic.i18n.LocalizedString;
import com.braintribe.model.processing.query.eval.api.QueryEvaluationContext;
import com.braintribe.model.processing.query.eval.api.RuntimeQueryEvaluationException;
import com.braintribe.model.processing.query.eval.api.Tuple;
import com.braintribe.model.processing.query.tools.ScalarComparator;
import com.braintribe.model.queryplan.set.SortCriterion;
import com.braintribe.model.queryplan.value.Value;

/**
 * 
 */
public class TupleComparator implements Comparator<Tuple> {

	private final List<SortCriterion> sortCriteria;
	private final int valuesCount;
	private final QueryEvaluationContext context;

	public TupleComparator(List<SortCriterion> sortCriteria, QueryEvaluationContext context) {
		this.sortCriteria = sortCriteria;
		this.valuesCount = sortCriteria.size();
		this.context = context;
	}

	@Override
	public int compare(Tuple t1, Tuple t2) {
		for (int i = 0; i < valuesCount; i++) {
			SortCriterion sortCriterion = sortCriteria.get(i);
			int cmp = compareValue(t1, t2, sortCriterion.getValue());

			if (cmp != 0)
				return sortCriterion.getDescending() ? -cmp : cmp;
		}

		return 0;
	}

	private int compareValue(Tuple t1, Tuple t2, Value value) {
		Object value1 = context.resolveValue(t1, value);
		Object value2 = context.resolveValue(t2, value);

		if (value1 == value2)
			return 0;

		if (value1 == null)
			return -1;

		if (value2 == null)
			return 1;

		if (value1.getClass() != value2.getClass())
			return ScalarComparator.INSTANCE.compare(value1, value2);
		
		if (value1 instanceof String)
			return StringAlphabeticalComparator.INSTANCE.compare((String) value1, (String) value2);

		if (value1 instanceof Comparable)
			return ((Comparable<Object>) value1).compareTo(value2);

		if (value1 instanceof LocalizedString) {
			LocalizedString ls1 = (LocalizedString) value1;
			LocalizedString ls2 = (LocalizedString) value2;

			String s1 = context.resolveLocalizedString(ls1);
			String s2 = context.resolveLocalizedString(ls2);

			return StringAlphabeticalComparator.INSTANCE.compare(s1, s2);
		}

		throw new RuntimeQueryEvaluationException("Cannot compare values: [" + value1 + ", " + value2 +
				"]. Wrong type. Only Comparables and LocalizedStrings are supported!");
	}
}
