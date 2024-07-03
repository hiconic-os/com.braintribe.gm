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
package com.braintribe.model.access.smood.collaboration.smoke;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.junit.Test;

import com.braintribe.model.access.smood.collaboration.deployment.CsaBuilder;
import com.braintribe.model.access.smood.collaboration.deployment.CsaDeployedUnit;
import com.braintribe.model.access.smood.collaboration.manager.model.CsaTestModel;
import com.braintribe.model.access.smood.collaboration.tools.CsaTestTools;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.resource.Resource;
import com.braintribe.model.smoodstorage.stages.PersistenceStage;

/**
 * @author peter.gazdik
 */
public class CorruptManFileCsaSmokeTest {

	/* Note the man files don't contain line-breaks but spaces, so the marker is valid for both Windows and Linux */
	private static final File PROTOTYPE_FOLDER = new File("res/CorruptManFilesCsaSmokeTest");

	private CsaDeployedUnit csaUnit;

	@Test
	public void trimsManFilesBeforeStartingUp() throws Exception {
		csaUnit = deployCsa();

		PersistenceGmSession session = csaUnit.session;

		GmMetaModel manModelEntity = session.findEntityByGlobalId("test.smoke.SimpleSmokeTestModel");
		checkEntity(manModelEntity, "trunk");

		Resource manDataEntity = session.findEntityByGlobalId("resource.smoke.test");
		checkEntity(manDataEntity, "trunk");
	}

	private CsaDeployedUnit deployCsa() throws Exception {
		File workingFolder = CsaTestTools.createWorkingFolder(PROTOTYPE_FOLDER);

		return CsaBuilder.create() //
				.baseFolder(workingFolder) //
				.cortex(true) //
				.model(CsaTestModel.raw()) //
				.done();
	}

	private void checkEntity(GenericEntity entity, String expectedStageName) {
		assertThat(entity).isNotNull();

		PersistenceStage persitenceStage = csaUnit.csa.findStageForReference(entity.reference());
		assertThat(persitenceStage).isNotNull();
		assertThat(persitenceStage.getName()).isEqualTo(expectedStageName);
	}

}
