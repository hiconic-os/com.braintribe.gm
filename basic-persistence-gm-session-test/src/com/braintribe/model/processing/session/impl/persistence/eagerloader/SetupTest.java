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
package com.braintribe.model.processing.session.impl.persistence.eagerloader;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.VdHolder;
import com.braintribe.model.processing.session.impl.persistence.eagerloader.model.EagerOwner;

/**
 * Simple sanity check that our test and util methods used here are setup correctly OK.
 * 
 * @see AbstractEagerLoaderTest
 * 
 * @author peter.gazdik
 */
public class SetupTest extends AbstractEagerLoaderTest {

	@Test
	public void checkTestSetup() throws Exception {
		prepareEmptyOwners();
		loadOwners();

		for (Property property : EagerOwner.T.getProperties())
			if (!property.isIdentifier() && !property.getType().isScalar())
				assertThat(countAbsentValues(property)).as("Wrong number for property: " + property.getName()).isEqualTo(NUMBER_OF_OWNERS);

	}

	private void prepareEmptyOwners() {
		for (int i = 0; i < NUMBER_OF_OWNERS; i++)
			newOwner(i);
	}

	protected long countAbsentValues(Property p) {
		return owners.stream() //
				.map(p::getDirectUnsafe) //
				.filter(VdHolder::isAbsenceInfo) // take those which are not absent
				.count();
	}

	private void newOwner(int i) {
		String name = getOwnerName(i);

		EagerOwner owner = EagerOwner.T.create();
		owner.setGlobalId(name);
		owner.setId(i);
		owner.setName(name);

		smood.registerEntity(owner, false);
	}

	private String getOwnerName(int i) {
		// make sure each number has two digits
		String number = i < 10 ? "0" + i : "" + i;
		return "owner_" + number;
	}

}
