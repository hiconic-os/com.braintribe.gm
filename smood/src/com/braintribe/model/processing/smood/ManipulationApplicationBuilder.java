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
package com.braintribe.model.processing.smood;

import com.braintribe.model.access.ModelAccessException;
import com.braintribe.model.accessapi.ManipulationRequest;
import com.braintribe.model.generic.manipulation.CollectionManipulation;
import com.braintribe.model.generic.manipulation.DeleteMode;
import com.braintribe.model.processing.session.api.managed.ManipulationLenience;
import com.braintribe.model.processing.session.api.managed.ManipulationMode;
import com.braintribe.model.processing.session.api.managed.ManipulationReport;

public interface ManipulationApplicationBuilder extends ManipulationReport {

	ManipulationReport request(ManipulationRequest request) throws ModelAccessException;

	ManipulationApplicationBuilder generateId(boolean generateId);
	boolean generateId();

	ManipulationApplicationBuilder ignoreUnknownEntitiesManipulations(boolean ignoreUnknownEntitiesManipulations);
	boolean ignoreManipulationsReferingToUnknownEntities();

	/**
	 * Ensures the behavior of apply manipulation is conform to {@link ManipulationLenience#manifestOnUnknownEntity}. T
	 */
	ManipulationApplicationBuilder manifestUnkownEntities(boolean manifestUnknownEntities);
	boolean manifestUnknownEntities();

	/**
	 * Ensures that {@link CollectionManipulation} is ignored if the corresponding (collection) property is absent. This is usually used when
	 * {@link #manifestUnknownEntities()} is true, as every created unknown entity has all it's properties absent and is expected to be refreshed
	 * after the manipulation is applied, so for performance reasons we skip all the collection manipulations.
	 */
	ManipulationApplicationBuilder ignoreAbsentCollectionManipulations(boolean ignoreAbsentCollectionManipulations);
	boolean ignoreAbsentCollectionManipulations();

	ManipulationApplicationBuilder manipulationMode(ManipulationMode mode);
	ManipulationMode getManipulationMode();

	/**
	 * Forces the Smood to keep referential integrity. This means that manipulations with {@link DeleteMode#ignoreReferences} are treated as if the
	 * mode was {@link DeleteMode#failIfReferenced}.
	 */
	ManipulationApplicationBuilder checkRefereesOnDelete(boolean checkRefereesOnDelete);

	ManipulationApplicationBuilder manipulationApplicationListener(ManipulationApplicationListener listener);
	ManipulationApplicationListener getManipulationApplicationListener();

}
