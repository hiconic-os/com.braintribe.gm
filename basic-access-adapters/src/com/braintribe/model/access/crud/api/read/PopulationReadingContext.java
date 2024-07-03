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
package com.braintribe.model.access.crud.api.read;

import com.braintribe.model.access.crud.api.DataReadingContext;
import com.braintribe.model.access.crud.api.query.ConditionAnalysis;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.query.Ordering;
import com.braintribe.model.query.Paging;
import com.braintribe.model.query.Query;
import com.braintribe.model.query.conditions.Condition;

/**
 * A {@link DataReadingContext} implementation provided to
 * {@link PopulationReader} experts containing necessary informations on the
 * expected instances.
 * 
 * @author gunther.schenk
 */
public interface PopulationReadingContext<T extends GenericEntity> extends DataReadingContext<T> {

	/**
	 * @return the actual requested type of the expected instances.
	 */
	EntityType<T> getRequestedType();

	Condition getCondition();

	Ordering getOrdering();

	ConditionAnalysis getConditionAnalysis();
	
	/**
	 * Static helper method to build a new {@link PopulationReadingContext}
	 * instance.
	 */
	static <T extends GenericEntity> PopulationReadingContext<T> create(EntityType<T> requestedType,
			Condition condition, Ordering ordering, ConditionAnalysis conditionAnalysis) {
		return create(requestedType, condition, ordering, conditionAnalysis, null);
	}

	/**
	 * Static helper method to build a new {@link PopulationReadingContext}
	 * instance.
	 */
	static <T extends GenericEntity> PopulationReadingContext<T> create(EntityType<T> requestedType,
			Condition condition, Ordering ordering, ConditionAnalysis conditionAnalysis, QueryContext queryContext) {
		return new PopulationReadingContext<T>() {
			@Override
			public Condition getCondition() {
				return condition;
			}

			@Override
			public Ordering getOrdering() {
				return ordering;
			}

			@Override
			public EntityType<T> getRequestedType() {
				return requestedType;
			}

			@Override
			public ConditionAnalysis getConditionAnalysis() {
				return conditionAnalysis;
			}
			@Override
			public QueryContext getQueryContext() {
				return queryContext;
			}
			
		};
	}

}
