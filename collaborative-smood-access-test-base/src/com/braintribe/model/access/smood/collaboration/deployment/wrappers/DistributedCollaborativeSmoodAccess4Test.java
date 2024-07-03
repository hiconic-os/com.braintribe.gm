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
package com.braintribe.model.access.smood.collaboration.deployment.wrappers;

import java.util.function.BiConsumer;

import com.braintribe.common.lcd.function.TriConsumer;
import com.braintribe.model.access.collaboration.CollaborationSmoodSession;
import com.braintribe.model.access.collaboration.distributed.DistributedCollaborativeSmoodAccess;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.manipulation.DeleteMode;

/**
 * DCSA that we use for tests, to have a way to intercept/assert things going on inside.
 * 
 * See Dcsa_Manipulation_Correctness_Test#updateWithDeleteManipulationsDoesNotQueryForReferences()
 * 
 * @author peter.gazdik
 */
public class DistributedCollaborativeSmoodAccess4Test extends DistributedCollaborativeSmoodAccess {

	@Override
	protected CollaborationSmoodSession newSession() {
		return new CollaborationSmoodSession4Test();
	}

	@Override
	public CollaborationSmoodSession4Test getSmoodSession() {
		return (CollaborationSmoodSession4Test) session;
	}

	public static class CollaborationSmoodSession4Test extends CollaborationSmoodSession {

		public TriConsumer<GenericEntity, DeleteMode, BiConsumer<GenericEntity, DeleteMode>> deleteEntityInterceptor;

		@Override
		public void deleteEntity(GenericEntity entity, DeleteMode deleteMode) {
			if (deleteEntityInterceptor != null)
				deleteEntityInterceptor.accept(entity, deleteMode, super::deleteEntity);
			else
				super.deleteEntity(entity, deleteMode);
		}

	}

}
