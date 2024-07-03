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
package com.braintribe.model.access;

import static com.braintribe.model.generic.GenericEntity.partition;
import static com.braintribe.model.generic.manipulation.util.ManipulationBuilder.changeValue;
import static com.braintribe.model.generic.manipulation.util.ManipulationBuilder.entityProperty;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.braintribe.model.accessapi.ReferencesCandidate;
import com.braintribe.model.accessapi.ReferencesRequest;
import com.braintribe.model.accessapi.ReferencesResponse;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.manipulation.ChangeValueManipulation;
import com.braintribe.model.generic.manipulation.EntityProperty;
import com.braintribe.model.generic.manipulation.Manipulation;
import com.braintribe.model.generic.pr.criteria.BasicCriterion;
import com.braintribe.model.generic.pr.criteria.CriterionType;
import com.braintribe.model.generic.pr.criteria.PropertyCriterion;
import com.braintribe.model.generic.pr.criteria.TraversingCriterion;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.GenericModelTypeReflection;
import com.braintribe.model.generic.reflection.StandardCloningContext;
import com.braintribe.model.generic.reflection.StrategyOnCriterionMatch;
import com.braintribe.model.processing.core.expert.api.DenotationMap;
import com.braintribe.model.processing.core.expert.impl.PolymorphicDenotationMap;
import com.braintribe.model.processing.findrefs.ReferenceFinder;
import com.braintribe.model.query.EntityQuery;
import com.braintribe.model.query.PropertyQuery;
import com.braintribe.model.query.Query;
import com.braintribe.model.query.SelectQuery;

/**
 * Alternative to {@link AbstractAccess} but cleaner and better documented.
 * 
 */
public abstract class AccessBase extends AbstractCustomPersistenceRequestProcessingAccess {

	public static final GenericModelTypeReflection typeReflection = GMF.getTypeReflection();

	protected DenotationMap<GenericEntity, TraversingCriterion> defaultTraversingCriteria;
	protected ReentrantLock defaultTraversingCriteriaLock = new ReentrantLock();
	protected final ReferenceFinder referenceFinder = new ReferenceFinder(this);
	protected String accessId;

	protected Set<String> partitions;
	protected ReentrantLock partitionsLock = new ReentrantLock();
	/** If partition of an entity is null, this value is used instead in the query result. */
	protected String defaultPartition;

	public void setDefaultTraversingCriteria(DenotationMap<GenericEntity, TraversingCriterion> defaultTraversingCriteria) {
		this.defaultTraversingCriteria = defaultTraversingCriteria;
	}

	public DenotationMap<GenericEntity, TraversingCriterion> getDefaultTraversingCriteria() {
		if (defaultTraversingCriteria == null)
			loadDefaultTraversingCriteria();

		return defaultTraversingCriteria;
	}

	protected void loadDefaultTraversingCriteria() {
		defaultTraversingCriteriaLock.lock();
		try {
			if (defaultTraversingCriteria != null)
				return;

			/* This will be read only, so we don't need to make it thread-safe */
			PolymorphicDenotationMap<GenericEntity, TraversingCriterion> map = new PolymorphicDenotationMap<>(false);
			map.put(GenericEntity.T, IncrementalAccesses.createDefaultTraversionCriterion());
			defaultTraversingCriteria = map;
		} finally {
			defaultTraversingCriteriaLock.unlock();
		}
	}

	protected List<GenericEntity> cloneEntityQueryResult(List<GenericEntity> entities, EntityQuery eq) {
		StandardCloningContext cloningContext = createStandardCloningContext();
		return IncrementalAccesses.cloneList(entities, cloningContext, strategy(eq), getTcFunction(eq.getTraversingCriterion()));
	}

	protected List<Object> cloneSelectQueryResults(List<Object> values, SelectQuery sq) {
		StandardCloningContext cloningContext = createStandardCloningContext();
		return IncrementalAccesses.cloneSelectQueryResults(values, cloningContext, strategy(sq), getTcFunction(sq.getTraversingCriterion()));
	}

	protected Object clonePropertyQueryResult(Object value, PropertyQuery pq) {
		StandardCloningContext cloningContext = createStandardCloningContext();
		return IncrementalAccesses.cloneObject(value, cloningContext, strategy(pq), getTcFunction(pq.getTraversingCriterion()));
	}

	private StrategyOnCriterionMatch strategy(Query query) {
		return query.getNoAbsenceInformation() ? StrategyOnCriterionMatch.skip : StrategyOnCriterionMatch.partialize;
	}

	private Function<EntityType<?>, TraversingCriterion> getTcFunction(TraversingCriterion tc) {
		if (tc != null)
			return et -> tc;
		else
			return defaultTraversingCriteria::get;
	}

	protected StandardCloningContext createStandardCloningContext() {
		return new QueryResultCloningContext();
	}

	protected class QueryResultCloningContext extends StandardCloningContext {
		@Override
		public Object postProcessCloneValue(GenericModelType propertyType, Object clonedValue) {
			BasicCriterion bc = getTraversingStack().peek();

			if (clonedValue == null && isPartitionProperty(bc))
				return defaultPartition;
			else
				return super.postProcessCloneValue(propertyType, clonedValue);
		}

		private boolean isPartitionProperty(BasicCriterion bc) {
			return bc.criterionType() == CriterionType.PROPERTY && partition.equals(((PropertyCriterion) bc).getPropertyName());
		}
	}

	@Override
	public ReferencesResponse getReferences(ReferencesRequest referencesRequest) throws ModelAccessException {
		Set<ReferencesCandidate> candidates = referenceFinder.findReferences(referencesRequest.getReference());
		return IncrementalAccesses.referenceCandidates(candidates);
	}

	protected List<Manipulation> createPartitionAssignmentManipulations(Stream<GenericEntity> entities) {
		return entities.map(this::createPartitionAssignmentManipulation).collect(Collectors.toList());
	}

	protected ChangeValueManipulation createPartitionAssignmentManipulation(GenericEntity entity) {
		EntityProperty owner = entityProperty(entity.reference(), GenericEntity.partition);
		return changeValue(defaultPartition, owner);
	}

	@Override
	public String getAccessId() {
		return accessId;
	}

	public void setAccessId(String accessId) {
		this.accessId = accessId;
		this.defaultPartition = accessId;
	}

	@Override
	public Set<String> getPartitions() throws ModelAccessException {
		if (partitions == null) {
			loadPartitions();
		}

		return partitions;
	}

	private void loadPartitions() {
		partitionsLock.lock();
		try {
			if (partitions == null) {
				partitions = Collections.singleton(getAccessId());
			}
		} finally {
			partitionsLock.unlock();
		}
	}

}
