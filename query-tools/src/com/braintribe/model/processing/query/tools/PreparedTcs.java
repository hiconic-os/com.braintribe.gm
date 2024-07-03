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
package com.braintribe.model.processing.query.tools;

import static com.braintribe.model.generic.typecondition.TypeConditions.isKind;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.criteria.TraversingCriterion;
import com.braintribe.model.generic.processing.pr.fluent.TC;
import com.braintribe.model.generic.typecondition.basic.TypeKind;

/**
 * @author peter.gazdik
 */
public interface PreparedTcs {

	// @formatter:off
	TraversingCriterion everythingTc = TC.create().negation().joker().done();

	TraversingCriterion scalarOnlyTc = TC.create()
											.negation()
												.disjunction()
													.property(GenericEntity.id)
													.typeCondition(isKind(TypeKind.scalarType))
												.close()
											.done();
	// @formatter:on

}
