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
package com.braintribe.model.generic.tracking;

import static com.braintribe.utils.lcd.CollectionTools2.newList;

import java.util.List;

import com.braintribe.model.generic.manipulation.Manipulation;
import com.braintribe.model.generic.manipulation.ManipulationType;

// Temporary, just to be able to commit and avoid cyclic dependencies problems
public class SimpleManipulationCollector implements ManipulationCollector {

	private final List<Manipulation> manipulations = newList();
	private boolean collectCompoundManipulations = true;

	public SimpleManipulationCollector setCollectCompoundManipulations(boolean collectCompoundManipulations) {
		this.collectCompoundManipulations = collectCompoundManipulations;
		return this;
	}

	@Override
	public List<Manipulation> getManipulations() {
		return manipulations;
	}

	@Override
	public void noticeManipulation(Manipulation manipulation) {
		if (collectCompoundManipulations || manipulation.manipulationType() != ManipulationType.COMPOUND)
			manipulations.add(manipulation);
	}
}
