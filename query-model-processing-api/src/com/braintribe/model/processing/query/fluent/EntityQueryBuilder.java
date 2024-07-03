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
package com.braintribe.model.processing.query.fluent;

import static com.braintribe.utils.lcd.CollectionTools2.newSet;

import java.util.Set;
import java.util.function.Function;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.criteria.TraversingCriterion;
import com.braintribe.model.generic.processing.pr.fluent.CriterionBuilder;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.query.EntityQuery;
import com.braintribe.model.query.Join;
import com.braintribe.model.query.OrderingDirection;
import com.braintribe.model.query.Paging;
import com.braintribe.model.query.Source;

public class EntityQueryBuilder extends AbstractQueryBuilder<EntityQuery> {

	public static final String DEFAULT_SOURCE = null;

	protected EntityQueryBuilder() {
		this(EntityType::create);
	}

	protected EntityQueryBuilder(Function<EntityType<?>, GenericEntity> factory) {
		super((EntityQuery) factory.apply(EntityQuery.T), factory);
	}

	public static EntityQueryBuilder from(Class<? extends GenericEntity> clazz) {
		return from(clazz.getName());
	}

	public static EntityQueryBuilder from(EntityType<?> type) {
		return from(type.getTypeSignature());
	}

	public static EntityQueryBuilder from(String typeSignature) {
		return getEntityQueryBuilder(typeSignature);
	}

	public static EntityQueryBuilder from(String typeSignature, Function<EntityType<?>, GenericEntity> factory) {
		return getEntityQueryBuilder(typeSignature, factory);
	}
	
	protected static EntityQueryBuilder getEntityQueryBuilder(String typeSignature) {
		return getEntityQueryBuilder(typeSignature, EntityType::create);
	}

	private static EntityQueryBuilder getEntityQueryBuilder(String typeSignature, Function<EntityType<?>, GenericEntity> factory) {
		EntityQueryBuilder builder = new EntityQueryBuilder(factory);
		builder.query.setEntityTypeSignature(typeSignature);
		return builder;
	}

	public EntityQueryBuilder join(String sourceAlias, String propertyName, String alias) {
		Source source = acquireSource(sourceAlias);
		Join join = newGe(Join.T);
		join.setSource(source);
		join.setProperty(propertyName);
		Set<Join> joins = source.getJoins();
		if (joins == null)
			source.setJoins(joins = newSet());

		joins.add(join);
		registerSource(alias, join);
		join.setName(alias);
		return this;
	}

	@Override
	public ConditionBuilder<EntityQueryBuilder> where() {
		return (ConditionBuilder<EntityQueryBuilder>) super.where();
	}

	@Override
	public EntityQueryBuilder limit(int limit) {
		aquirePaging().setPageSize(limit);
		return this;
	}

	@Override
	public EntityQueryBuilder paging(int pageSize, int pageStart) {
		Paging paging = aquirePaging();
		paging.setPageSize(pageSize);
		paging.setStartIndex(pageStart);
		return this;
	}

	@Override
	public OperandBuilder<EntityQueryBuilder> orderBy() {
		return (OperandBuilder<EntityQueryBuilder>) super.orderBy();
	}

	@Override
	public OperandBuilder<EntityQueryBuilder> orderBy(OrderingDirection orderingDirection) {
		return (OperandBuilder<EntityQueryBuilder>) super.orderBy(orderingDirection);
	}

	@Override
	public EntityQueryBuilder orderBy(String propertyName) {
		return (EntityQueryBuilder) super.orderBy(propertyName);
	}

	@Override
	public EntityQueryBuilder orderBy(String propertyName, OrderingDirection orderingDirection) {
		return (EntityQueryBuilder) super.orderBy(propertyName, orderingDirection);
	}

	@Override
	public EntityQueryBuilder orderingDirection(OrderingDirection orderingDirection) {
		return (EntityQueryBuilder) super.orderingDirection(orderingDirection);
	}

	@Override
	public CascadedOrderingBuilder<EntityQueryBuilder> orderByCascade() {
		return (CascadedOrderingBuilder<EntityQueryBuilder>) super.orderByCascade();
	}

	@Override
	public CriterionBuilder<EntityQueryBuilder> tc() {
		return (CriterionBuilder<EntityQueryBuilder>) super.tc();
	}

	@Override
	public EntityQueryBuilder tc(TraversingCriterion traversingCriterion) {
		return (EntityQueryBuilder) super.tc(traversingCriterion);
	}

	@Override
	public Source getFirstSource() {
		return null;
	}

}
