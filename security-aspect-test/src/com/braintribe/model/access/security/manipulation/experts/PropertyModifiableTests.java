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
package com.braintribe.model.access.security.manipulation.experts;

import static com.braintribe.utils.lcd.CollectionTools2.asSet;

import java.util.Set;

import org.junit.Test;

import com.braintribe.model.access.security.manipulation.ValidatorTestBase;
import com.braintribe.model.access.security.testdata.manipulation.EntityWithPropertyConstraints;
import com.braintribe.model.processing.security.manipulation.ManipulationSecurityExpert;

/**
 * 
 */
public class PropertyModifiableTests extends ValidatorTestBase {

	@Override
	protected Set<? extends ManipulationSecurityExpert> manipulationSecurityExperts() {
		return asSet(new PropertyModifiableExpert());
	}

	@Test
	public void noContraint_EditOk() throws Exception {
		validate(() -> {
			EntityWithPropertyConstraints entity = session.create(EntityWithPropertyConstraints.T);
			entity.setUnique("value");
		});

		assertOk();
	}

	@Test
	public void nonModifiable_Error() throws Exception {
		validate(() -> {
			EntityWithPropertyConstraints entity = session.create(EntityWithPropertyConstraints.T);
			entity.setNonModifiable("value");
		});

		assertSingleError("nonModifiable");
	}

	@Test
	public void nonModifiableButMandatory_Instantiation_Ok() throws Exception {
		validate(() -> {
			EntityWithPropertyConstraints entity = session.create(EntityWithPropertyConstraints.T);
			entity.setNonModifiableButMandatory("value");
		});
		
		assertOk();
	}
	
	@Test
	public void nonModifiableButMandatory_Persistent_Ok() throws Exception {
		EntityWithPropertyConstraints entity = session.create(EntityWithPropertyConstraints.T);
		commit();

		validate(() -> {
			entity.setNonModifiableButMandatory("value");
		});
		
		assertSingleError("nonModifiableButMandatory");
	}

	// @Test
	public void KNOWN_ISSUE_nonModifiableButMandatory_InstantiationAfterExplicitIdSet_Ok() throws Exception {
		validate(() -> {
			EntityWithPropertyConstraints entity = session.create(EntityWithPropertyConstraints.T);
			entity.setId(1L);
			entity.setNonModifiableButMandatory("value");
		});
		
		assertOk();
	}
	
	private void assertSingleError(String propertyName) {
		assertNumberOfErrors(1);
		assertErrors(EntityWithPropertyConstraints.T, propertyName);
	}

}
