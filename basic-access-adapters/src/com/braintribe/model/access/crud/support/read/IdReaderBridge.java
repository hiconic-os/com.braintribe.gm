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

import java.util.stream.Collectors;

import com.braintribe.model.access.crud.api.query.ConditionAnalysis;
import com.braintribe.model.access.crud.api.read.EntityReader;
import com.braintribe.model.access.crud.api.read.EntityReadingContext;
import com.braintribe.model.access.crud.api.read.PopulationReader;
import com.braintribe.model.access.crud.api.read.PopulationReadingContext;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.query.conditions.Condition;

/**
 * An implementation of {@link PopulationReader} that takes the id values(referenced in {@link Condition} and provided by the {@link ConditionAnalysis})
 * and delegates them the configured {@link EntityReader}. All resulting entities are collected and
 * finally returned as a result of {@link #findEntities(PopulationReadingContext)}.
 *    
 * @author gunther.schenk
 */
public class IdReaderBridge<T extends GenericEntity> implements PopulationReader<T>, DispatchingPredicates{
	
	private EntityReader<T> target;

	private IdReaderBridge(EntityReader<T> target) {
		this.target = target;
	}

	// ***************************************************************************************************
	// Static Instantiation
	// ***************************************************************************************************

	/**
	 * @return instance of {@link IdReaderBridge} with provided target {@link EntityReader}.
	 */
	public static <T extends GenericEntity> IdReaderBridge<T> instance(EntityReader<T> target) {
		return new IdReaderBridge<>(target);
	}
	
	// ***************************************************************************************************
	// PopulationReader
	// ***************************************************************************************************

	@Override
	public Iterable<T> findEntities(PopulationReadingContext<T> context) {
		EntityType<T> requestedType = context.getRequestedType();
		ConditionAnalysis conditionAnalysis = context.getConditionAnalysis();

		// @formatter:off
		return conditionAnalysis.getComparedIds()
			.stream()
			.map(id->EntityReadingContext.create(requestedType, id))
			.map(target::getEntity)
			.filter(isNotNull())
			.collect(Collectors.toList());
		// @formatter:on
	}

}
