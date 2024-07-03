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
package com.braintribe.model.processing.session.impl.managed;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.value.EntityReference;
import com.braintribe.model.generic.value.PersistentEntityReference;
import com.braintribe.model.processing.session.api.managed.EntityAccessBuilder;
import com.braintribe.model.processing.session.api.managed.SessionQueryBuilder;

public abstract class AbstractSessionQueryBuilder implements SessionQueryBuilder {
	@Override
	public <T extends GenericEntity> EntityAccessBuilder<T> entity(String typeSignature, Object id) {
		return entity(typeSignature, id, EntityReference.ANY_PARTITION);
	}
	
	@Override
	public <T extends GenericEntity> EntityAccessBuilder<T> entity(String typeSignature, Object id, String partition) {
		PersistentEntityReference entityReference = PersistentEntityReference.T.create();
		entityReference.setRefId(id);
		entityReference.setTypeSignature(typeSignature);
		entityReference.setRefPartition(partition);
		return entity(entityReference);
	}

	@Override
	public <T extends GenericEntity> EntityAccessBuilder<T> entity(EntityType<T> entityType, Object id) {
		return entity(entityType, id, EntityReference.ANY_PARTITION);
	}

	@Override
	public <T extends GenericEntity> EntityAccessBuilder<T> entity(EntityType<T> entityType, Object id, String partition) {
		PersistentEntityReference entityReference = PersistentEntityReference.T.create();
		entityReference.setTypeSignature(entityType.getTypeSignature());
		entityReference.setRefId(id);
		entityReference.setRefPartition(partition);
		return entity(entityReference);
	}

}
