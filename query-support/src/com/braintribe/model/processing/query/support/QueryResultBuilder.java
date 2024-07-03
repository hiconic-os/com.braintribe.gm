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
package com.braintribe.model.processing.query.support;

import static com.braintribe.utils.lcd.CollectionTools2.newList;

import java.util.List;

import com.braintribe.model.processing.query.eval.api.EvalTupleSet;
import com.braintribe.model.processing.query.eval.api.Tuple;
import com.braintribe.model.processing.query.eval.set.HasMoreAwareSet;
import com.braintribe.model.processing.query.tools.QueryResults;
import com.braintribe.model.query.EntityQueryResult;
import com.braintribe.model.query.PropertyQueryResult;
import com.braintribe.model.query.SelectQueryResult;
import com.braintribe.model.record.ListRecord;
import com.braintribe.model.record.Record;

/**
 * 
 */
public class QueryResultBuilder {

	public static SelectQueryResult buildQueryResult(EvalTupleSet tuples, int tupleSize) {
		List<Object> results = buildQueryResultRows(tuples, tupleSize);
		boolean hasMore = hasMore(tuples);

		return QueryResults.selectQueryResult(results, hasMore);
	}

	private static List<Object> buildQueryResultRows(EvalTupleSet tuples, int tupleSize) {
		List<Object> result = newList();

		if (tupleSize == 1)
			for (Tuple tuple : tuples)
				result.add(tuple.getValue(0));
		else
			for (Tuple tuple : tuples)
				result.add(asRow(tuple, tupleSize));

		return result;
	}

	private static Record asRow(Tuple tuple, int size) {
		List<Object> values = newList(size);

		for (int i = 0; i < size; i++)
			values.add(tuple.getValue(i));

		ListRecord result = ListRecord.T.create();
		result.setValues(values);

		return result;
	}

	private static boolean hasMore(EvalTupleSet tuples) {
		return tuples instanceof HasMoreAwareSet && ((HasMoreAwareSet) tuples).hasMore();
	}

	public static EntityQueryResult buildEntityQueryResult(List<?> entities, boolean hasMore) {
		return QueryResults.entityQueryResult(entities, hasMore);

	}

	public static PropertyQueryResult buildPropertyQueryResult(Object value, boolean hasMore) {
		return QueryResults.propertyQueryResult(value, hasMore);
	}

}
