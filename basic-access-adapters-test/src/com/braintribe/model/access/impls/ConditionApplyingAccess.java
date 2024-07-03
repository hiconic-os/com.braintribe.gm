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

import java.util.List;

import com.braintribe.model.access.ModelAccessException;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.value.EntityReference;
import com.braintribe.model.processing.query.fluent.EntityQueryBuilder;
import com.braintribe.model.processing.smood.Smood;
import com.braintribe.model.query.EntityQuery;
import com.braintribe.model.query.Restriction;
import com.braintribe.model.query.conditions.Condition;

/**
 * 
 */
public class ConditionApplyingAccess extends AbstractTestAccess {

	private final Smood dataSource;

	public ConditionApplyingAccess(Smood dataSource, boolean idIsIndexed) {
		this.dataSource = dataSource;
		this.repository.setAssumeIdIsIndexed(idIsIndexed);
	}

	@Override
	protected List<GenericEntity> queryPopulation(String typeSignature, Condition condition) throws ModelAccessException {
		EntityQuery query = EntityQueryBuilder.from(typeSignature).done();
		query.setRestriction(restrictionFor(condition));

		return dataSource.queryEntities(query).getEntities();
	}

	private Restriction restrictionFor(Condition condition) {
		if (condition == null) {
			return null;
		}

		Restriction restriction = Restriction.T.create();
		restriction.setCondition(condition);

		return restriction;
	}

	@Override
	protected GenericEntity getEntity(EntityReference entityReference) throws ModelAccessException {
		return dataSource.getEntity(entityReference);
	}

}
