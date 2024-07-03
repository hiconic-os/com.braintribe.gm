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
package com.braintribe.model.processing.query.stringifier.experts.query;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.LinearCollectionType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.TypeCode;
import com.braintribe.model.generic.value.PersistentEntityReference;
import com.braintribe.model.processing.query.api.stringifier.QueryStringifierRuntimeException;
import com.braintribe.model.processing.query.stringifier.BasicQueryStringifierContext;
import com.braintribe.model.processing.query.stringifier.experts.AbstractQueryStringifier;
import com.braintribe.model.query.PropertyQuery;

public class PropertyQueryStringifier extends AbstractQueryStringifier<PropertyQuery, BasicQueryStringifierContext> {
	@Override
	public String stringify(PropertyQuery query, BasicQueryStringifierContext context) throws QueryStringifierRuntimeException {
		context.pushDefaultSourceType(propertyTypeSignature(query));
		try {
			return stringifyIt(query, context);
		} finally {
			context.popDefaultSourceType();
		}
	}

	private String propertyTypeSignature(PropertyQuery query) {
		PersistentEntityReference ref = query.getEntityReference();
		if (ref == null)
			return null;

		EntityType<?> ownerType = GMF.getTypeReflection().findEntityType(ref.getTypeSignature());
		if (ownerType == null)
			return null;

		Property property = ownerType.findProperty(query.getPropertyName());
		if (property == null)
			return null;

		GenericModelType pt = property.getType();
		if (pt.getTypeCode() == TypeCode.listType || pt.getTypeCode() == TypeCode.setType)
			pt = ((LinearCollectionType) pt).getCollectionElementType();

		return pt.getTypeSignature();
	}

	private String stringifyIt(PropertyQuery query, BasicQueryStringifierContext context) throws QueryStringifierRuntimeException {
		StringBuilder queryString = new StringBuilder();

		if (query.getDistinct()) {
			queryString.append("distinct ");
		}
		queryString.append("property ");

		// Get type-signature (Default-Alias needed for null sources)
		final String propertyName = query.getPropertyName();
		context.setDefaultAliasName(propertyName);

		// Stringify type-signature of EntityReference of PropertyQuery
		queryString.append(context.escapeKeywords(propertyName));
		if (hasCondition(query) || hasOrdering(query)) {
			// Appending propertyAlias replaceTag
			queryString.append(" ").append(context.getReplaceAliasTag());
		}

		queryString.append(" of ");
		context.stringifyAndAppend(query.getEntityReference(), queryString);

		appendCondition(query, context, queryString);
		appendOrdering(query, context, queryString);
		appendPaging(query, context, queryString);

		// Return result
		context.ReplaceAliasTags(queryString);
		return queryString.toString();
	}
}
