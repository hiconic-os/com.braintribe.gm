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
package com.braintribe.model.processing.core.commons;

import static com.braintribe.model.generic.typecondition.TypeConditions.isKind;
import static com.braintribe.model.generic.typecondition.TypeConditions.or;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.pr.criteria.TraversingCriterion;
import com.braintribe.model.generic.pr.criteria.matching.StandardMatcher;
import com.braintribe.model.generic.processing.pr.fluent.TC;
import com.braintribe.model.generic.reflection.StrategyOnCriterionMatch;
import com.braintribe.model.generic.typecondition.basic.TypeKind;

public class AssemblyExport {

	public static <T> T flatExport(T assembly) {
		StandardMatcher matcher = new StandardMatcher();
		TraversingCriterion tc = TC.create()
					.conjunction()
						.property()
						.typeCondition(or(
								isKind(TypeKind.collectionType),
								isKind(TypeKind.entityType)
							))
					.close()
					.done();
							
		matcher.setCriterion(tc);
		
		T exportedAssembly = (T)GMF.getTypeReflection().getBaseType().clone(assembly, matcher, StrategyOnCriterionMatch.partialize);
		
		return exportedAssembly;
	}

}
