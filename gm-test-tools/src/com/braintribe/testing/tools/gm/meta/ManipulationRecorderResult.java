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
package com.braintribe.testing.tools.gm.meta;

import static com.braintribe.model.processing.manipulation.basic.tools.ManipulationTools.asManipulationRequest;
import static com.braintribe.utils.lcd.CollectionTools2.newList;

import java.util.List;

import com.braintribe.model.accessapi.ManipulationRequest;
import com.braintribe.model.generic.manipulation.AtomicManipulation;
import com.braintribe.model.generic.manipulation.Manipulation;

/**
 * @author peter.gazdik
 */
public class ManipulationRecorderResult {

	private final Manipulation manipulation;

	public ManipulationRecorderResult(Manipulation manipulation) {
		this.manipulation = manipulation;
	}

	public Manipulation asManipulation() {
		return manipulation;
	}

	public List<AtomicManipulation> asAtomicManipulations() {
		return manipulation == null ? newList() /* MUST BE MUTABLE!!! */ : manipulation.inline();
	}

	public ManipulationRequest asRequest() {
		return asManipulationRequest(manipulation);
	}

}
