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
package com.braintribe.model.access.smood.collaboration.basic;

import static com.braintribe.utils.lcd.CollectionTools2.asList;

import java.util.List;

import org.junit.Test;

import com.braintribe.model.access.smood.collaboration.manager.model.StagedEntity;
import com.braintribe.model.processing.session.api.collaboration.AbstractPersistenceInitializer;
import com.braintribe.model.processing.session.api.collaboration.CollaborativeAccess;
import com.braintribe.model.processing.session.api.collaboration.ManipulationPersistenceException;
import com.braintribe.model.processing.session.api.collaboration.PersistenceInitializationContext;
import com.braintribe.model.processing.session.api.collaboration.PersistenceInitializer;
import com.braintribe.model.smoodstorage.stages.PersistenceStage;
import com.braintribe.model.smoodstorage.stages.StaticStage;

/**
 * @see CollaborativeAccess
 * 
 * @author peter.gazdik
 */
public class PostInitializersCsaTest extends AbstractCollaborativePersistenceTest {

	private static final String SIMPLE_POST_STAGE_NAME = "simplePostStage";
	private static final String SIMPLE_POST_STAGED_ENTITY_ID = "post.staged";

	@Override
	protected List<PersistenceInitializer> postInitializers() {
		return asList(simpleInitializer());
	}

	private PersistenceInitializer simpleInitializer() {
		PersistenceStage postStage = StaticStage.T.create();
		postStage.setName(SIMPLE_POST_STAGE_NAME);

		return new AbstractPersistenceInitializer() {
			@Override
			public void initializeData(PersistenceInitializationContext context) throws ManipulationPersistenceException {
				context.getSession().create(StagedEntity.T, SIMPLE_POST_STAGED_ENTITY_ID);
			}

			@Override
			public PersistenceStage getPersistenceStage() {
				return postStage;
			}
		};
	}

	@Test
	public void entityFromPostInitializerHasCorrectStage() {
		StagedEntity entity = session.findEntityByGlobalId(SIMPLE_POST_STAGED_ENTITY_ID);
		assertEntityStage(entity, SIMPLE_POST_STAGE_NAME);
	}

	/** There was a bug where the new entity would have the stage of the last post initializer, rather than "trunk". */
	@Test
	public void newEntityHasCorrectStage() {
		StagedEntity entity = session.create(StagedEntity.T);
		session.commit();

		assertEntityStage(entity, "trunk");
	}

}
