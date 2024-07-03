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

/**
 * Criterion for map entry. The typeSignature is exactly the same as for {@link MapCriterion}. After this criterion is pushed on the stack,
 * the entry key/value are pushed as well, so this is used if someone wants to access the entire entry at once, but if the key/value may be
 * processed separately, one may ignore this and check {@link MapKeyCriterion} and/or {@link MapValueCriterion}.
 */

public interface MapEntryCriterion extends BasicCriterion {

	final EntityType<MapEntryCriterion> T = EntityTypes.T(MapEntryCriterion.class);

	@Override
	default CriterionType criterionType() {
		return CriterionType.MAP_ENTRY;
	}

}
