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

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

import java.util.Set;


import com.braintribe.model.generic.pr.criteria.typematch.TypeMatch;

/**
 * @deprecated use {@link TypeConditionCriterion} instead
 * @author dirk.scheffler
 *
 */
@Deprecated

public interface PropertyTypeCriterion extends StackElementCriterion {

	final EntityType<PropertyTypeCriterion> T = EntityTypes.T(PropertyTypeCriterion.class);

	void setTypes(Set<TypeMatch> types);

	Set<TypeMatch> getTypes();

	@Override
	default CriterionType criterionType() {
		return CriterionType.PROPERTY_TYPE;
	}
}
