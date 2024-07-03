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

import java.util.Collection;

import com.braintribe.model.processing.generic.synchronize.api.GenericEntitySynchronization;
import com.braintribe.model.processing.generic.synchronize.experts.GenericIdentityManager;
import com.braintribe.model.query.conditions.Conjunction;

/**
 * Builder interface for {@link GenericIdentityManager}'s 
 */
public interface GenericIdentityManagerBuilder<S extends GenericEntitySynchronization> extends ConfigurableIdentityManagerBuilder<S,GenericIdentityManagerBuilder<S>> {

	/**
	 * Adds a property (name) of the responsible type that should be used to
	 * build the lookup query when searching for existing instances. All
	 * properties added will be combined in a {@link Conjunction} when running
	 * the lookup query.
	 */
	GenericIdentityManagerBuilder<S> addIdentityProperty(String property);

	/**
	 * Same as {@link #addIdentityProperty(String)} but let you specify multiple
	 * properties in one step.
	 */
	GenericIdentityManagerBuilder<S> addIdentityProperties(Collection<String> properties);
	
	/**
	 * Same as {@link #addIdentityProperty(String)} but let you specify multiple
	 * properties in one step.
	 */
	GenericIdentityManagerBuilder<S> addIdentityProperties(String... properties);

}
