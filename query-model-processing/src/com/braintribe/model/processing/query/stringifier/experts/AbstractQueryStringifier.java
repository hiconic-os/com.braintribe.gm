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
package com.braintribe.model.processing.query.stringifier.experts;

import com.braintribe.model.processing.query.api.stringifier.experts.Stringifier;
import com.braintribe.model.processing.query.api.stringifier.experts.StringifierContext;
import com.braintribe.model.processing.query.stringifier.BasicQueryStringifierContext;
import com.braintribe.model.query.Ordering;
import com.braintribe.model.query.Paging;
import com.braintribe.model.query.Query;
import com.braintribe.model.query.Restriction;
import com.braintribe.utils.lcd.StringTools;

public abstract class AbstractQueryStringifier<Q extends Query, C extends BasicQueryStringifierContext> implements Stringifier<Q, C> {
	protected static void replaceAll(final StringBuilder builder, final String from, final String to) {
		int index = builder.indexOf(from);

		while (index != -1) {
			// Replace and move to the end of the replacement
			builder.replace(index, index + from.length(), to);
			index += to.length();

			index = builder.indexOf(from, index);
		}
	}

	protected boolean hasCondition(Q query) {
		Restriction restriction = query.getRestriction();
		return (restriction != null && restriction.getCondition() != null);
	}

	protected boolean hasOrdering(Q query) {
		return query.getOrdering() != null;
	}

	protected boolean appendOrdering(Q query, StringifierContext context, StringBuilder queryString) {
		Ordering ordering = query.getOrdering();
		if (ordering != null) {
			String orderString = context.stringify(ordering);
			if (!StringTools.isEmpty(orderString)) {
				queryString.append(" order by ");
				queryString.append(orderString);
				return true;
			}
		}

		return false;
	}

	protected boolean appendCondition(Q query, StringifierContext context, final StringBuilder queryString) {
		if (hasCondition(query)) {
			// Stringify condition of Query
			queryString.append(" where ");
			context.stringifyAndAppend(query.getRestriction().getCondition(), queryString);
			return true;
		}

		return false;
	}

	protected boolean appendPaging(Q query, StringifierContext context, final StringBuilder queryString) {
		Restriction restriction = query.getRestriction();
		if (restriction != null) {
			Paging paging = restriction.getPaging();
			if (paging != null) {
				context.stringifyAndAppend(paging, queryString);
				return true;
			}
		}

		return false;
	}
}
