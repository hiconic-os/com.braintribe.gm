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
package com.braintribe.gwt.utils.genericmodel;

import java.util.HashSet;
import java.util.Set;

import com.braintribe.common.lcd.ConfigurationException;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.criteria.TraversingCriterion;
import com.braintribe.model.generic.processing.pr.fluent.TC;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.query.EntityQuery;

/**
 * This {@link EntitiesFinder} is used to find entities based on a set of configured entity types. For more info see
 * method {@link #findEntities(PersistenceGmSession)}.
 *
 */
public class EntityTypeBasedEntitiesFinder extends EntityQueryBasedEntitiesFinder {

	private Set<String> entityTypeSignatures;

	private TraversingCriterion traversingCriterion = TC.create().negation().joker().done();

	public EntityTypeBasedEntitiesFinder() {
		// nothing to do
	}

	public EntityTypeBasedEntitiesFinder(final Set<String> entityTypeSignatures) {
		this.entityTypeSignatures = entityTypeSignatures;
	}

	/**
	 * Iterates through the set of the configured {@link #setEntityTypes(Set) entityTypes}, creates a new
	 * {@link EntityQuery} based on an entityType, finds the respective entities for each entity type and returns the
	 * union of the result sets.
	 *
	 * @throws ConfigurationException
	 *             if the list of the entity types is <code>null</code>.
	 */
	@Override
	public Set<GenericEntity> findEntities(final PersistenceGmSession session) {

		final Set<GenericEntity> foundEntitiesUnion = new HashSet<GenericEntity>();

		if (this.entityTypeSignatures == null) {
			throw new ConfigurationException("The configured set of entity types must not be null!");
		}

		for (final String entityTypeSignature : this.entityTypeSignatures) {
			final EntityQuery entityQuery = EntityQuery.T.create();
			setEntityQuery(entityQuery);
			entityQuery.setEntityTypeSignature(entityTypeSignature);
			entityQuery.setTraversingCriterion(getTraversingCriterion());

			final Set<GenericEntity> foundEntitiesBasedOnEntityType = super.findEntities(session);
			foundEntitiesUnion.addAll(foundEntitiesBasedOnEntityType);
		}
		return foundEntitiesUnion;
	}

	public void setEntityTypes(final Set<Class<? extends GenericEntity>> entityTypes) {
		this.entityTypeSignatures = new HashSet<String>();
		for (final Class<? extends GenericEntity> entityType : entityTypes) {
			this.entityTypeSignatures.add(entityType.getName());
		}
	}

	public void setEntityTypeSignatures(final Set<String> entityTypeSignatures) {
		this.entityTypeSignatures = entityTypeSignatures;
	}

	public TraversingCriterion getTraversingCriterion() {
		return this.traversingCriterion;
	}

	public void setTraversingCriterion(final TraversingCriterion traversingCriterion) {
		this.traversingCriterion = traversingCriterion;
	}
}
