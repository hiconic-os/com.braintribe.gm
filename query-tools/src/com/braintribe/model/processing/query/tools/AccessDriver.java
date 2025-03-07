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
package com.braintribe.model.processing.query.tools;

import static com.braintribe.utils.lcd.CollectionTools2.first;
import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.function.Consumer;

import com.braintribe.model.access.IncrementalAccess;
import com.braintribe.model.accessapi.ManipulationResponse;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.processing.query.fluent.EntityQueryBuilder;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.query.EntityQuery;
import com.braintribe.model.query.EntityQueryResult;
import com.braintribe.model.query.PropertyQuery;
import com.braintribe.model.query.PropertyQueryResult;
import com.braintribe.model.query.Query;
import com.braintribe.model.query.QueryResult;
import com.braintribe.model.query.SelectQuery;
import com.braintribe.model.query.SelectQueryResult;

/**
 * 
 */
public class AccessDriver {

	public final IncrementalAccess access;

	private final PersistenceGmSession session;

	private boolean traceQueries;

	private boolean lastAcquireWasCreate;

	public AccessDriver(IncrementalAccess access, PersistenceGmSession session) {
		this.access = access;
		this.session = session;
	}

	public void setTraceQueries(boolean traceQueries) {
		this.traceQueries = traceQueries;
	}

	public PersistenceGmSession gmSession() {
		return session;
	}

	public String getAccessId() {
		return access.getAccessId();
	}

	public <T extends GenericEntity> T acquireEntity(EntityType<T> entityType, String uniqueProperty, String uniqueValue) {
		return acquireEntity(entityType, uniqueProperty, uniqueValue, null);
	}

	public <T extends GenericEntity> T acquireEntity(EntityType<T> entityType, String uniqueProperty, String uniqueValue,
			Consumer<T> entityInitializer) {
		T result = queryEntityByProperty(entityType, uniqueProperty, uniqueValue);

		if (result != null) {
			lastAcquireWasCreate = false;
			return result;
		}

		result = createEntity(entityType, uniqueProperty, uniqueValue);
		if (entityInitializer != null)
			entityInitializer.accept(result);

		commit();

		lastAcquireWasCreate = true;
		return result;
	}

	public <T extends GenericEntity> T createEntity(EntityType<T> entityType, String uniqueProperty, String uniqueValue) {
		T result = session.create(entityType);
		result.write(entityType.getProperty(uniqueProperty), uniqueValue);
		return result;
	}

	public <T extends GenericEntity> T requireEntityByProperty(EntityType<T> entityType, String uniqueProperty, String uniqueValue) {
		return requireNonNull(queryEntityByProperty(entityType, uniqueProperty, uniqueValue),
				entityType.getShortName() + " not found by criteria: " + uniqueProperty + "=" + uniqueValue);
	}

	public <T extends GenericEntity> T queryEntityByProperty(EntityType<T> entityType, String uniqueProperty, String uniqueValue) {
		EntityQuery query = EntityQueryBuilder.from(entityType).where().property(uniqueProperty).eq(uniqueValue).done();
		List<GenericEntity> entities = query(query).getEntities();

		return entities.isEmpty() ? null : first(entities);
	}

	public <T extends GenericEntity> T acquireAnyEntity(EntityType<T> entityType) {
		T result = queryAny(entityType);
		if (result != null) {
			lastAcquireWasCreate = false;
			return result;
		}

		result = session.create(entityType);
		commit();

		lastAcquireWasCreate = true;
		return result;
	}

	public boolean lastAcquireWasCreate() {
		return lastAcquireWasCreate;
	}

	public ManipulationResponse commit() {
		return session.commit();
	}

	public <T extends GenericEntity> List<T> queryAll(EntityType<T> et) {
		return (List<T>) query(EntityQueryBuilder.from(et).done()).getEntities();
	}

	public <T extends GenericEntity> T queryAny(EntityType<T> et) {
		List<T> entities = (List<T>) query(EntityQueryBuilder.from(et).limit(1).done()).getEntities();
		return entities.isEmpty() ? null : first(entities);
	}

	public void delete(GenericEntity entity) {
		session.deleteEntity(entity);
	}

	public SelectQueryResult query(SelectQuery query) {
		return runQuery(query);
	}

	public EntityQueryResult query(EntityQuery query) {
		return runQuery(query);
	}

	public PropertyQueryResult query(PropertyQuery query) {
		return runQuery(query);
	}

	public <Q extends Query, R extends QueryResult> R runQuery(Q query) {
		if (traceQueries) {
			System.out.println("\n-------------------------------");
			System.out.println("\n\nRunning query: " + query.stringify());
		}

		R result = (R) session.query().abstractQuery(query).result();

		if (traceQueries) {
			System.out.println("\n\nQueryResult:");
			QueryResultPrinter.printQueryResult(result);
		}

		return result;
	}

}
