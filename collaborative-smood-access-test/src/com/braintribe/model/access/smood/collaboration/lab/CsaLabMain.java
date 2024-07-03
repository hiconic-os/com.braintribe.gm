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
package com.braintribe.model.access.smood.collaboration.lab;

import java.io.File;

import com.braintribe.model.access.smood.collaboration.deployment.CsaBuilder;
import com.braintribe.model.access.smood.collaboration.deployment.CsaDeployedUnit;
import com.braintribe.model.access.smood.collaboration.tools.CsaTestTools;
import com.braintribe.model.cortexapi.access.collaboration.MergeCollaborativeStage;
import com.braintribe.model.csa.CollaborativeSmoodConfiguration;
import com.braintribe.model.generic.GMF;

/**
 * @author peter.gazdik
 */
public class CsaLabMain {

	static final File LAB_FILE = new File("res/Lab");

	private final CsaDeployedUnit csaUnit;

	public static void main(String[] args) throws Exception {
		new CsaLabMain().run();

		System.out.println("CSA Lab done!");
	}

	public CsaLabMain() throws Exception {
		File workingFolder = CsaTestTools.createWorkingFolder(LAB_FILE);

		csaUnit = CsaBuilder.create() //
				.baseFolder(workingFolder) //
				.cortex(true) //
				.configurationSupplier(this::prepareNewConfiguration) //
				.model(GMF.getTypeReflection().getModel("tribefire.cortex:platform-setup-workbench-model").getMetaModel()) //
				.done();
	}

	private void run() throws Exception {
		mergeStageToPredecessor("trunk", "pre-trunk");
	}

	private CollaborativeSmoodConfiguration prepareNewConfiguration() {
		throw new IllegalStateException("config.json not found");
	}

	private void mergeStageToPredecessor(String source, String target) {
		csaUnit.eval(mergeStageToPredecessorRequest(source, target));
	}

	private MergeCollaborativeStage mergeStageToPredecessorRequest(String source, String target) {
		MergeCollaborativeStage result = MergeCollaborativeStage.T.create();
		result.setSource(source);
		result.setTarget(target);

		return result;
	}

}
