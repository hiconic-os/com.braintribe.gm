// ============================================================================
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
// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
// ============================================================================
package com.braintribe.model.processing.traversing.engine.impl.clone;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

import com.braintribe.model.generic.manipulation.Manipulation;
import com.braintribe.model.generic.manipulation.ManipulationType;
import com.braintribe.model.processing.traversing.engine.GMT;
import com.braintribe.model.processing.traversing.engine.impl.misc.model.ListOwner;
import com.braintribe.testing.tools.gm.meta.ManipulationRecorder;

public class CollectionCloningTest {

	@Test
	public void collectionManipulationsAreTracked() throws Exception {
		ManipulationRecorder manipulationRecorder = new ManipulationRecorder();

		ListOwner listOwner = ListOwner.T.create();
		listOwner.setList(Arrays.asList(ListOwner.T.create()));

		manipulationRecorder.record(session -> {
			BasicClonerCustomization clonerCustomization = new BasicClonerCustomization();
			clonerCustomization.setSession(session);

			Cloner cloner = new Cloner();
			GMT.doClone().customize(clonerCustomization).visitor(cloner).doFor(listOwner);

			assertContainsAddManipulation(session.getTransaction().getManipulationsDone());
		});
	}

	private void assertContainsAddManipulation(List<Manipulation> manipulations) {
		Optional<?> add = manipulations.stream().filter(m -> m.manipulationType() == ManipulationType.ADD).findFirst();
		assertThat(add.isPresent()).as("Add manipulation not tracked.").isTrue();
	}

}
