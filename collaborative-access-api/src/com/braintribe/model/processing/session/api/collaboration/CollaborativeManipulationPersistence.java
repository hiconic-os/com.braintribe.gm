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
package com.braintribe.model.processing.session.api.collaboration;

import java.io.File;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.processing.meta.oracle.ModelOracle;
import com.braintribe.model.processing.session.api.managed.ManagedGmSession;
import com.braintribe.model.resource.Resource;
import com.braintribe.model.smoodstorage.stages.PersistenceStage;

/**
 * @author peter.gazdik
 */
public interface CollaborativeManipulationPersistence extends PersistenceInitializer {

	File getStrogaBase();

	void onCollaborativeAccessInitialized(CollaborativeAccess csa, ManagedGmSession csaSession);

	void setModelOracle(ModelOracle modelOracle);

	Stream<PersistenceStage> getPersistenceStages();

	/** @return the {@link PersistenceStage} of the current appender */
	@Override
	PersistenceStage getPersistenceStage();

	PersistenceAppender getPersistenceAppender() throws ManipulationPersistenceException;

	PersistenceAppender newPersistenceAppender(String name);

	void renamePersistenceStage(String oldName, String newName);

	void mergeStage(String source, String target);

	void reset();

	/** @see CollaborativeAccess#getResourcesForStage(String) */
	Stream<Resource> getResourcesForStage(String name);

	/** @see CollaborativeAccess#getModifiedEntitiesForStage(String) */
	Stream<Supplier<Set<GenericEntity>>> getModifiedEntitiesForStage(String name);

}
