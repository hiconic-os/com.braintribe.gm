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
package com.braintribe.model.processing.session.api.common;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.AbsenceInformation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.StandardCloningContext;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;

class InSessionCloningContext extends StandardCloningContext {
		private final PersistenceGmSession session;
		
		private boolean transferId;

		InSessionCloningContext(PersistenceGmSession session, boolean transferId) {
			this.session = session;
			this.transferId = transferId;
		}

		@Override
		public <T> T getAssociated(GenericEntity entity) {
			GenericEntity alreadyPersistedInstance = session.query().entity(entity.reference()).find();
			return (T) alreadyPersistedInstance;
		}

		@Override
		public GenericEntity supplyRawClone(EntityType<? extends GenericEntity> entityType, GenericEntity instanceToBeCloned) {
			return session.create(entityType);
		}

		@Override
		public boolean canTransferPropertyValue(EntityType<? extends GenericEntity> entityType, Property property, GenericEntity instanceToBeCloned,
				GenericEntity clonedInstance, AbsenceInformation sourceAbsenceInformation) {
			if (transferId)
				return true;
			
			return !property.isIdentifying() && !property.isGlobalId();
		}
	}