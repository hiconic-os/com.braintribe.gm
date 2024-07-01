// ============================================================================
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
// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
// ============================================================================
package com.braintribe.model.processing.manipulation.api;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.manipulation.AtomicManipulation;
import com.braintribe.model.generic.manipulation.LifecycleManipulation;
import com.braintribe.model.generic.manipulation.ManipulationType;
import com.braintribe.model.generic.manipulation.Owner;
import com.braintribe.model.generic.manipulation.PropertyManipulation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.Property;

/**
 * Experimental!!! DO NOT USE YET
 * 
 * @author peter.gazdik
 */
public interface AtomicManipulationOracle {

	ManipulationOracle getManipulationOracle();

	AtomicManipulation getManipulation();

	boolean isProperty();

	boolean isLifecycle();

	boolean isVoid();

	ManipulationType manipulationType();

	String propertyName();

	Property property();

	Owner owner();

	/**
	 * Returns the corresponding {@link PropertyManipulation}'s {@link Owner#ownerEntity()} or
	 * {@link LifecycleManipulation}'s {@link LifecycleManipulation#manipulatedEntity()}
	 */
	GenericEntity getManipulatedEntity();

	GenericEntity findManipulatedEntity();

	String manipulatedEntitySignature();

	EntityType<?> manipulatedEntityType();

	/**
	 * Returns the resolved entity for the underlying owner reference. Note that this method might lead to a query on
	 * the underlying session, so in case you want to resolve multiple manipulation's owner, consider
	 * {@link ManipulationOracle#resolveAll(Iterable)} for better performance.
	 */
	GenericEntity resolveManipulatedEntity();
}
