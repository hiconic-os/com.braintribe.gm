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
package com.braintribe.model.generic.pr.criteria;

import com.braintribe.model.generic.annotation.meta.Mandatory;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * This criterion represents another criterion with can be resolved by {@link #getName() name}.
 * <p>
 * Example: Every access can have default {@link TraversingCriterion} which describe which entity properties should be loaded (fetched) eagerly. If
 * the client is fine with the defaults, he does not have to specify the TC at all, thus saving time building it and data transfered. Along the way,
 * some aspect might need to adjust the criteria to load more information, e.g. to apply some authorization checks. In order to be able to extend the
 * default criteria, instead of replacing them, an explicit representation of the default TC is needed, to describe how exactly they are combined with
 * the injected TC.
 */
public interface PlaceholderCriterion extends BasicCriterion {

	EntityType<PlaceholderCriterion> T = EntityTypes.T(PlaceholderCriterion.class);

	@Mandatory
	String getName();
	void setName(String name);

	@Override
	default CriterionType criterionType() {
		return CriterionType.PLACEHOLDER;
	}

}
