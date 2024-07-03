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

import java.util.Collections;

import com.braintribe.model.access.crud.api.CrudExpert;
import com.braintribe.model.access.crud.api.read.EntityReader;
import com.braintribe.model.access.crud.api.read.EntityReadingContext;
import com.braintribe.model.access.crud.api.read.PopulationReader;
import com.braintribe.model.access.crud.api.read.PopulationReadingContext;
import com.braintribe.model.access.crud.api.read.PropertyReader;
import com.braintribe.model.access.crud.api.read.PropertyReadingContext;
import com.braintribe.model.generic.GenericEntity;

/**
 * A generic implementation of {@link EntityReader}, {@link PopulationReader}
 * and {@link PropertyReader} that returns an empty collection for
 * {@link #findEntities(PopulationReadingContext)} or <code> null</code> for
 * {@link #getEntity(EntityReadingContext)} and
 * {@link #getPropertyValue(PropertyReadingContext)}. This class is usually used
 * as a default expert for types of a model that are not explicitly handled by
 * another {@link CrudExpert} implementation.
 * 
 * @author gunther.schenk
 */
public class EmptyReader<T extends GenericEntity>
		implements EntityReader<T>, PopulationReader<T>, PropertyReader<T, Object> {

	public static final EmptyReader<? extends GenericEntity> INSTANCE = new EmptyReader<>();

	// ***************************************************************************************************
	// Typed Singleton
	// ***************************************************************************************************

	/**
	 * @return a (auto typed) static singleton instance of {@link EmptyReader}.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends GenericEntity> EmptyReader<T> instance() {
		return (EmptyReader<T>) EmptyReader.INSTANCE;
	}

	// ***************************************************************************************************
	// EntityReader
	// ***************************************************************************************************

	/**
	 * @return <code>null</code>
	 */
	@Override
	public T getEntity(EntityReadingContext<T> context) {
		return null;
	}

	// ***************************************************************************************************
	// PopulationReader
	// ***************************************************************************************************

	/**
	 * @return an empty (immutable) collection.
	 */
	@Override
	public Iterable<T> findEntities(PopulationReadingContext<T> context) {
		return Collections.emptyList();
	}

	// ***************************************************************************************************
	// PropertyReader
	// ***************************************************************************************************

	/**
	 * @return <code>null</code>
	 */
	@Override
	public Object getPropertyValue(PropertyReadingContext<T> context) {
		return null;
	}

}
