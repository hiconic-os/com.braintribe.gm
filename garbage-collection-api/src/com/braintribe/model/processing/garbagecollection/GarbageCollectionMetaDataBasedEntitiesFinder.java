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

import java.util.HashSet;
import java.util.Set;

import com.braintribe.gwt.utils.genericmodel.EntitiesFinder;
import com.braintribe.gwt.utils.genericmodel.EntityTypeBasedEntitiesFinder;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.meta.GmEntityType;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.meta.data.cleanup.GarbageCollection;
import com.braintribe.model.meta.data.cleanup.GarbageCollectionKind;
import com.braintribe.model.processing.meta.cmd.CmdResolver;
import com.braintribe.model.processing.meta.cmd.CmdResolverImpl;
import com.braintribe.model.processing.meta.oracle.BasicModelOracle;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;

/**
 * Finds entities based on the {@link GarbageCollection} metadata.
 *
 * @author michael.lafite
 */
public class GarbageCollectionMetaDataBasedEntitiesFinder implements EntitiesFinder {

	private final GmMetaModel metaModel;
	private final String useCase;
	private final GarbageCollectionKind searchedGarbageCollectionKind;

	public GarbageCollectionMetaDataBasedEntitiesFinder(final GmMetaModel metaModel, final String useCase,
			final GarbageCollectionKind searchedGarbageCollectionKind) {
		this.metaModel = metaModel;
		this.useCase = useCase;
		this.searchedGarbageCollectionKind = searchedGarbageCollectionKind;
	}

	@Override
	public Set<GenericEntity> findEntities(final PersistenceGmSession session) {
		final Set<String> entityTypes = findEntityTypes(this.metaModel, this.searchedGarbageCollectionKind,
				this.useCase);

		final EntityTypeBasedEntitiesFinder entityTypeBasedEntitiesFinder = new EntityTypeBasedEntitiesFinder();
		entityTypeBasedEntitiesFinder.setEntityTypeSignatures(entityTypes);

		final Set<GenericEntity> result = entityTypeBasedEntitiesFinder.findEntities(session);

		return result;
	}

	public static Set<String> findEntityTypes(final GmMetaModel metaModel,
			final GarbageCollectionKind searchedGarbageCollectionKind, final String useCase) {

		BasicModelOracle modelOracle = new BasicModelOracle(metaModel);
		final CmdResolver cmdResolver = new CmdResolverImpl(modelOracle);

		final Set<String> entityTypeSignatures = new HashSet<>();

		modelOracle.getTypes().onlyEntities().<GmEntityType> asGmTypes().forEach(entityType -> {
			GarbageCollection metaData = cmdResolver.getMetaData().entityType(entityType).useCase(useCase).meta(GarbageCollection.T).exclusive();

			if (metaData != null) {
				final GarbageCollectionKind kind = metaData.getKind();
				if (kind == null) {
					throw new GarbageCollectionException(GarbageCollection.class.getSimpleName()
							+ " metadata not properly specified for entity type " + entityType.getTypeSignature() + ": "
							+ GarbageCollectionKind.class.getSimpleName() + " missing!");
				}

				if (kind.equals(searchedGarbageCollectionKind)) {
					entityTypeSignatures.add(entityType.getTypeSignature());
				}
			}
		});

		return entityTypeSignatures;
	}

}
