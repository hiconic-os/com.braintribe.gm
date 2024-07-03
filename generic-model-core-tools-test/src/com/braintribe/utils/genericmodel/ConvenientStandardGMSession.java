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
package com.braintribe.utils.genericmodel;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.braintribe.common.lcd.GenericRuntimeException;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.criteria.TraversingCriterion;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.processing.query.fluent.EntityQueryBuilder;
import com.braintribe.model.processing.query.fluent.JunctionBuilder;
import com.braintribe.model.processing.session.impl.persistence.BasicPersistenceGmSession;
import com.braintribe.model.query.EntityQuery;
import com.braintribe.model.query.Query;
import com.braintribe.model.query.SelectQuery;
import com.braintribe.utils.CollectionTools;
import com.braintribe.utils.CommonTools;

public class ConvenientStandardGMSession extends BasicPersistenceGmSession {

	public <T extends GenericEntity> T getEntityById(final EntityType<T> entityClass, final Object id) throws Exception {
		return query().entity(entityClass, id).find();
	}

	public <T extends GenericEntity> T getExistingEntityById(final EntityType<T> entityClass, final Object id)
			throws Exception {
		return query().entity(entityClass, id).refresh();
	}

	public <T extends GenericEntity> T getExistingEntityById(final EntityType<T> entityClass, final Object id,
			final TraversingCriterion traversingCriterion) throws Exception {
		return query().entity(entityClass, id).withTraversingCriterion(traversingCriterion).refresh();
	}

	public <T extends GenericEntity> T getEntityById(final EntityType<T> entityClass, final Object id,
			final TraversingCriterion traversingCriterion) throws Exception {
		return query().entity(entityClass, id).withTraversingCriterion(traversingCriterion).find();
	}

	public <T extends GenericEntity> T getEntityByProperty(final Class<T> entityClass, final String propertyName,
			final Object propertyValue) throws Exception {
		final EntityQuery entityQuery = EntityQueryBuilder.from(entityClass).where().property(propertyName)
				.eq(propertyValue).done();
		return query().entities(entityQuery).first();
	}

	/*
	 * TODO: Should be discussed
	 */
	public <T extends GenericEntity> T getExistingEntityByProperty(final Class<T> entityClass,
			final String propertyName, final Object propertyValue) throws Exception {
		final T entity = getEntityByProperty(entityClass, propertyName, propertyValue);
		if (entity == null) {
			throw new Exception("Couldn't find " + entityClass.getSimpleName() + " entity where property '"
					+ propertyName + "' is set to '" + propertyValue + "'!");
		}
		return entity;
	}

	public <T extends GenericEntity> List<T> getEntitiesByProperty(final Class<T> entityClass,
			final String propertyName, final Object propertyValue) throws Exception {
		final EntityQuery entityQuery = EntityQueryBuilder.from(entityClass).where().property(propertyName)
				.eq(propertyValue).done();
		return query().entities(entityQuery).list();
	}

	/*
	 * TODO: Should be discussed
	 */
	public <T extends GenericEntity> List<T> getEntitiesByProperties(final Class<T> entityClass,
			final Map<String, ? extends Object> propertyNamesAndValues) throws Exception {

		final EntityQueryBuilder entityQueryBuilder = EntityQueryBuilder.from(entityClass);

		if (CommonTools.isEmpty(propertyNamesAndValues)) {
			// nothing to do
		} else if (propertyNamesAndValues.size() == 1) {
			final Entry<String, ? extends Object> propertyNameAndValue = CollectionTools
					.getSingleElement(propertyNamesAndValues.entrySet());
			entityQueryBuilder.where().property(propertyNameAndValue.getKey()).eq(propertyNameAndValue.getValue());
		} else {
			final JunctionBuilder<EntityQueryBuilder> junctionBuilder = entityQueryBuilder.where().conjunction();
			for (final Entry<String, ? extends Object> propertyNameAndValue : propertyNamesAndValues.entrySet()) {
				junctionBuilder.property(propertyNameAndValue.getKey()).eq(propertyNameAndValue.getValue());
			}
			junctionBuilder.close();
		}

		final EntityQuery query = entityQueryBuilder.done();
		return query().entities(query).list();
	}

	public <T extends GenericEntity> List<T> getEntitiesByType(final Class<T> entityClass) throws Exception {
		final EntityQuery entityQuery = EntityQueryBuilder.from(entityClass).done();
		return query().entities(entityQuery).list();
	}

	/*
	 * TODO: Should be discussed
	 */
	public <T extends GenericEntity> T getEntityByQuery(final Query query) throws Exception {

		if (query instanceof EntityQuery) {
			return query().entities((EntityQuery) query).first();
		} else if (query instanceof SelectQuery) {
			return query().select((SelectQuery) query).first();
		} else {
			throw new GenericRuntimeException("Unsupported query type " + query.getClass().getName() + "!");
		}
	}

	/*
	 * TODO: Should be discussed
	 */
	public <T extends GenericEntity> T getExistingEntityByQuery(final Query query) throws Exception {
		final T entity = getEntityByQuery(query);
		if (entity == null) {
			throw new Exception("Query unexpectedly returned no result! "
					+ CommonTools.getParametersString("query", query));
		}
		return entity;
	}

	/*
	 * TODO: Should be discussed
	 */
	public <T extends GenericEntity> List<T> getEntitiesByQuery(final Query query) throws Exception {
		if (query instanceof EntityQuery) {
			return query().entities((EntityQuery) query).list();
		} else if (query instanceof SelectQuery) {
			return query().select((SelectQuery) query).list();
		} else {
			throw new GenericRuntimeException("Unsupported query type " + query.getClass().getName() + "!");
		}
	}
}
