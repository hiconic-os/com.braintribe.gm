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
package com.braintribe.model.access.collaboration.persistence;

import static com.braintribe.utils.lcd.CollectionTools2.newMap;

import java.io.File;
import java.util.Map;
import java.util.stream.Stream;

import com.braintribe.model.generic.manipulation.Manipulation;
import com.braintribe.model.processing.session.api.collaboration.ManipulationPersistenceException;
import com.braintribe.model.processing.session.api.collaboration.PersistenceInitializationContext;
import com.braintribe.model.processing.session.api.managed.EntityManager;
import com.braintribe.model.processing.session.api.managed.ManipulationMode;
import com.braintribe.model.resource.Resource;

public class BasicGmmlManipulationPersistence extends AbstractGmmlManipulationPersistence {

	private File dataFile;

	private Map<Object, String> dataVariables = newMap();

	@Override
	public void configureStage(File parentFolder, String stageName) {
		this.dataFile = createStorageFile(parentFolder, "data");
		this.stage.setName(stageName);

		configureStageFolder(parentFolder);
	}

	@Override
	public void initializeData(PersistenceInitializationContext context) throws ManipulationPersistenceException {
		dataVariables = initialize(context, dataFile);
	}

	@Override
	public AppendedSnippet[] append(Manipulation manipulation, ManipulationMode mode) {
		storeManMarkers();

		logAppendedManipulation(dataFile, manipulation);
		AppendedSnippet[] result = new AppendedSnippet[] { null, append(dataFile, manipulation, dataVariables, mode) };

		deleteManMarkers();

		return result;
	}

	@Override
	public void append(Resource[] gmmlResources, EntityManager entityManager) {
		storeManMarkers();

		append(dataFile, gmmlResources[1], dataVariables, entityManager);

		deleteManMarkers();
	}

	@Override
	protected Stream<Map<Object, String>> getVariablesMapStream() {
		return Stream.of(dataVariables);
	}

	@Override
	public Stream<File> getGmmlStageFiles() {
		return Stream.of(dataFile);
	}

}
