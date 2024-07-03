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
import com.braintribe.model.processing.generic.synchronize.experts.QueryingIdentityManager;

/**
 * Base interface for builders providing implementations of
 * {@link QueryingIdentityManager}.
 */
public interface QueryingIdentiyManagerBuilder<S extends GenericEntitySynchronization, B extends QueryingIdentiyManagerBuilder<S, B>>
		extends IdentityManagerBuilder<S> {

	/**
	 * If called, the session cache won't be respected during lookup of already
	 * existing entities.
	 */
	B ignoreCache();

	/**
	 * If called, identity properties with a null value will be included in the
	 * query. By default, if at least one of the identity properties has a null
	 * value the lookup query won't be executed and no existing entity is
	 * assumed.
	 */
	B supportNullIdentityProperty();

}
