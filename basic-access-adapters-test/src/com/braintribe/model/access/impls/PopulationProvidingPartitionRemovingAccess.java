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
package com.braintribe.model.access.impls;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.braintribe.model.access.BasicAccessAdapter;
import com.braintribe.model.access.ModelAccessException;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.BaseType;
import com.braintribe.model.processing.smood.Smood;
import com.braintribe.model.query.EntityQuery;
import com.braintribe.model.query.EntityQueryResult;

/**
 * There was a problem related to partitions, which showed up when we had a condition comparing two entities in a query.
 * For example: <code>select p from Person p where p.address = PersistentEntityReference(1, 'accessId')</code>.
 * 
 * The problem was, that the left side of comparison was coming directly from the data result of loadPopulation, where
 * partition was null. the right side - the reference - was resolved with {@link BasicAccessAdapter#getEntity}, which
 * before was delegating to {@link BasicAccessAdapter#queryEntities(com.braintribe.model.query.EntityQuery)}, and this
 * had the partition set. Thus the used
 */
public class PopulationProvidingPartitionRemovingAccess extends BasicAccessAdapter {

	private final Smood dataSource;

	public PopulationProvidingPartitionRemovingAccess(Smood dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	protected EntityQueryResult createEntityQueryResult(EntityQuery query, List<GenericEntity> resultingEntities, boolean hasMore) throws Exception {
		EntityQueryResult result = EntityQueryResult.T.create();
		result.setEntities(resultingEntities);
		result.setHasMore(hasMore);

		return result;
	}

	@Override
	protected Collection<GenericEntity> loadPopulation() throws ModelAccessException {
		Set<GenericEntity> entities = dataSource.getEntitiesPerType(GenericEntity.T);
		Collection<GenericEntity> result = BaseType.INSTANCE.clone(entities, null, null);
		result.forEach(e -> e.setPartition(null));

		return result;
	}

}
