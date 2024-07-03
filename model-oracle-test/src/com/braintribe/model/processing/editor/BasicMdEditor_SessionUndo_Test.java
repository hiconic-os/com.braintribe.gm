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
package com.braintribe.model.processing.editor;

import static com.braintribe.utils.lcd.CollectionTools2.asList;

import org.junit.Before;
import org.junit.Test;

import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.meta.data.prompt.Visible;
import com.braintribe.model.processing.meta.editor.BasicModelMetaDataEditor;
import com.braintribe.model.processing.meta.editor.ModelMetaDataEditor;
import com.braintribe.model.processing.oracle.model.ModelNames;
import com.braintribe.model.processing.oracle.model.ModelOracleModelProvider;
import com.braintribe.model.processing.oracle.model.basic.animal.Animal;
import com.braintribe.model.processing.oracle.model.basic.animal.Gender;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.processing.session.api.transaction.NestedTransaction;
import com.braintribe.model.util.meta.NewMetaModelGeneration;
import com.braintribe.testing.tools.gm.GmTestTools;

/**
 * @author peter.gazdik
 */
public class BasicMdEditor_SessionUndo_Test implements ModelNames {

	private PersistenceGmSession session;
	private ModelMetaDataEditor mammalEditor;

	@Before
	public void initialize() {
		session = GmTestTools.newSessionWithSmoodAccessMemoryOnly();
		mammalEditor = buildMammalModelEditor();
	}

	private BasicModelMetaDataEditor buildMammalModelEditor() {
		NewMetaModelGeneration mmg = new NewMetaModelGeneration(session::create).withValidation();
		GmMetaModel animalModel = mmg.buildMetaModel(ANIMAL_MODEL, ModelOracleModelProvider.animalTypes, asList(mmg.rootMetaModel()));
		GmMetaModel mammalModel = mmg.buildMetaModel(MAMMAL_MODEL, ModelOracleModelProvider.mammalTypes, asList(animalModel));

		session.commit();

		return BasicModelMetaDataEditor.create(mammalModel).withSession(session).done();
	}

	@Test
	public void canUndoEntityMd() throws Exception {
		testUndoIsFine(this::addEntityMd);
	}

	private void addEntityMd() {
		mammalEditor.onEntityType(Animal.T).addMetaData(visible());
	}

	@Test
	public void canUndoPropertyMd() throws Exception {
		testUndoIsFine(this::addPropertyMd);
	}

	private void addPropertyMd() {
		mammalEditor.onEntityType(Animal.T).addPropertyMetaData(GENDER, visible());
	}

	@Test
	public void canUndoEnumMd() throws Exception {
		testUndoIsFine(this::addEnumMd);
	}

	private void addEnumMd() {
		mammalEditor.onEnumType(Gender.class).addMetaData(visible());
	}

	@Test
	public void canUndoConstantMd() throws Exception {
		testUndoIsFine(this::addConstantMd);
	}

	private void testUndoIsFine(Runnable mdAdding) {
		NestedTransaction nestedTransaction = session.getTransaction().beginNestedTransaction();
		mdAdding.run();
		nestedTransaction.rollback();

		mdAdding.run();
		session.commit();
	}

	private void addConstantMd() {
		mammalEditor.addConstantMetaData(Gender.M, visible());
	}

	private Visible visible() {
		return session.create(Visible.T);
	}

}
