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

import java.util.function.Function;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.manipulation.EntityProperty;
import com.braintribe.model.generic.pr.criteria.TraversingCriterion;
import com.braintribe.model.generic.processing.pr.fluent.CriterionBuilder;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.value.PersistentEntityReference;
import com.braintribe.model.query.OrderingDirection;
import com.braintribe.model.query.Paging;
import com.braintribe.model.query.PropertyQuery;
import com.braintribe.model.query.Source;

public class PropertyQueryBuilder extends AbstractQueryBuilder<PropertyQuery> {

	protected PropertyQueryBuilder() {
		this(EntityType::create);
	}

	protected PropertyQueryBuilder(Function<EntityType<?>, GenericEntity> factory) {
		super((PropertyQuery) factory.apply(PropertyQuery.T), factory);
	}

	/** @deprecated use {@link #forProperty(EntityType, Object, String)} */
	@Deprecated
	public static PropertyQueryBuilder forProperty(Class<? extends GenericEntity> clazz, Object id, String property) {
		return forProperty(clazz.getName(), id, property);
	}

	public static PropertyQueryBuilder forProperty(EntityType<?> type, Object id, String property) {
		return forProperty(type.getTypeSignature(), id, property);
	}

	public static PropertyQueryBuilder forProperty(EntityType<?> type, Object id, String partition, String property) {
		return forProperty(type.getTypeSignature(), id, partition, property);
	}

	public static PropertyQueryBuilder forProperty(String typeSignature, Object id, String property) {
		return forProperty(typeSignature, id, null, property);
	}

	public static PropertyQueryBuilder forProperty(String typeSignature, Object id, String partition, String property) {
		return forProperty(typeSignature, id, partition, property, EntityType::create);
	}

	public static PropertyQueryBuilder forProperty(String typeSignature, Object id, String partition, String property,
			Function<EntityType<?>, GenericEntity> factory) {

		PersistentEntityReference reference = (PersistentEntityReference) factory.apply(PersistentEntityReference.T);
		reference.setTypeSignature(typeSignature);
		reference.setRefId(id);
		reference.setRefPartition(partition);
		return forProperty(reference, property);
	}

	public static PropertyQueryBuilder forProperty(PersistentEntityReference entityReference, String property) {
		return forProperty(entityReference, property, EntityType::create);
	}

	private static PropertyQueryBuilder forProperty(PersistentEntityReference entityReference, String property,
			Function<EntityType<?>, GenericEntity> factory) {

		PropertyQueryBuilder builder = new PropertyQueryBuilder(factory);
		builder.query.setEntityReference(entityReference);
		builder.query.setPropertyName(property);
		return builder;
	}

	public static PropertyQueryBuilder forProperty(EntityProperty entityProperty) {
		return forProperty((PersistentEntityReference) entityProperty.getReference(), entityProperty.getPropertyName());
	}

	@Override
	public ConditionBuilder<PropertyQueryBuilder> where() {
		return (ConditionBuilder<PropertyQueryBuilder>) super.where();
	}

	@Override
	public PropertyQueryBuilder limit(int limit) {
		aquirePaging().setPageSize(limit);
		return this;
	}

	@Override
	public PropertyQueryBuilder paging(int pageSize, int pageStart) {
		Paging paging = aquirePaging();
		paging.setPageSize(pageSize);
		paging.setStartIndex(pageStart);
		return this;
	}

	@Override
	public OperandBuilder<PropertyQueryBuilder> orderBy() {
		return (OperandBuilder<PropertyQueryBuilder>) super.orderBy();
	}

	@Override
	public OperandBuilder<PropertyQueryBuilder> orderBy(OrderingDirection orderingDirection) {
		return (OperandBuilder<PropertyQueryBuilder>) super.orderBy(orderingDirection);
	}

	@Override
	public PropertyQueryBuilder orderBy(String propertyName) {
		return (PropertyQueryBuilder) super.orderBy(propertyName);
	}

	@Override
	public PropertyQueryBuilder orderBy(String propertyName, OrderingDirection orderingDirection) {
		return (PropertyQueryBuilder) super.orderBy(orderingDirection).property(null, propertyName);
	}

	@Override
	public PropertyQueryBuilder orderingDirection(OrderingDirection orderingDirection) {
		return (PropertyQueryBuilder) super.orderingDirection(orderingDirection);
	}

	@Override
	public CascadedOrderingBuilder<PropertyQueryBuilder> orderByCascade() {
		return (CascadedOrderingBuilder<PropertyQueryBuilder>) super.orderByCascade();
	}

	@Override
	public CriterionBuilder<PropertyQueryBuilder> tc() {
		return (CriterionBuilder<PropertyQueryBuilder>) super.tc();
	}

	@Override
	public PropertyQueryBuilder tc(TraversingCriterion traversingCriterion) {
		return (PropertyQueryBuilder) super.tc(traversingCriterion);
	}

	@Override
	public Source getFirstSource() {
		return null;
	}

}
