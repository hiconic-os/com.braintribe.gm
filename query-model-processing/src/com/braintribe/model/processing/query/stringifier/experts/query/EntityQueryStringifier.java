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
package com.braintribe.model.processing.query.stringifier.experts.query;

import com.braintribe.model.processing.query.api.stringifier.QueryStringifierRuntimeException;
import com.braintribe.model.processing.query.stringifier.BasicQueryStringifierContext;
import com.braintribe.model.processing.query.stringifier.experts.AbstractQueryStringifier;
import com.braintribe.model.query.EntityQuery;

public class EntityQueryStringifier extends AbstractQueryStringifier<EntityQuery, BasicQueryStringifierContext> {
	@Override
	public String stringify(EntityQuery query, BasicQueryStringifierContext context) throws QueryStringifierRuntimeException {
		context.pushDefaultSourceType(query.getEntityTypeSignature());
		try {
			return stringifyIt(query, context);
		} finally {
			context.popDefaultSourceType();
		}
	}

	private String stringifyIt(EntityQuery query, BasicQueryStringifierContext context) throws QueryStringifierRuntimeException {
		// Create StringBuilder and add "distinct" and "from" to query
		StringBuilder queryString = new StringBuilder();

		if (query.getDistinct()) {
			queryString.append("distinct ");
		}
		queryString.append("from ");

		// Get type-signature (Default-Alias needed for null sources)
		String typeSignature = query.getEntityTypeSignature();
		if (typeSignature == null) {
			typeSignature = "<?>";
		}

		context.setDefaultAliasName(context.getFreeAliasNameForTypeSignature(typeSignature));
		queryString.append(context.getShortening().shorten(typeSignature));

		if (hasCondition(query) || hasOrdering(query)) {
			// Appending entityAlias replaceTag
			queryString.append(" ").append(context.getReplaceAliasTag());
		}

		appendCondition(query, context, queryString);
		appendOrdering(query, context, queryString);
		appendPaging(query, context, queryString);

		// Return result
		context.ReplaceAliasTags(queryString);
		return queryString.toString();
	}
}
