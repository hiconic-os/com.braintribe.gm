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
package com.braintribe.model.processing.session.impl.persistence;

import static com.braintribe.utils.lcd.CollectionTools2.newMap;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.braintribe.cc.lcd.CodingSet;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.commons.EntRefHashingComparator;
import com.braintribe.model.generic.pr.criteria.TraversingCriterion;
import com.braintribe.model.generic.value.EntityReference;
import com.braintribe.model.generic.value.PersistentEntityReference;
import com.braintribe.model.processing.manipulation.basic.oracle.AbstractReferenceResolver;
import com.braintribe.model.processing.query.fluent.EntityQueryBuilder;
import com.braintribe.model.processing.session.api.managed.NotFoundException;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.query.EntityQuery;

/**
 * @author peter.gazdik
 */
public class PersistenceSessionReferenceResolver extends AbstractReferenceResolver {

	private final PersistenceGmSession session;
	private final Function<String, TraversingCriterion> tcProvider;

	public PersistenceSessionReferenceResolver(PersistenceGmSession session) {
		this(session, signature -> null);
	}

	public PersistenceSessionReferenceResolver(PersistenceGmSession session, Function<String, TraversingCriterion> tcProvider) {
		this(session, tcProvider, 100);
	}

	public PersistenceSessionReferenceResolver(PersistenceGmSession session, Function<String, TraversingCriterion> tcProvider, int bulkSize) {
		super(bulkSize);

		this.session = session;
		this.tcProvider = tcProvider;
	}

	@Override
	protected Map<PersistentEntityReference, GenericEntity> resolveBulk(String typeSignature, Set<PersistentEntityReference> references) {
		List<GenericEntity> entities = queryEntities(typeSignature, references);

		checkResolvedReferences(references, entities);

		return mapByReference(entities);
	}

	private List<GenericEntity> queryEntities(String typeSignature, Set<PersistentEntityReference> references) {
		EntityQuery query = buildQuery(typeSignature, references);

		return session.query().entities(query).list();
	}

	private EntityQuery buildQuery(String typeSignature, Set<PersistentEntityReference> references) {
		TraversingCriterion tc = tcProvider.apply(typeSignature);

		// @formatter:off
		return EntityQueryBuilder.from(typeSignature)
				.where()
					.entity(EntityQueryBuilder.DEFAULT_SOURCE).in(references)
				.tc(tc)
				.done();
		// @formatter:on
	}

	private void checkResolvedReferences(Set<PersistentEntityReference> refs, List<GenericEntity> entities) {
		if (refs.size() == entities.size())
			return;

		Set<?> resolvedRefs = entities.stream() //
				.map(GenericEntity::reference) //
				.collect(Collectors.toSet());

		Set<EntityReference> unresolvedRefs = CodingSet.create(EntRefHashingComparator.INSTANCE);
		unresolvedRefs.removeAll(resolvedRefs);

		throw new NotFoundException("Could not resolve the following entity references: " + unresolvedRefs);
	}

	private Map<PersistentEntityReference, GenericEntity> mapByReference(List<GenericEntity> entities) {
		Map<PersistentEntityReference, GenericEntity> result = newMap();

		for (GenericEntity entity : entities)
			result.put(entity.reference(), entity);

		return result;
	}

}
