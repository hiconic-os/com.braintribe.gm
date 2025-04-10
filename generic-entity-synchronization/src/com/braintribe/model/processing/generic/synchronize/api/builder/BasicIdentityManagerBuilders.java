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
package com.braintribe.model.processing.generic.synchronize.api.builder;

import com.braintribe.model.processing.generic.synchronize.api.GenericEntitySynchronization;
import com.braintribe.model.processing.generic.synchronize.api.IdentityManager;
import com.braintribe.model.processing.generic.synchronize.experts.ExternalIdIdentityManager;
import com.braintribe.model.processing.generic.synchronize.experts.GenericIdentityManager;
import com.braintribe.model.processing.generic.synchronize.experts.GlobalIdIdentityManager;
import com.braintribe.model.processing.generic.synchronize.experts.IdPropertyIdentityManager;

/**
 * Wrapping builder offering options to fluently build basic {@link IdentityManager}'s.
 */
public interface BasicIdentityManagerBuilders<S extends GenericEntitySynchronization> {

	/**
	 * Returns the builder to create a customized {@link ExternalIdIdentityManager} 
	 */
	public ExternalIdIdentityManagerBuilder<S> externalId();
	
	/**
	 * Returns the builder to create a customized {@link GlobalIdIdentityManager} 
	 */
	public GlobalIdIdentityManagerBuilder<S> globalId();
	
	/**
	 * Returns the builder to create a customized {@link IdPropertyIdentityManager}
	 */
	public IdPropertyIdentityManagerBuilder<S> idProperty();

	/**
	 * Returns the builder to create a customized {@link GenericIdentityManager} 
	 */
	public GenericIdentityManagerBuilder<S> generic();
	
}
