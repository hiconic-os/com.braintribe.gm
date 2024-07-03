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

import com.braintribe.model.processing.session.api.managed.ManagedGmSession;
import com.braintribe.model.processing.session.api.resource.ResourceAccess;
import com.braintribe.model.processing.session.api.resource.ResourceAccessFactory;

/**
 * @see AbstractManagedGmSession
 * @see ManagedGmSession
 */
public class BasicManagedGmSession extends AbstractManagedGmSession {

	private ResourceAccessFactory<? super BasicManagedGmSession> resourcesAccessFactory;
	private ResourceAccess resourcesAccess;

	public void setResourcesAccessFactory(ResourceAccessFactory<? super BasicManagedGmSession> resourcesAccessFactory) {
		this.resourcesAccessFactory = resourcesAccessFactory;
	}
	
	public void setResourcesAccess(ResourceAccess resourcesAccess) {
		this.resourcesAccess = resourcesAccess;
	}
	
	
	protected ResourceAccess getResourcesAccess() {
		if (resourcesAccess == null && resourcesAccessFactory != null) {
			resourcesAccess = resourcesAccessFactory.newInstance(this);
		}

		return resourcesAccess;
	}
	
	@Override
	public ResourceAccess resources() {
		ResourceAccess builder = getResourcesAccess();
		if (builder != null)
			return builder;
		else
			throw new UnsupportedOperationException("no resource builder configured for the session");
	}
}
