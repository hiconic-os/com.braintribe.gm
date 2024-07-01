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
package com.braintribe.model.processing.session.api.collaboration;

import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.value.EntityReference;
import com.braintribe.model.processing.session.api.managed.ManagedGmSession;
import com.braintribe.model.resource.Resource;
import com.braintribe.model.smoodstorage.stages.PersistenceStage;

/**
 * An abstraction for a persistent layer built with the collaborative architecture in mind.
 * <p>
 * As of now, there is only one implementation - the CollaborativeSmoodAccess.
 * 
 * @author peter.gazdik
 */
public interface CollaborativeAccess {

	String getAccessId();

	/**
	 * Returns the {@link ReadWriteLock} used within this persistence. This is used to make sure the operations on persistence stages can be made
	 * thread-safe in a scope bigger than just the persistence itself (e.g. when manipulation files externally.)
	 */
	ReadWriteLock getLock();

	StageStats getStageStats(String name);

	void pushPersistenceStage(String name);

	void renamePersistenceStage(String oldName, String newName);

	void mergeStage(String sourceName, String targetName);

	/** Resets the access to it's initial state. This means all data persisted by the access on top of it's initial state will be deleted. */
	void reset();

	/**
	 * In case of GMML stage returns a {@link Stream} with 1 or 2 suppliers (data, model) of the {@link Resource} representing the corresponding GMML
	 * file. For non-GMML returns an empty stream.
	 * <p>
	 * Related to {@link CollaborativeManipulationPersistence#getResourcesForStage(String)}
	 */
	Stream<Resource> getResourcesForStage(String name);

	/**
	 * In case of GMML stage returns a {@link Stream} with 1 or 2 suppliers of touched entities (data, model). For non-GMML returns an empty stream.
	 * 
	 * Related to {@link CollaborativeManipulationPersistence#getModifiedEntitiesForStage(String)}
	 */
	Stream<Supplier<Set<GenericEntity>>> getModifiedEntitiesForStage(String name);

	Set<GenericEntity> getCreatedEntitiesForStage(String name);

	PersistenceStage getStageByName(String name);

	PersistenceStage getStageForReference(EntityReference reference);

	PersistenceStage findStageForReference(EntityReference reference);

	/**
	 * TODO explain this is dangerous to use.
	 */
	<R> R readWithCsaSession(Function<ManagedGmSession, R> readingFunction);

}
