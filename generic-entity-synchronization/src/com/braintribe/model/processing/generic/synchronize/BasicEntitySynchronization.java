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
package com.braintribe.model.processing.generic.synchronize;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.processing.generic.synchronize.BasicIdentityManagers.BasicIdentityManagerBuildersImpl;
import com.braintribe.model.processing.generic.synchronize.api.GenericEntitySynchronization;
import com.braintribe.model.processing.generic.synchronize.api.IdentityManager;
import com.braintribe.model.processing.generic.synchronize.api.builder.BasicIdentityManagerBuilders;

/**
 * An {@link GenericEntitySynchronization} implementation that can be <br />
 * used for any {@link GenericEntity} by configuring according <br />
 * {@link IdentityManager}'s
 */
public class BasicEntitySynchronization extends AbstractSynchronization<BasicEntitySynchronization> {

	/**
	 * Private constructor. Use {@link #newInstance()} to get an instance of the
	 * {@link BasicEntitySynchronization}.
	 */
	protected BasicEntitySynchronization(boolean withDefaultIdentityManagers) {
		super(withDefaultIdentityManagers);
	}

	/**
	 * Get a new instance of {@link BasicEntitySynchronization} with default
	 * identityManagers added.
	 * 
	 * @return
	 */
	public static BasicEntitySynchronization newInstance() {
		return newInstance(true);
	}

	/**
	 * Get a new instance of {@link CortexSynchronization} with option to
	 * disable default identityManagers.
	 */
	public static BasicEntitySynchronization newInstance(boolean withDefaultIdentityManagers) {
		return new BasicEntitySynchronization(withDefaultIdentityManagers);
	}

	/**
	 * Provides a builder to create and add a new {@link IdentityManager}
	 * fluently.
	 */
	public BasicIdentityManagerBuilders<BasicEntitySynchronization> addIdentityManager() {
		return new BasicIdentityManagerBuildersImpl<BasicEntitySynchronization>(this);
	}

}
