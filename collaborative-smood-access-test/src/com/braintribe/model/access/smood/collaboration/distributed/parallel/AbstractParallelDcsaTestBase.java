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
package com.braintribe.model.access.smood.collaboration.distributed.parallel;

import static com.braintribe.utils.lcd.CollectionTools2.first;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.junit.After;

import com.braintribe.exception.Exceptions;
import com.braintribe.model.access.smood.collaboration.deployment.DcsaDeployedUnit;
import com.braintribe.model.access.smood.collaboration.distributed.AbstractDcsaTestBase;
import com.braintribe.model.access.smood.collaboration.distributed.model.DcsaEntity;
import com.braintribe.model.processing.query.fluent.EntityQueryBuilder;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.query.EntityQuery;

/**
 * @author peter.gazdik
 */
public class AbstractParallelDcsaTestBase extends AbstractDcsaTestBase {

	protected static final int CLUSTER_SIZE = 4;
	protected static final int THREAD_COUNT = 5 * CLUSTER_SIZE;

	protected static final int ENTITY_COUNT = 40;

	protected final DcsaCluster dcsaCluster = new DcsaCluster();

	@After
	public void cleanup() {
		dcsaCluster.cleanupCluster();
	}

	protected Map<String, DcsaEntity> getAllEntitiesByName(PersistenceGmSession session) {
		List<DcsaEntity> list = session.query().entities(EntityQueryBuilder.from(DcsaEntity.T).done()).list();

		return list.stream().collect(Collectors.toMap(DcsaEntity::getName, e -> e));
	}

	protected DcsaEntity findEntityByName(PersistenceGmSession session, String name) {
		EntityQuery query = EntityQueryBuilder.from(DcsaEntity.T) //
				.where() //
				.property("name").eq(name).done();

		List<DcsaEntity> list = session.query().entities(query).list();

		if (list.size() != 1)
			throw new IllegalStateException("Exactly one entity was expected for name '" + name + "'. Actual result: " + list);

		return first(list);
	}

	protected void forEachEntity_InParallel(Consumer<Integer> entityTask) {
		forRange_InParallel(ENTITY_COUNT, entityTask);
	}

	protected void forEachNode_InParallel(Consumer<DcsaDeployedUnit> entityTask) {
		forRange_InParallel(CLUSTER_SIZE, i -> {
			entityTask.accept(dcsaCluster.unit(i));
		});
	}

	private void forRange_InParallel(int max, Consumer<Integer> indexTask) {
		// IntStream.rangeClosed(0, max).parallel().forEach(indexTask::accept);

		ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);

		for (int i = 0; i < max; i++) {
			int entityNumber = i;
			executorService.execute(() -> indexTask.accept(entityNumber));
		}

		executorService.shutdown();

		try {
			executorService.awaitTermination(1, TimeUnit.MINUTES);

		} catch (InterruptedException e) {
			throw Exceptions.unchecked(e, "Interrupted exception");
		}
	}

	public class DcsaCluster {
		private final DcsaDeployedUnit[] dcsaUnits = new DcsaDeployedUnit[CLUSTER_SIZE];
		private final Random r = new Random(95456L);

		public DcsaCluster() {
			for (int i = 0; i < CLUSTER_SIZE; i++)
				dcsaUnits[i] = deployDcsa("access.dcsa", i);
		}

		public void cleanupCluster() {
			for (int i = 0; i < CLUSTER_SIZE; i++)
				cleanup(dcsaUnits[i]);
		}

		public PersistenceGmSession randomSession() {
			return randomUnit().newSession();

		}

		public DcsaDeployedUnit randomUnit() {
			return dcsaUnits[r.nextInt(CLUSTER_SIZE)];
		}

		public DcsaDeployedUnit unit(int index) {
			return dcsaUnits[index];
		}

	}

}
