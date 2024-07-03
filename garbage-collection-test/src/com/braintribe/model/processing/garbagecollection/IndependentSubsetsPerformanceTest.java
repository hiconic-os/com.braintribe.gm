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
package com.braintribe.model.processing.garbagecollection;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.braintribe.model.access.IncrementalAccess;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.processing.session.impl.persistence.BasicPersistenceGmSession;
import com.braintribe.testing.model.test.technical.features.ComplexEntity;
import com.braintribe.testing.model.test.testtools.TestModelTestTools;
import com.braintribe.utils.MathTools;
import com.braintribe.utils.genericmodel.ReachableEntitiesFinder;
import com.braintribe.utils.lcd.GraphTools;
import com.braintribe.utils.lcd.GraphTools.ReachablesFinder;
import com.braintribe.utils.lcd.StopWatch;

@RunWith(Parameterized.class)
public class IndependentSubsetsPerformanceTest {

	private BasicPersistenceGmSession session = null;
	private int numOfChainNodes = 0;
	private int numOfRayNodes = 0;
	final Set<GenericEntity> nodes = new HashSet<GenericEntity>();

	public IndependentSubsetsPerformanceTest(final int numOfChainNodes, final int numOfRayNodes) {
		this.numOfChainNodes = numOfChainNodes;
		this.numOfRayNodes = numOfRayNodes;
	}

	@Parameters
	public static Collection<Integer[]> addedNumbers() {
		return Arrays.asList(new Integer[][] { { 100, 1 }, { 1, 100 }, { 100, 100 }, { 100, 1000 }, { 1000, 100 },
				// { 1000, 1000 }, { 1000, 10000 }, { 10000, 1000 }, { 10000, 100 }, { 100, 10000 },
		});
	}

	@Before
	public void setUp() {
		this.session = createSession();
		final ComplexEntity rootEntity = this.session.create(ComplexEntity.T);
		ComplexEntity tempEntityRef = rootEntity;
		for (int i = 0; i < this.numOfChainNodes; i++) {
			this.nodes.add(tempEntityRef);
			final ComplexEntity nextChainEntity = this.session.create(ComplexEntity.T);
			tempEntityRef.setComplexEntityProperty(nextChainEntity);
			tempEntityRef = nextChainEntity;
		}

		for (int i = 0; i < this.numOfRayNodes; i++) {
			final ComplexEntity rayEntity = this.session.create(ComplexEntity.T);
			rayEntity.setComplexEntityProperty(rootEntity);
			this.nodes.add(rayEntity);
		}
	}

	@Test
	public void testPerformance() {
		System.out.println("----------------------------------------------------------------------------------------");
		System.out.println("Architecture: Star-Chain");
		System.out.println("Star nodes: " + this.numOfRayNodes);
		System.out.println("Chain nodes: " + this.numOfChainNodes);
		final StopWatch stopWatch = new StopWatch();
		GraphTools.findIndependentSubsets(this.nodes, new ReachableEntitiesFinder());
		final long timeWithStop = stopWatch.getElapsedTime();
		System.out.println(
				"Finding independent subsets with reachable finder that stops in nodes that are already traversed: "
						+ timeWithStop);
		stopWatch.reset();
		GraphTools.findIndependentSubsets(this.nodes,
				new ReachableEntitiesFinderWithoutNodesWhereToStopFurtherTraversing());
		final long timeWithoutStop = stopWatch.getElapsedTime();
		System.out.println(
				"Finding independent subsets with reachable finder that DOES NOT stop in nodes that are already traversed: "
						+ timeWithoutStop);

		System.out.println("Imputation = " + MathTools.roundToDecimals((double) timeWithStop / timeWithoutStop, 3));
	}

	private static BasicPersistenceGmSession createSession() {
		final IncrementalAccess access = TestModelTestTools.newSmoodAccessMemoryOnly("test", TestModelTestTools.createTestModelMetaModel());
		final BasicPersistenceGmSession session = new BasicPersistenceGmSession();
		session.setIncrementalAccess(access);
		return session;
	}

	public class ReachableEntitiesFinderWithoutNodesWhereToStopFurtherTraversing
			implements ReachablesFinder<GenericEntity> {

		@Override
		public Set<GenericEntity> findReachables(final GenericEntity rootNode,
				final Set<GenericEntity> nodesWhereToStopFurtherTraversing) {
			return com.braintribe.utils.genericmodel.GMCoreTools.findReachableEntities(rootNode, null);
		}

	}
}
