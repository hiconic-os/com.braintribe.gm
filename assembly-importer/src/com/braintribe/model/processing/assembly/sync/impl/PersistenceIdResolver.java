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
package com.braintribe.model.processing.assembly.sync.impl;

import static com.braintribe.utils.lcd.CollectionTools2.newMap;
import static com.braintribe.utils.lcd.CollectionTools2.newSet;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelException;
import com.braintribe.model.generic.session.exception.GmSessionException;
import com.braintribe.model.processing.query.fluent.SelectQueryBuilder;
import com.braintribe.model.processing.query.stringifier.BasicQueryStringifier;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.query.SelectQuery;
import com.braintribe.model.query.SelectQueryResult;
import com.braintribe.model.record.ListRecord;

/**
 * @author peter.gazdik
 */
public class PersistenceIdResolver {

	private static final int BULK_SIZE = 100;

	private final PersistenceGmSession session;
	private final Map<EntityType<?>, List<String>> globalIdMap;

	private final PersistenceInfoMaps result = new PersistenceInfoMaps();

	public PersistenceIdResolver(PersistenceGmSession session, Map<EntityType<?>, List<String>> globalIdMap) {
		this.session = session;
		this.globalIdMap = globalIdMap;
	}

	public static PersistenceInfoMaps resolve(PersistenceGmSession session, Map<EntityType<?>, List<String>> globalIdMap) {
		return new PersistenceIdResolver(session, globalIdMap).resolve();
	}

	public static class PersistenceInfoMaps {
		public Map<String, Object> idMap = newMap();
		public Map<String, String> partitionMap = newMap();
	}

	private PersistenceInfoMaps resolve() {
		for (Entry<EntityType<?>, List<String>> entry : globalIdMap.entrySet()) {
			EntityType<?> entityType = entry.getKey();
			List<String> globalIds = entry.getValue();

			resolveFor(entityType, globalIds);
		}

		return result;
	}

	private void resolveFor(EntityType<?> entityType, List<String> globalIds) {
		int counter = 0;
		Set<String> bulkSet = newSet();

		Iterator<String> it = globalIds.iterator();
		while (it.hasNext()) {
			bulkSet.add(it.next());

			if (counter++ == BULK_SIZE || !it.hasNext()) {
				resolveBulk(entityType, bulkSet);
				bulkSet.clear();
			}
		}
	}

	private void resolveBulk(EntityType<?> entityType, Set<String> bulkSet) {
		// @formatter:off
		SelectQuery query = new SelectQueryBuilder()
				.select("e", GenericEntity.globalId)
				.select("e", GenericEntity.id)
				.select("e", GenericEntity.partition)
				.from(entityType, "e")
				.where()
					.property("e", GenericEntity.globalId).in(bulkSet)
			.done();
		// @formatter:on

		for (ListRecord listRecord : evaluate(query)) {
			List<Object> values = listRecord.getValues();
			String globalId = (String) values.get(0);
			Object persistenceId = values.get(1);
			String partition = (String) values.get(2);

			result.idMap.put(globalId, persistenceId);
			result.partitionMap.put(globalId, partition);
		}
	}

	private List<ListRecord> evaluate(SelectQuery query) {
		try {
			SelectQueryResult queryResult = session.query().select(query).result();

			return (List<ListRecord>) (List<?>) queryResult.getResults();

		} catch (GmSessionException e) {
			throw new GenericModelException("Error while resolving query: " + BasicQueryStringifier.print(query), e);
		}
	}

}
