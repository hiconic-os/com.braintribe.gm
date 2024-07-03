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
package com.braintribe.model.processing.session.impl.managed;

import com.braintribe.logging.Logger;
import com.braintribe.model.generic.session.exception.GmSessionRuntimeException;
import com.braintribe.model.processing.query.parser.QueryParser;
import com.braintribe.model.processing.query.parser.api.GmqlParsingError;
import com.braintribe.model.processing.query.parser.api.ParsedQuery;
import com.braintribe.model.query.EntityQuery;
import com.braintribe.model.query.PropertyQuery;
import com.braintribe.model.query.Query;
import com.braintribe.model.query.SelectQuery;

public class QueryParserHelper {

	protected static Logger logger = Logger.getLogger(QueryParserHelper.class);
	
	
	public static EntityQuery parseEntityQuery (String queryString) {
		Query entityQuery = parseQuery(queryString);
		if (!(entityQuery instanceof EntityQuery))  {
			throw new GmSessionRuntimeException("Query: "+queryString+" is not an EntityQuery.");
		}
		return (EntityQuery) entityQuery;

	}
	
	public static PropertyQuery parsePropertyQuery (String queryString) {
		Query entityQuery = parseQuery(queryString);
		if (!(entityQuery instanceof PropertyQuery))  {
			throw new GmSessionRuntimeException("Query: "+queryString+" is not a PropertyQuery.");
		}
		return (PropertyQuery) entityQuery;

	}

	public static SelectQuery parseSelectQuery (String queryString) {
		Query entityQuery = parseQuery(queryString);
		if (!(entityQuery instanceof SelectQuery))  {
			throw new GmSessionRuntimeException("Query: "+queryString+" is not a SelectQuery.");
		}
		return (SelectQuery) entityQuery;

	}

	
	public static Query parseQuery (String queryString) {
		ParsedQuery parsedQuery = QueryParser.parse(queryString);
		if (parsedQuery.getErrorList().isEmpty()) {
			Query query = parsedQuery.getQuery();
			return query;
		} else {
			StringBuilder msg = new StringBuilder();
			msg.append("The query: "+queryString+" could not be parsed to a valid query.");
			for (GmqlParsingError error : parsedQuery.getErrorList()) {
				msg.append("\n").append(error.getMessage());
			}
			logger.error(msg.toString());
			throw new GmSessionRuntimeException(msg.toString());
		}

	}
	
}
