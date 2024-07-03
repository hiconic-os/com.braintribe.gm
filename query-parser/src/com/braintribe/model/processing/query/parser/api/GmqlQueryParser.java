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
package com.braintribe.model.processing.query.parser.api;

import com.braintribe.model.query.Query;

/**
 * Gmql query parser interface that should be used by other artifacts.
 * 
 */

public interface GmqlQueryParser {

	/**
	 * Parses a string representation of a query in Gmql format. The result of
	 * the parsing process is returned in a {@link ParsedQuery}. If the query
	 * string is not valid for either syntactic or semantic reasons, a list of
	 * parsing errors is included in the wrapper object.
	 * 
	 * @param queryString
	 *            String representing a {@link Query} in Gmql syntax.
	 * @return {@link ParsedQuery} representing the result of the parsing
	 */
	ParsedQuery parse(String queryString);

}
