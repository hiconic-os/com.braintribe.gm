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

import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.meta.GmType;
import com.braintribe.model.processing.query.fluent.SelectQueryBuilder;
import com.braintribe.model.query.SelectQuery;

/**
 * @author peter.gazdik
 */
public interface PreparedQueries {

	static SelectQuery modelByName(String modelName) {
		return entityBySimpleProperty(GmMetaModel.T, "name", modelName);
	}

	static SelectQuery typeBySignature(String typeSignature) {
		return typeBySignature(GmType.T, typeSignature);
	}

	static SelectQuery typeBySignature(EntityType<? extends GmType> modelElementType, String typeSignature) {
		return entityBySimpleProperty(modelElementType, "typeSignature", typeSignature);
	}

	static SelectQuery entityBySimpleProperty(EntityType<?> type, String propertyName, Object propertyValue) {
		return new SelectQueryBuilder().from(type, "t").where().property("t", propertyName).eq(propertyValue).done();
	}

	static SelectQuery entitiesByGlobalIds(Set<String> globalIds) {
		return entitiesByGlobalIds(GenericEntity.T, globalIds);
	}

	static SelectQuery entitiesByGlobalIds(EntityType<?> entityType, Set<String> globalIds) {
		return new SelectQueryBuilder().from(entityType, "t").where().property("t", GenericEntity.globalId).in(globalIds).done();
	}
}
