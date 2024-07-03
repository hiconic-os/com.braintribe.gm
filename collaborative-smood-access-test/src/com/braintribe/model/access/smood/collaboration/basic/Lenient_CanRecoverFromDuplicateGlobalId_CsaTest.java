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
import static com.braintribe.utils.lcd.CollectionTools2.first;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import com.braintribe.model.access.smood.collaboration.manager.model.StagedEntity;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.processing.manipulation.parser.api.GmmlManipulatorErrorHandler;
import com.braintribe.model.processing.manipulation.parser.impl.listener.error.LenientErrorHandler;
import com.braintribe.model.processing.query.fluent.EntityQueryBuilder;
import com.braintribe.model.processing.session.api.collaboration.CollaborativeAccess;
import com.braintribe.model.processing.session.api.collaboration.PersistenceInitializationContext;
import com.braintribe.model.processing.session.api.collaboration.PersistenceInitializer;
import com.braintribe.model.processing.session.api.collaboration.SimplePersistenceInitializer;
import com.braintribe.model.processing.session.api.managed.ManagedGmSession;

/**
 * 
 * @see CollaborativeAccess
 * 
 * @author peter.gazdik
 */
public class Lenient_CanRecoverFromDuplicateGlobalId_CsaTest extends AbstractCollaborativePersistenceTest {

	private static final String PRE_INIT_STAGE = "pre-initialization";
	private static final String DUPLICATED_GID = "duplicated-gid";
	private List<PersistenceInitializer> preInitializers = emptyList();

	/** Here we test that we can actually read the configuration written with our setup. */
	@Test
	public void singleStageInitialization_AndRedeployment() {
		createEntity(session);
		session.commit();

		preInitializers = asList(SimplePersistenceInitializer.create(PRE_INIT_STAGE, this::createEntityInInitializer));

		redeploy();

		List<GenericEntity> allEntities = session.query().entities(EntityQueryBuilder.from(GenericEntity.T).done()).list();
		assertThat(allEntities).hasSize(2);

		Set<String> gids = allEntities.stream().map(GenericEntity::getGlobalId).collect(Collectors.toSet());
		assertThat(gids.remove(DUPLICATED_GID)).isTrue();

		String copyGid = first(gids);
		assertThat(copyGid).startsWith("gmml://").contains("trunk").contains(DUPLICATED_GID);
	}

	private void createEntityInInitializer(PersistenceInitializationContext ctx) {
		createEntity(ctx.getSession());
	}

	private void createEntity(ManagedGmSession session) {
		StagedEntity entity = session.create(StagedEntity.T);
		entity.setGlobalId(DUPLICATED_GID);
	}

	@Override
	protected GmmlManipulatorErrorHandler errorHandler() {
		return LenientErrorHandler.INSTANCE;
	}

	@Override
	protected List<PersistenceInitializer> preInitializers() {
		return preInitializers;
	}

}
