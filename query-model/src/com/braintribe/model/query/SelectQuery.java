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
package com.braintribe.model.query;

import java.util.Arrays;
import java.util.List;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.criteria.TraversingCriterion;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.query.conditions.Condition;

/**
 * A {@link Query} that encapsulates a request for entities based on a list of selections {@link #setSelections(List)}
 * defining items that are to be returned in a query result.
 */
public interface SelectQuery extends Query {

	EntityType<SelectQuery> T = EntityTypes.T(SelectQuery.class);

	void setFroms(List<From> froms);
	List<From> getFroms();

	/**
	 * Sets a list of selections. A selection is either a static value (String, Integer, Date or even
	 * {@link GenericEntity}) or an instance of {@link Operand}.
	 */
	void setSelections(List<Object> selections);
	List<Object> getSelections();

	@Deprecated
	QueryContext getQueryContext();
	@Deprecated
	void setQueryContext(QueryContext queryContext);

	GroupBy getGroupBy();
	void setGroupBy(GroupBy groupBy);

	Condition getHaving();
	void setHaving(Condition having);
	
	static SelectQuery create(From... froms) {
		SelectQuery query = SelectQuery.T.create();
		query.getFroms().addAll(Arrays.asList(froms));
		return query;
	}
	
	default SelectQuery select(Object... selections) {
		getSelections().addAll(Arrays.asList(selections));
		return this;
	}
	
	@Override
	default SelectQuery where(Condition condition) {
		Query.super.where(condition);
		return this;
	}
	
	@Override
	default SelectQuery distinct() {
		Query.super.distinct();
		return this;
	}
	
	@Override
	default SelectQuery orderBy(Ordering ordering) {
		Query.super.orderBy(ordering);
		return this;
	}

	@Override
	default SelectQuery orderBy(Object orderValue) {
		Query.super.orderBy(orderValue);
		return this;
	}
	
	@Override
	default SelectQuery orderBy(OrderingDirection direction, Object orderValue) {
		Query.super.orderBy(direction, orderValue);
		return this;
	}
	
	@Override
	default SelectQuery paging(Paging paging) {
		Query.super.paging(paging);
		return this;
	}
	
	@Override
	default SelectQuery paging(int startIndex, int pageSize) {
		Query.super.paging(Paging.create(startIndex, pageSize));
		return this;
	}
	
	@Override
	default SelectQuery limit(int limit) {
		Query.super.paging(Paging.create(0, limit));
		return this;
	}
	
	@Override
	default SelectQuery tc(TraversingCriterion tc) {
		Query.super.tc(tc);
		return this;
	}
	
	@Override
	default SelectQuery restriction(Restriction restriction) {
		Query.super.restriction(restriction);
		return this;
	}


}
