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

import java.util.List;
import java.util.Map;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.value.Variable;
import com.braintribe.model.query.Query;
import com.braintribe.model.query.Source;

/**
 * Gmql parsing results are represented by this object.
 * 
 * It contains the following items:
 * <ul>
 * <li>Query: which can be a valid query or what the parser was able to create
 * with some erroneous input</li>
 * <li>List of {@link GmqlParsingError} that were identified. The list is empty
 * if a valid query string has been provided. There are two concrete definitions
 * for errors, namely {@link GmqlSyntacticParsingError} and 
 * {@link GmqlSemanticParsingError}</li>
 * <li>Map between the aliases and actual sources {@link Source} in the query</li>
 * <li>A convenience boolean value that indicates the validity of the query</li>
 * </ul>
 * 
 */
public interface ParsedQuery extends GenericEntity {

	EntityType<ParsedQuery> T = EntityTypes.T(ParsedQuery.class);

	Query getQuery();
	void setQuery(Query query);
	
	List<GmqlParsingError> getErrorList(); 
	void setErrorList(List<GmqlParsingError> errorList);
	
	Map<String,Variable> getVariablesMap(); 
	void setVariablesMap(Map<String,Variable> variablesMap);
	
	Map<String,Source> getSourcesRegistry();
	void setSourcesRegistry(Map<String,Source> sourceRegistry);
	
	boolean getIsValidQuery();
	void setIsValidQuery(boolean isValidQuery);
	
}
