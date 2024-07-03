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
package com.braintribe.model.processing.findrefs;

import com.braintribe.model.generic.pr.criteria.TraversingCriterion;
import com.braintribe.model.generic.processing.pr.fluent.TC;
import com.braintribe.model.generic.typecondition.TypeConditions;
import com.braintribe.model.generic.typecondition.basic.TypeKind;
import com.braintribe.model.generic.value.EntityReference;
import com.braintribe.model.query.EntityQuery;
import com.braintribe.model.query.Restriction;
import com.braintribe.model.query.conditions.Condition;

/**
 * The {@link RefereeQueryBuilder} builds entity queries to find all entities where a specific property references an
 * entity
 * 
 * 
 */
public class RefereeQueryBuilder {

	private PropertyConditionCreator propertyConditionCreator = new PropertyConditionCreator();
	private static TraversingCriterion tc = TC.create()
			.pattern()
				
			.close()
		.done();
	
	static {
		// PGA: I don't get it, this TC cannot match anything, the typeCondition is always false.
		tc = TC.create()
			.conjunction()
				.property()				
				.typeCondition(
						TypeConditions.and(
								TypeConditions.isKind(TypeKind.collectionType), 
								TypeConditions.isKind(TypeKind.entityType)))
				.close()
			.done();
	}
	
	public EntityQuery buildQuery(CandidateProperty candidateProperty, EntityReference reference) {
		EntityQuery entityQuery = EntityQuery.T.create();
		entityQuery.setEntityTypeSignature(candidateProperty.getEntityTypeSignature());

		Condition condition = createConditionForProperty(candidateProperty, reference);

		Restriction restriction = Restriction.T.create();
		restriction.setCondition(condition);
		entityQuery.setRestriction(restriction);

		entityQuery.setTraversingCriterion(tc);
		return entityQuery;
	}

	private Condition createConditionForProperty(CandidateProperty property, EntityReference reference) {
		return propertyConditionCreator.createConditionForProperty(property, reference);
	}

	public void setPropertyConditionCreator(PropertyConditionCreator propertyConditionCreator) {
		this.propertyConditionCreator = propertyConditionCreator;
	}
}
