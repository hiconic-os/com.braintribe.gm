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
package com.braintribe.model.meta.data;

import com.braintribe.model.generic.annotation.Abstract;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * A {@link PredicateErasure} is a predicate that removes the positive effect of it's supertype. For example, for visibility we have a
 * {@link Predicate} called Visible. In order to make something not visible, we use a predicate called Hidden, which is a subtype of both Visible and
 * {@link PredicateErasure}, thus saying this negates the positive effect of Visible.
 * <p>
 * Note that one should (almost) never resolve a PredicateErasure with CmdResolver. When resolving the positive predicate, the resolved MD could be an
 * instance of that positive type, or the erasure, but the resolution of an erasure always returns just the erasure (obviously, as that is the
 * subtype). The CMD resolver would actually throw an exception if the predicate-specific resolution method would be used ({@code .is(Hidden.T)}), but
 * there is no exception when resolving in a general way (via {@code list()/exclusive()} methods).
 * 
 * @see Predicate
 */
@Abstract
public interface PredicateErasure extends Predicate {

	EntityType<PredicateErasure> T = EntityTypes.T(PredicateErasure.class);

	@Override
	default boolean isTrue() {
		return false;
	}

}
