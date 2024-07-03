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
package com.braintribe.model.processing.manipulation.lab;

import static com.braintribe.model.generic.manipulation.util.ManipulationBuilder.compound;

import java.io.File;
import java.util.List;

import com.braintribe.model.access.smood.bms.BinaryManipulationStorage;
import com.braintribe.model.generic.manipulation.AtomicManipulation;
import com.braintribe.model.generic.manipulation.CompoundManipulation;
import com.braintribe.model.generic.manipulation.Manipulation;

/**
 * @author peter.gazdik
 */
public class NormalizationLab {

	public static void main(String[] args) throws Exception {
		new NormalizationLab().run();

		System.out.println("***************");
		System.out.println("DONE");
	}

	// ################################################
	// ## . . . . . . Lab Initialization . . . . . . ##
	// ################################################

	protected Manipulation manipulation;

	public NormalizationLab() throws Exception {
		manipulation = loadManipulation();
	}

	private Manipulation loadManipulation() throws Exception {
		File bufferFile = new File("resource/current.buffer");

		BinaryManipulationStorage storage = new BinaryManipulationStorage();
		storage.setStorageFile(bufferFile);

		return storage.getAccumulatedManipulation();
	}

	// ################################################
	// ## . . . . . . Lab Implementation . . . . . . ##
	// ################################################

	private void run() {
		List<AtomicManipulation> manipulations = manipulation.inline();
		manipulations = ManipulationFilter.filterByOwnerType(manipulations, "com.braintribe.model.resourcerepository.Content");
		CompoundManipulation cm = compound(manipulations);

		print(cm);
	}

	static void print(Manipulation manipulation) {
		System.out.println(manipulation.stringify());
	}

}
