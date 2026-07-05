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

import static com.braintribe.model.generic.manipulation.util.ManipulationBuilder.delete;
import static com.braintribe.utils.lcd.CollectionTools2.asSet;

import java.util.Set;

import org.junit.Test;

import com.braintribe.model.access.security.SecurityAspect;
import com.braintribe.model.access.security.manipulation.ValidatorTestBase;
import com.braintribe.model.access.security.testdata.Box;
import com.braintribe.model.access.security.testdata.manipulation.EntityWithPropertyConstraints;
import com.braintribe.model.generic.manipulation.DeleteManipulation;
import com.braintribe.model.generic.manipulation.DeleteMode;
import com.braintribe.model.generic.value.EntityReference;
import com.braintribe.model.processing.security.manipulation.ManipulationSecurityExpert;

/**
 * 
 */
public class UniquePropertyTests extends ValidatorTestBase {

	@Override
	protected Set<? extends ManipulationSecurityExpert> manipulationSecurityExperts() {
		return asSet(new UniqueKeyPropertyExpert());
	}

	/**
	 * AFAIK having multiple nulls for unique property is not a problem in SQL (so I think it holds for Smood as well)
	 */
	@Test
	public void notSettingPropertyIsOk() throws Exception {
		validate(() -> {
			session.create(EntityWithPropertyConstraints.T);
			session.create(EntityWithPropertyConstraints.T);
		});

		assertOk();
	}

	@Test
	public void settingDifferentValuesIsOk() throws Exception {
		validate(() -> {
			EntityWithPropertyConstraints e1 = session.create(EntityWithPropertyConstraints.T);
			e1.setUnique("value1");

			EntityWithPropertyConstraints e2 = session.create(EntityWithPropertyConstraints.T);
			e2.setUnique("value2");
		});

		assertOk();
	}

	@Test
	public void settingSameValueTwiceIsAProblem() throws Exception {
		validate(() -> {
			EntityWithPropertyConstraints e1 = session.create(EntityWithPropertyConstraints.T);
			e1.setUnique("value");

			EntityWithPropertyConstraints e2 = session.create(EntityWithPropertyConstraints.T);
			e2.setUnique("value");
		});

		assertDoubleError();
	}

	@Test
	public void settingSameEntityValueTwiceIsAProblem() throws Exception {
		EntityWithPropertyConstraints e0 = session.create(EntityWithPropertyConstraints.T);
		commit();

		validate(() -> {
			EntityWithPropertyConstraints e1 = session.create(EntityWithPropertyConstraints.T);
			e1.setUniqueEntity(e0);

			EntityWithPropertyConstraints e2 = session.create(EntityWithPropertyConstraints.T);
			e2.setUniqueEntity(e0);
		});

		assertDoubleEntityError();
	}

	@Test
	public void settingValueTwiceAndThenChangingOneOfEntitiesIsOk() throws Exception {
		validate(() -> {
			EntityWithPropertyConstraints e1 = session.create(EntityWithPropertyConstraints.T);
			e1.setUnique("value");

			EntityWithPropertyConstraints e2 = session.create(EntityWithPropertyConstraints.T);
			e2.setUnique("value");

			e1.setUnique("somethingElse");
		});

		assertOk();
	}

	/** There was a bug that this did not work due to {@link SecurityAspect}s wrong handling of changing id property. */
	@Test
	public void settingValueTwiceAndThenChangingOneOfEntitiesIsOk_WhenChangingId() throws Exception {
		validate(() -> {
			EntityWithPropertyConstraints e1 = session.create(EntityWithPropertyConstraints.T);
			e1.setUnique("value");

			EntityWithPropertyConstraints e2 = session.create(EntityWithPropertyConstraints.T);
			e2.setUnique("value");

			e1.setId(1);
			e1.setUnique("somethingElse");
		});

		assertOk();
	}

	// ##########################################
	// ## . . TESTS WHICH REQUIRE QUERYING . . ##
	// ##########################################

	/* These tests are quite ugly, too bad we don't have closures in java. */

	@Test
	public void settingValueToSomethingIsOk() throws Exception {
		EntityWithPropertyConstraints e1 = session.create(EntityWithPropertyConstraints.T);
		commit();

		record(() -> e1.setUnique("newValue"));

		e1.setUnique("value");
		commit();

		validate();
		assertOk();
	}

	@Test
	public void settingPropertyToSameValueIsOk() throws Exception {
		EntityWithPropertyConstraints e1 = session.create(EntityWithPropertyConstraints.T);
		commit();

		record(() -> e1.setUnique("value"));

		e1.setUnique("value");
		commit();

		validate();
		assertOk();
	}

	@Test
	public void settingUsedValueIsAProblem() throws Exception {
		EntityWithPropertyConstraints e1 = session.create(EntityWithPropertyConstraints.T);
		e1.setUnique("value");
		commit();

		validate(() -> {
			EntityWithPropertyConstraints e2 = session.create(EntityWithPropertyConstraints.T);
			e2.setUnique("value");
		});

		assertSingleError();
	}

	@Test
	public void settingUsedEntityValueIsAProblem() throws Exception {
		EntityWithPropertyConstraints e1 = session.create(EntityWithPropertyConstraints.T);
		e1.setUniqueEntity(e1);
		commit();

		validate(() -> {
			EntityWithPropertyConstraints e2 = session.create(EntityWithPropertyConstraints.T);
			e2.setUniqueEntity(e1);
		});

		assertSingleEntityError();
	}

	@Test
	public void settingUsedValueIsNoProblemIfWeChangeTheExistingAsWell() throws Exception {
		EntityWithPropertyConstraints e1 = session.create(EntityWithPropertyConstraints.T);
		commit();
		Box<EntityWithPropertyConstraints> e2 = Box.newBox();

		record(() -> {
			e2.value = session.create(EntityWithPropertyConstraints.T);
			e2.value.setUnique("value");

			e1.setUnique("something else");
		});

		validate();
		assertOk();
	}

	@Test
	public void settingUsedValueIsNoProblemIfWeSetExistingToNull() throws Exception {
		EntityWithPropertyConstraints e1 = session.create(EntityWithPropertyConstraints.T);
		commit();
		Box<EntityWithPropertyConstraints> e2 = Box.newBox();

		record(() -> {
			e2.value = session.create(EntityWithPropertyConstraints.T);
			e2.value.setUnique("value");

			e1.setUnique(null);
		});

		validate();
		assertOk();
	}

	@Test
	public void settingUsedValueIsOkIfWeDeleteOriginalEntity() throws Exception {
		EntityWithPropertyConstraints e1 = session.create(EntityWithPropertyConstraints.T);
		e1.setUnique("value");
		commit();

		record(() -> {
			EntityWithPropertyConstraints e2 = session.create(EntityWithPropertyConstraints.T);
			e2.setUnique("value");
		});

		/* I cannot simply record something like session.deleteEntity(e1), since that would also delete the entity right
		 * away and the test would not be testing what I want. */
		appendManipulation(deleteManipulationFor(e1));

		validate();
		assertOk();
	}

	private DeleteManipulation deleteManipulationFor(EntityWithPropertyConstraints e1) {
		EntityReference entityReference = e1.reference();
		return delete(entityReference, DeleteMode.ignoreReferences);
	}

	private void assertSingleError() {
		assertNumberOfErrors(1);
		assertErrors(EntityWithPropertyConstraints.T, "unique");
	}

	private void assertDoubleError() {
		assertNumberOfErrors(2);
		assertErrors(EntityWithPropertyConstraints.T, "unique");
	}

	private void assertSingleEntityError() {
		assertNumberOfErrors(1);
		assertErrors(EntityWithPropertyConstraints.T, "uniqueEntity");
	}

	private void assertDoubleEntityError() {
		assertNumberOfErrors(2);
		assertErrors(EntityWithPropertyConstraints.T, "uniqueEntity");
	}

}
