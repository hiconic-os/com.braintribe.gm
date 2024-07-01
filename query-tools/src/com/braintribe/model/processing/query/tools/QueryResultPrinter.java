// ============================================================================
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
// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
// ============================================================================
package com.braintribe.model.processing.query.tools;

import java.util.List;
import java.util.stream.Collectors;

import com.braintribe.model.query.EntityQueryResult;
import com.braintribe.model.query.PropertyQueryResult;
import com.braintribe.model.query.QueryResult;
import com.braintribe.model.query.SelectQueryResult;
import com.braintribe.model.record.ListRecord;

/**
 * 
 */
public class QueryResultPrinter {

	public static void printQueryResult(QueryResult queryResult) {
		if (queryResult instanceof SelectQueryResult)
			printQueryResult((SelectQueryResult) queryResult);
		else if (queryResult instanceof PropertyQueryResult)
			printQueryResult((PropertyQueryResult) queryResult);
		else if (queryResult instanceof EntityQueryResult)
			printQueryResult((EntityQueryResult) queryResult);
		else
			throw new IllegalArgumentException("Unknow QueryResult: " + queryResult);
	}

	public static void printQueryResult(SelectQueryResult result) {
		System.out.println("\nSelect QueryResult:");

		printTuples(result.getResults());

		if (result.getHasMore())
			System.out.println("<and there's more...>");
	}

	public static void printTuples(List<?> tuples) {
		if (tuples.isEmpty())
			System.out.println("<void>");
		else
			for (Object o : tuples)
				System.out.println(stringifyTuple(o));
	}

	private static String stringifyTuple(Object o) {
		return (o instanceof ListRecord) ? stringifyListRecord((ListRecord) o) : "" + o;
	}

	private static String stringifyListRecord(ListRecord o) {
		return  o.getValues().stream() //
				.map(e -> "" + e) //
				.collect(Collectors.joining(", "));
	}

	public static void printQueryResult(EntityQueryResult result) {
		System.out.println("\nEntityQueryResult:");

		List<?> entities = result.getEntities();

		if (entities.isEmpty())
			System.out.println("<void>");

		for (Object o : entities)
			System.out.println(o);
	}

	public static void printQueryResult(PropertyQueryResult result) {
		System.out.println("\nPropertyQueryResult " + (result.getHasMore() ? "(hasMore)" : "") + ":");
		System.out.println(result.getPropertyValue());
	}

}
