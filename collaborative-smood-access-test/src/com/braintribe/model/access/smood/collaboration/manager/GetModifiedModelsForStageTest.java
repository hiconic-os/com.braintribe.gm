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
package com.braintribe.model.access.smood.collaboration.manager;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import com.braintribe.model.access.smood.collaboration.basic.AbstractCollaborativePersistenceTest;
import com.braintribe.model.cortexapi.access.collaboration.GetModifiedModelsForStage;
import com.braintribe.model.meta.GmEntityType;
import com.braintribe.model.meta.GmMetaModel;

/**
 * @author peter.gazdik
 */
public class GetModifiedModelsForStageTest extends AbstractCollaborativePersistenceTest {

	private static final String[] NO_MODELS = new String[0];

	/** Smoke test for {@link GetModifiedModelsForStage}. */
	@Test
	public void emptyDb() {
		assertModels(trunkStageName, NO_MODELS);
	}

	/** We find a model we have created. */
	@Test
	public void createdModel() {
		createModel("NewModel");
		session.commit();

		assertModels(trunkStageName, "NewModel");
	}

	/** We find a model we have influenced indirectly - who's element we have touched. */
	@Test
	public void touchModelElement() {
		GmMetaModel trunkModel = createModel("TrunkModel");
		GmEntityType gmEntityType = session.create(GmEntityType.T);
		gmEntityType.setDeclaringModel(trunkModel);
		session.commit();

		pushNewStage("stage2");

		gmEntityType.setTypeSignature("DummyEntity");
		session.commit();

		assertModels("stage2", "TrunkModel");
	}

	/** We if we only reference a model with a dependency, it is not considered as being modified. */
	@Test
	public void noTouchIfOnlyReferenced() {
		GmMetaModel trunkModel = createModel("TrunkModel");
		GmEntityType gmEntityType = session.create(GmEntityType.T);
		gmEntityType.setDeclaringModel(trunkModel);
		session.commit();

		pushNewStage("stage2");

		GmMetaModel stage2Model = createModel("Stage2Model");
		stage2Model.getDependencies().add(trunkModel);
		session.commit();

		// TrunkModel only referenced
		assertModels("stage2", "Stage2Model");
	}

	@Test
	public void correctAfterMerge() {
		pushNewStage("stage2");

		createModel("Stage2Model");
		session.commit();

		// First there is no model, after merge there is a model in trunk

		assertModels(trunkStageName, NO_MODELS);

		mergeStage("stage2", trunkStageName);

		assertModels(trunkStageName, "Stage2Model");
	}

	// #################################################
	// ## . . . . . . . . . Helpers . . . . . . . . . ##
	// #################################################

	private GmMetaModel createModel(String name) {
		GmMetaModel newModel = session.create(GmMetaModel.T);
		newModel.setName(name);

		return newModel;
	}

	private void assertModels(String stageName, String... expectedModelNames) {
		List<GmMetaModel> models = getModifiedModelsForStage(stageName);

		assertThat(extractNames(models)).containsOnly(expectedModelNames);
	}

	private List<String> extractNames(List<GmMetaModel> models) {
		return models.stream().map(GmMetaModel::getName).collect(Collectors.toList());
	}

	private List<GmMetaModel> getModifiedModelsForStage(String stageName) {
		return eval(getModifiedModelsRequest(stageName));
	}

	private GetModifiedModelsForStage getModifiedModelsRequest(String stageName) {
		GetModifiedModelsForStage result = GetModifiedModelsForStage.T.create();
		result.setName(stageName);

		return result;
	}

}
