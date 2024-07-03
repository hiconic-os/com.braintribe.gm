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
package com.braintribe.model.access.crud.support.read;

import java.util.function.Predicate;

import com.braintribe.model.access.crud.api.read.PopulationReadingContext;
import com.braintribe.model.access.crud.api.read.PropertyReadingContext;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;

/**
 * A collection of {@link Predicate}'s that might be handy when registering
 * experts for a {@link DispatchingReader} implementation.
 * 
 * @author gunther.schenk
 *
 */
public interface DispatchingPredicates {

	static Predicate<?> ALWAYS_TRUE = c -> true;
	static Predicate<?> IS_NOT_NULL = c -> c != null;

	@SuppressWarnings("unchecked")
	default <T> Predicate<T> isAlwaysTrue() {
		return (Predicate<T>) ALWAYS_TRUE;
	}

	@SuppressWarnings("unchecked")
	default <T> Predicate<T> isNotNull() {
		return (Predicate<T>) IS_NOT_NULL;
	}

	default <T extends GenericEntity> Predicate<PopulationReadingContext<T>> isExclusiveIdCondition() {
		return c -> c.getConditionAnalysis().hasIdComparisonsExclusively();
	}

	default <T extends GenericEntity> boolean isRequestedProperty(PropertyReadingContext<T> context,
			String propertyName) {
		return context.getPropertyName().equals(propertyName);
	}

	default <T extends GenericEntity> Predicate<PopulationReadingContext<T>> isRequestedTypeEqual(
			EntityType<T> comparisonType) {
		return c -> c.getRequestedType() == comparisonType;
	}
}
