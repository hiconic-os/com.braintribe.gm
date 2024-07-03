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
package com.braintribe.model.processing.query.test.check;

import static com.braintribe.utils.lcd.CollectionTools2.asSet;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.query.PropertyOperand;
import com.braintribe.model.queryplan.index.RepositoryMetricIndex;
import com.braintribe.model.queryplan.set.IndexOrderedSet;
import com.braintribe.model.queryplan.set.IndexSubSet;
import com.braintribe.model.queryplan.set.PaginatedSet;
import com.braintribe.model.queryplan.set.SourceSet;
import com.braintribe.model.queryplan.set.join.EntityJoin;
import com.braintribe.model.queryplan.set.join.JoinKind;
import com.braintribe.model.queryplan.value.AggregateFunction;
import com.braintribe.model.queryplan.value.AggregationFunctionType;
import com.braintribe.model.queryplan.value.QueryFunctionValue;
import com.braintribe.model.queryplan.value.StaticValue;
import com.braintribe.model.queryplan.value.TupleComponent;
import com.braintribe.model.queryplan.value.ValueProperty;

/**
 * 
 */
public class AbstractQueryPlanAssemblyChecker<T extends AbstractQueryPlanAssemblyChecker<T>> extends AbstractEntityAssemblyChecker<T> {

	public AbstractQueryPlanAssemblyChecker(GenericEntity root) {
		super(root);
	}

	public T isTupleComponent_(int index) {
		return hasType(TupleComponent.T).whereProperty("tupleComponentIndex").is_(index).close();
	}

	public T isValueProperty_(String propertyPath) {
		return isValueProperty(propertyPath).close();
	}

	public T isValueProperty(String propertyPath) {
		return hasType(ValueProperty.T).whereProperty("propertyPath").is_(propertyPath);
	}

	public T isStaticSet_(Object... elements) {
		return isStaticValue_(asSet(elements));
	}

	public T isStaticValue_(Object value) {
		return hasType(StaticValue.T).whereValue().is_(value).close();
	}

	public T isStaticValue_(EntityType<? extends GenericEntity> et, Object id, String partition) {
		return hasType(StaticValue.T) //
				.whereValue().isReference_(et.getTypeSignature(), id, partition) //
				.close();
	}

	public T whereValue() {
		return whereProperty("value");
	}

	public T hasValues(int size) {
		return whereProperty("values").isListWithSize(size);
	}

	public T whereOperand() {
		return whereProperty("operand");
	}

	public T isQueryFunctionValueAndQf() {
		return hasType(QueryFunctionValue.class).whereProperty("queryFunction");
	}

	public T isSourceSet_(EntityType<?> soureType) {
		return hasType(SourceSet.T).whereProperty("typeSignature").is_(soureType.getTypeSignature()).close();
	}

	public T isIndexSubSet_(EntityType<?> soureType, String property) {
		return isIndexSubSet(soureType, property).close();
	}

	public T isIndexSubSet(EntityType<?> soureType, String property) {
		return hasType(IndexSubSet.T) //
				.whereProperty("typeSignature").is_(soureType.getTypeSignature()) //
				.whereProperty("propertyName").is_(property);
	}

	public T isIndexOrderedSet_(EntityType<?> soureType, String property, boolean descending) {
		return hasType(IndexOrderedSet.T) //
				.whereProperty("typeSignature").is_(soureType.getTypeSignature()) //
				.whereProperty("propertyName").is_(property) //
				.whereProperty("descending").is_(descending) //
				.whereProperty("metricIndex").hasType_(RepositoryMetricIndex.T) //
				.close();
	}

	public T isPaginatedSetWithOperand(Integer max, Integer offset) {
		return hasType(PaginatedSet.T) //
				.whereProperty("limit").is_(max) //
				.whereProperty("offset").is_(offset) //
				.whereOperand();
	}

	public T isEntityJoin(JoinKind joinKind) {
		return hasType(EntityJoin.T).whereProperty("joinKind").is_(joinKind);
	}
	public T isPropertyOperandAndSource(String propertyName) {
		return isPropertyOperand(propertyName).whereProperty("source");
	}

	public T isPropertyOperand(String propertyName) {
		return hasType(PropertyOperand.T) //
				.whereProperty("propertyName").is_(propertyName);
	}

	public T isAggregateFunction(AggregationFunctionType type) {
		return hasType(AggregateFunction.T) //
				.whereProperty("aggregationFunctionType").is_(type);
	}

}
