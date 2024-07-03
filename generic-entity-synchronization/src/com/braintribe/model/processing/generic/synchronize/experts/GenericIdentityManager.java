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
package com.braintribe.model.processing.generic.synchronize.experts;

import java.util.Collection;
import java.util.HashSet;

import com.braintribe.cfg.Configurable;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.processing.generic.synchronize.api.SynchronizationContext;

/**
 * Implementation of {@link ConfigurableIdentityManager} that can be configured with 
 * identityProperties via according setters and add methods.
 *
 */
public class GenericIdentityManager extends ConfigurableIdentityManager {

	private Collection<String> identityProperties = new HashSet<String>();

	
	@Configurable
	public void setIdentityProperties(Collection<String> identityProperties) {
		this.identityProperties = identityProperties;
	}
	
	public void addIdentityProperties(Collection<String> identityProperties) {
		this.identityProperties.addAll(identityProperties);
	}
	
	/**
	 * Returns the collection internally stored with {@link #setIdentityProperties(Collection)} or {@link #addIdentityProperties(Collection)}.
	 */
	public Collection<String> getIdentityProperties(GenericEntity instance, EntityType<? extends GenericEntity> entityType, SynchronizationContext context) {
		return identityProperties;
	}


	
}
