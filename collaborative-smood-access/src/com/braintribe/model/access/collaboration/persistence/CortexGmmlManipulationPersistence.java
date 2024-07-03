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

import static com.braintribe.utils.lcd.CollectionTools2.newList;
import static com.braintribe.utils.lcd.CollectionTools2.newMap;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.manipulation.AtomicManipulation;
import com.braintribe.model.generic.manipulation.Manipulation;
import com.braintribe.model.generic.manipulation.ManipulationType;
import com.braintribe.model.generic.manipulation.PropertyManipulation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelException;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.LinearCollectionType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.value.EntityReference;
import com.braintribe.model.meta.GmModelElement;
import com.braintribe.model.processing.manipulation.basic.tools.ManipulationTools;
import com.braintribe.model.processing.session.api.collaboration.ManipulationPersistenceException;
import com.braintribe.model.processing.session.api.collaboration.PersistenceInitializationContext;
import com.braintribe.model.processing.session.api.managed.EntityManager;
import com.braintribe.model.processing.session.api.managed.ManipulationMode;
import com.braintribe.model.resource.Resource;

public class CortexGmmlManipulationPersistence extends AbstractGmmlManipulationPersistence {

	private final boolean mergeModelAndData;

	private File modelFile;
	private File dataFile;

	private Map<Object, String> modelVariables = newMap();
	private Map<Object, String> dataVariables = newMap();

	public CortexGmmlManipulationPersistence(boolean mergeModelAndData) {
		this.mergeModelAndData = mergeModelAndData;
	}

	@Override
	public void configureStage(File parentFolder, String stageName) {
		this.modelFile = createStorageFile(parentFolder, "model");
		this.dataFile = createStorageFile(parentFolder, "data");
		this.stage.setName(stageName);

		configureStageFolder(dataFile.getParentFile());
	}

	@Override
	public void initializeModels(PersistenceInitializationContext context) throws ManipulationPersistenceException {
		modelVariables = initialize(context, modelFile);
	}

	@Override
	public void initializeData(PersistenceInitializationContext context) throws ManipulationPersistenceException {
		dataVariables = initialize(context, dataFile);
	}

	@Override
	public AppendedSnippet[] append(Manipulation manipulation, ManipulationMode mode) throws ManipulationPersistenceException {
		storeManMarkers();

		logAppendedManipulation(dataFile, manipulation);

		Manipulation[] splitManipulations = split(manipulation, mode);
		AppendedSnippet[] result = new AppendedSnippet[] { //
				append(modelFile, splitManipulations[0], modelVariables, mode), //
				append(dataFile, splitManipulations[1], dataVariables, mode) //
		};

		deleteManMarkers();

		return result;
	}

	private Manipulation[] split(Manipulation manipulation, ManipulationMode mode) {
		if (mergeModelAndData)
			return new Manipulation[] { null, manipulation };

		List<AtomicManipulation> modelManis = newList();
		List<AtomicManipulation> dataManis = newList();

		ManipulationTools.visit(manipulation, m -> {
			if (isSkeletonRelevant(m, mode))
				modelManis.add(m);
			else
				dataManis.add(m);
		});

		Manipulation modelM = ManipulationTools.asManipulation(modelManis);
		Manipulation dataM = ManipulationTools.asManipulation(dataManis);

		return new Manipulation[] { modelM, dataM };
	}

	@Override
	public void append(Resource[] gmmlResources, EntityManager entityManager) {
		storeManMarkers();

		append(modelFile, gmmlResources[0], modelVariables, entityManager);
		append(dataFile, gmmlResources[1], dataVariables, entityManager);

		deleteManMarkers();
	}

	/**
	 * Manipulations on {@link GmModelElement} instances which add something that is not necessarily a GmModelElement (for now basically MetaData) are
	 * not skeleton relevant and cannot be part of model manipulations.
	 */
	private boolean isSkeletonRelevant(AtomicManipulation m, ManipulationMode mode) {
		if (m.manipulationType() == ManipulationType.VOID)
			return false;

		EntityType<?> ownerType = ownerType(m, mode);

		if (!GmModelElement.T.isAssignableFrom(ownerType))
			return false;

		if (!(m instanceof PropertyManipulation))
			return true;

		GenericModelType extractedPropertyType = getExtractedPropertyType((PropertyManipulation) m);
		if (!extractedPropertyType.isEntity())
			return true;

		return GmModelElement.T.isAssignableFrom(extractedPropertyType);
	}

	private EntityType<?> ownerType(AtomicManipulation m, ManipulationMode mode) {
		if (m instanceof PropertyManipulation) {
			return ((PropertyManipulation) m).getOwner().ownerEntityType();
		}

		if (mode == ManipulationMode.LOCAL)
			return m.manipulatedEntity().entityType();

		String typeSignature = ((EntityReference) m.manipulatedEntity()).getTypeSignature();
		return GMF.getTypeReflection().getEntityType(typeSignature);
	}

	/**
	 * Extracted means that in case it is a {@link LinearCollectionType}, we "extract" the element type and return that. This obviously cannot work
	 * for a map, but that's OK, as we have no map property on any of the {@link GmModelElement} sub-types.
	 */
	private GenericModelType getExtractedPropertyType(PropertyManipulation m) {
		Property property = m.getOwner().property();
		GenericModelType propertyType = property.getType();

		switch (propertyType.getTypeCode()) {
			case listType:
			case setType:
				return ((LinearCollectionType) propertyType).getCollectionElementType();
			case mapType:
				throw new GenericModelException("Map property is not expected on a GmModelElement entity.");
			default:
				return propertyType;
		}
	}

	@Override
	protected Stream<Map<Object, String>> getVariablesMapStream() {
		return Stream.of(dataVariables, modelVariables);
	}

	@Override
	public Stream<File> getGmmlStageFiles() {
		return Stream.of(dataFile, modelFile);
	}

	@Override
	public String toString() {
		return "CortexGmmlManipulationPersistence. Model File: " + modelFile + ", Data File: " + dataFile;
	}
}
