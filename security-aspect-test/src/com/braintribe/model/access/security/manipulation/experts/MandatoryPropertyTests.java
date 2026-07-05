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

import com.braintribe.model.access.security.SecurityAspect;
import com.braintribe.model.access.security.manipulation.ValidatorTestBase;
import com.braintribe.model.access.security.testdata.manipulation.EntityWithPropertyConstraints;
import com.braintribe.model.processing.security.manipulation.ManipulationSecurityExpert;

/**
 * 
 */
public class MandatoryPropertyTests extends ValidatorTestBase {

	@Override
	protected Set<? extends ManipulationSecurityExpert> manipulationSecurityExperts() {
		return asSet(new MandatoryPropertyExpert());
	}

	@Test
	public void settingPropertyIsOk() throws Exception {
		validate(() -> {
			EntityWithPropertyConstraints entity = session.create(EntityWithPropertyConstraints.T);
			entity.setMandatory("value");
			entity.setNonModifiableButMandatory("value");
		});

		assertOk();
	}

	/** There was a bug that this did not work due to {@link SecurityAspect}s wrong handling of changing id property. */
	@Test
	public void settingPropertyIsOk_WhenChangingId() throws Exception {
		validate(() -> {
			EntityWithPropertyConstraints entity = session.create(EntityWithPropertyConstraints.T);
			entity.setId(10);
			entity.setMandatory("value");
			entity.setNonModifiableButMandatory("value");
		});

		assertOk();
	}

	@Test
	public void notSettingPropertyCausesFailure() throws Exception {
		validate(() -> session.create(EntityWithPropertyConstraints.T));

		assertNumberOfErrors(2);
	}

	@Test
	public void settingAndUnsettingPropertyCausesFailure() throws Exception {
		validate(() -> {
			EntityWithPropertyConstraints entity = session.create(EntityWithPropertyConstraints.T);
			entity.setNonModifiableButMandatory("value");
			entity.setMandatory("value");
			entity.setMandatory(null);
		});

		assertSingleError();
	}

	@Test
	public void unsettingPropertyCausesFailure() throws Exception {
		EntityWithPropertyConstraints entity = session.create(EntityWithPropertyConstraints.T);
		entity.setNonModifiableButMandatory("value");
		entity.setMandatory("value");

		validate(() -> entity.setMandatory(null));

		assertSingleError();
	}

	private void assertSingleError() {
		assertNumberOfErrors(1);
		assertErrors(EntityWithPropertyConstraints.T, "mandatory");
	}

}
