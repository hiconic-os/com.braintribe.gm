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

import static com.braintribe.testing.junit.assertions.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.braintribe.gwt.utils.genericmodel.EntityTypeBasedEntitiesFinder;
import com.braintribe.model.access.IncrementalAccess;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.criteria.TraversingCriterion;
import com.braintribe.model.generic.processing.pr.fluent.TC;
import com.braintribe.model.generic.session.exception.GmSessionException;
import com.braintribe.model.generic.typecondition.TypeConditions;
import com.braintribe.model.generic.typecondition.basic.TypeKind;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.query.EntityQuery;
import com.braintribe.testing.model.test.technical.features.ComplexEntity;
import com.braintribe.testing.model.test.technical.features.SimpleEntity;
import com.braintribe.testing.model.test.testtools.TestModelTestTools;
import com.braintribe.utils.genericmodel.GMCoreTools;

/**
 * This class tests the Garbage Collection implemented in the method
 * {@link InMemoryGarbageCollection#performGarbageCollection(PersistenceGmSession, List, boolean)} passing different TCs
 * to the EntityFinders needed in GC configuration. Purpose of this test is/was to reproduce a bug.
 *
 *
 */
public class InMemoryGarbageCollectionTcTest {

	private final Set<GenericEntity> expectedExistingEntities = new HashSet<GenericEntity>();
	private final Set<GenericEntity> expectedRemovedEntities = new HashSet<GenericEntity>();

	private IncrementalAccess access;

	@Before
	public void setUp() throws GmSessionException {
		this.access = TestModelTestTools.newSmoodAccessMemoryOnly("test", TestModelTestTools.createTestModelMetaModel());
		final PersistenceGmSession session = TestModelTestTools.newSession(this.access);

		final ComplexEntity c1 = session.create(ComplexEntity.T);
		this.expectedExistingEntities.add(c1);

		final SimpleEntity s1 = session.create(SimpleEntity.T);
		c1.setSimpleEntityProperty(s1);
		s1.setStringProperty("Should not be deleted from GC");
		this.expectedExistingEntities.add(s1);

		final SimpleEntity s2 = session.create(SimpleEntity.T);
		s2.setStringProperty("Should be deleted from GC");
		this.expectedRemovedEntities.add(s2);

		session.commit();
	}

	@Test
	public void testWithoutAbsenceInformation() throws GmSessionException {
		final TraversingCriterion tc = TC.create().negation().joker().done();
		runGcAndCheckResults(tc);
	}

	@Test
	public void testWithAbsenceInformation() throws GmSessionException {
		final TraversingCriterion tc = TC.create().negation().typeCondition(TypeConditions.isKind(TypeKind.simpleType)).done();
		runGcAndCheckResults(tc);
	}

	public void runGcAndCheckResults(final TraversingCriterion tc) throws GmSessionException {

		// set up GC
		final EntityTypeBasedEntitiesFinder rootEntitiesFinder = new EntityTypeBasedEntitiesFinder();
		final Set<Class<? extends GenericEntity>> rootEntityTypes = new HashSet<>();
		rootEntityTypes.add(ComplexEntity.class);
		rootEntitiesFinder.setEntityTypes(rootEntityTypes);
		rootEntitiesFinder.setTraversingCriterion(tc);

		final EntityTypeBasedEntitiesFinder subsetEntitiesFinder = new EntityTypeBasedEntitiesFinder();
		final Set<Class<? extends GenericEntity>> subsetEntityTypes = new HashSet<>();
		subsetEntityTypes.add(SimpleEntity.class);
		subsetEntityTypes.add(ComplexEntity.class);
		subsetEntitiesFinder.setEntityTypes(subsetEntityTypes);
		subsetEntitiesFinder.setTraversingCriterion(tc);

		final SubsetConfiguration conf = new SubsetConfiguration("test", rootEntitiesFinder, subsetEntitiesFinder);
		final List<SubsetConfiguration> subsetConfigurations = new ArrayList<>();
		subsetConfigurations.add(conf);

		final InMemoryGarbageCollection garbageCollection = new InMemoryGarbageCollection();

		final PersistenceGmSession session = TestModelTestTools.newSession(this.access);

		// perform GC
		garbageCollection.performGarbageCollection(session, subsetConfigurations, false);

		// check result
		final EntityQuery entityQuery = EntityQuery.T.create();
		entityQuery.setEntityTypeSignature(GenericEntity.class.getName());
		List<GenericEntity> actualExistingEntities = session.query().entities(entityQuery).list();

		assertThat(GMCoreTools.referenceEquals(new HashSet<GenericEntity>(actualExistingEntities),
				this.expectedExistingEntities)).isTrue();
	}
}
