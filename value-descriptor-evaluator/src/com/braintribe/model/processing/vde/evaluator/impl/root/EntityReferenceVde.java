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
package com.braintribe.model.processing.vde.evaluator.impl.root;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.value.EntityReference;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.processing.vde.evaluator.api.ValueDescriptorEvaluator;
import com.braintribe.model.processing.vde.evaluator.api.VdeContext;
import com.braintribe.model.processing.vde.evaluator.api.VdeEvaluationMode;
import com.braintribe.model.processing.vde.evaluator.api.VdeResult;
import com.braintribe.model.processing.vde.evaluator.api.VdeRuntimeException;
import com.braintribe.model.processing.vde.evaluator.api.aspects.SessionAspect;
import com.braintribe.model.processing.vde.evaluator.impl.VdeResultImpl;

/**
 * {@link ValueDescriptorEvaluator} for {@link EntityReference}
 * 
 */
public class EntityReferenceVde implements ValueDescriptorEvaluator<EntityReference> {

	private static EntityReferenceVde instance = null;

	protected EntityReferenceVde() {
		// empty
	}

	public static EntityReferenceVde getInstance() {
		if (instance == null) {
			instance = new EntityReferenceVde();
		}
		return instance;
	}
	
	@Override
	public VdeResult evaluate(VdeContext context, EntityReference valueDescriptor) throws VdeRuntimeException {
		PersistenceGmSession session = (PersistenceGmSession) context.get(SessionAspect.class);

		if (session == null && context.getEvaluationMode() == VdeEvaluationMode.Preliminary) {
			return new VdeResultImpl("No session provided in context");
		}
		try {
			// query the session to get the entity
			GenericEntity entity = session.query().entity(valueDescriptor).require();
			
			return new VdeResultImpl(entity,false);

		} catch (Exception e) {
			throw new VdeRuntimeException("EntityReference evaluation failed", e);
		}
	}

}
