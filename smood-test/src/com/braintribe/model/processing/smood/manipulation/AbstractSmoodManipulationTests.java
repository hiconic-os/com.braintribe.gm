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
package com.braintribe.model.processing.smood.manipulation;

import java.util.List;
import java.util.Set;

import com.braintribe.model.access.ModelAccessException;
import com.braintribe.model.accessapi.ManipulationRequest;
import com.braintribe.model.accessapi.ManipulationResponse;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.manipulation.Manipulation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.processing.manipulation.basic.tools.ManipulationTools;
import com.braintribe.model.processing.query.fluent.EntityQueryBuilder;
import com.braintribe.model.processing.smood.population.SmoodIndexTools;
import com.braintribe.model.processing.smood.test.AbstractSmoodTests;
import com.braintribe.model.processing.test.tools.meta.ManipulationDriver;
import com.braintribe.model.processing.test.tools.meta.ManipulationDriver.NotifyingSessionRunnable;
import com.braintribe.model.processing.test.tools.meta.ManipulationTrackingMode;
import com.braintribe.model.query.EntityQuery;
import com.braintribe.model.query.EntityQueryResult;
import com.braintribe.utils.junit.assertions.BtAssertions;

public class AbstractSmoodManipulationTests extends AbstractSmoodTests {

	protected final ManipulationDriver manipulationRecorder = new ManipulationDriver();
	protected boolean generateId = true;

	protected ManipulationTrackingMode defaultManipulationMode = ManipulationTrackingMode.LOCAL;

	protected Manipulation trackedManipulation;
	protected ManipulationTrackingMode trackedMode;
	protected ManipulationResponse response;

	protected void applyManipulations(NotifyingSessionRunnable r) {
		applyManipulations(defaultManipulationMode, r);
	}

	protected void applyManipulations(ManipulationTrackingMode mm, NotifyingSessionRunnable r) {
		trackManipulations(mm, r);
		applyTrackedManipulations();
	}

	protected void trackManipulations(ManipulationTrackingMode mm, NotifyingSessionRunnable r) {
		manipulationRecorder.setTrackingMode(mm);
		trackedMode = mm;
		trackedManipulation = manipulationRecorder.track(r);
	}

	private void applyTrackedManipulations() {
		ManipulationRequest mr = asRequest(trackedManipulation);

		try {
			response = smood.apply() //
					.generateId(generateId) //
					.manipulationMode(trackedMode.toManipulationMode()) //
					.manifestUnkownEntities(manifestUnknownEntities()) //
					.request(mr) //
					.getManipulationResponse();

		} catch (ModelAccessException e) {
			throw new RuntimeException("Something went wrong!", e);
		}
	}

	protected boolean manifestUnknownEntities() {
		return false;
	}

	protected ManipulationRequest asRequest(Manipulation m) {
		return ManipulationTools.asManipulationRequest(m);
	}

	protected EntityQueryResult executeQuery(EntityQuery eq) {
		return smood.queryEntities(eq);
	}

	protected void assertFindsByProperty(EntityType<?> et, String propName, String propValue) {
		EntityQuery eq = EntityQueryBuilder.from(et).where().property(propName).eq(propValue).done();
		EntityQueryResult result = executeQuery(eq);
		List<GenericEntity> entities = result.getEntities();
		BtAssertions.assertThat(entities).isNotEmpty();
	}

	protected void assertFindsByIndexedProperty(EntityType<?> et, String propName, String propValue) {
		GenericEntity entity = smood.getValueForIndex(SmoodIndexTools.indexId(et.getTypeSignature(), propName), propValue);
		BtAssertions.assertThat(entity).isInstanceOf(et.getJavaType());
	}

	protected void assertEntityCountForType(EntityType<?> et, int count) {
		Set<? extends GenericEntity> entities = smood.getEntitiesPerType(et);

		if (count == 0)
			BtAssertions.assertThat(entities).isNullOrEmpty();
		else
			BtAssertions.assertThat(entities).isNotNull().hasSize(count);
	}

}
