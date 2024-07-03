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
package com.braintribe.model.access.collaboration;

import com.braintribe.model.generic.enhance.ManipulationTrackingPropertyAccessInterceptor;
import com.braintribe.model.processing.session.api.notifying.interceptors.CollectionEnhancer;
import com.braintribe.model.processing.session.api.notifying.interceptors.ManipulationTracking;
import com.braintribe.model.processing.session.api.resource.ResourceAccess;
import com.braintribe.model.processing.session.impl.managed.AbstractManagedGmSession;
import com.braintribe.model.processing.session.impl.session.collection.CollectionEnhancingPropertyAccessInterceptor;
import com.braintribe.model.processing.smood.Smood;

/**
 * @author peter.gazdik
 */
public class CollaborationSmoodSession extends AbstractManagedGmSession {

	public CollaborationSmoodSession() {
		super(false);

		interceptors().with(ManipulationTracking.class).before(CollectionEnhancer.class).add(noChangeAssignmentIgnoringMtpai());
		interceptors().with(CollectionEnhancer.class).add(new CollectionEnhancingPropertyAccessInterceptor());
	}

	private static ManipulationTrackingPropertyAccessInterceptor noChangeAssignmentIgnoringMtpai() {
		ManipulationTrackingPropertyAccessInterceptor mtpai = new ManipulationTrackingPropertyAccessInterceptor();
		mtpai.ignoredNoChangeAssignments = true;
		return mtpai;
	}

	public void setSmood(Smood smood) {
		super.setBackup(smood);
	}

	@Override
	public ResourceAccess resources() {
		throw new UnsupportedOperationException("Method 'SmoodSession.resources' is not supported!");
	}

}
