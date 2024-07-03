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
package com.braintribe.model.access.smood.collaboration.distributed.basic;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.braintribe.model.access.smood.collaboration.deployment.DcsaDeployedUnit;
import com.braintribe.model.access.smood.collaboration.distributed.AbstractDcsaTestBase;
import com.braintribe.model.access.smood.collaboration.distributed.model.DcsaEntity;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.testing.junit.assertions.assertj.core.api.Assertions;

/**
 * @author peter.gazdik
 */
public class Dcsa_Smoke_Test extends AbstractDcsaTestBase {

	private DcsaDeployedUnit dcsaUnit;
	private PersistenceGmSession session;

	@Before
	public void setup() {
		dcsaUnit = deployDcsa("access.dcsa", 1);
		session = dcsaUnit.session;
	}

	@After
	public void cleanup() {
		cleanup(dcsaUnit);
	}

	@Test
	public void createEntity() throws Exception {
		DcsaEntity dcsaEntity = session.create(DcsaEntity.T);
		dcsaEntity.setName("DCSA");
		session.commit();

		String markerPersistence = dcsaUnit.markerPersistence.get();
		Assertions.assertThat(markerPersistence).isNotNull();
		
		DcsaEntity dcsaEntity2 = session.findEntityByGlobalId(dcsaEntity.getGlobalId());
		Assertions.assertThat(dcsaEntity2).isSameAs(dcsaEntity);
		
		redeploy();

		DcsaEntity dcsaEntity3 = session.findEntityByGlobalId(dcsaEntity.getGlobalId());
		Assertions.assertThat(dcsaEntity3).isNotNull();

	}

	private void redeploy() {
		dcsaUnit = redeploy(dcsaUnit);
		session = dcsaUnit.session;
	}

}
