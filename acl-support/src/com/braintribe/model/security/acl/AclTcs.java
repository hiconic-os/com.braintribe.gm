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
package com.braintribe.model.security.acl;

import com.braintribe.model.access.IncrementalAccess;
import com.braintribe.model.acl.Acl;
import com.braintribe.model.acl.AclEntry;
import com.braintribe.model.acl.HasAcl;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.criteria.TraversingCriterion;
import com.braintribe.model.generic.processing.pr.fluent.TC;
import com.braintribe.model.generic.typecondition.TypeConditions;

/**
 * @author peter.gazdik
 */
public class AclTcs {

	// @formatter:off
	public static final TraversingCriterion TC_MATCHING_ACL_PROPS = TC.create() //
			.disjunction()
				.pattern()
					.typeCondition(TypeConditions.isAssignableTo(HasAcl.T))
					.disjunction()
						.property("owner")
						.property("acl")
					.close()
				.close()
				.pattern()
					.disjunction()
						.typeCondition(TypeConditions.isType(Acl.T))
						.typeCondition(TypeConditions.isAssignableTo(AclEntry.T))
					.close()
					.property()
				.close()
			.close() // disjunction
			.done();

	public static final TraversingCriterion HAS_ACL_TC = TC.create() //
										.negation() //
											.disjunction()
												.property(GenericEntity.id)
												.property(GenericEntity.partition)
												.criterion(TC_MATCHING_ACL_PROPS)
											.close()
										.done();

	public static final TraversingCriterion DEFAULT_TC_WITH_ACL = addAclEagerLoadingTo(
			TC.create().placeholder(IncrementalAccess.DEFAULT_TC_NAME).done());

	public static TraversingCriterion addAclEagerLoadingTo(TraversingCriterion tc) {
		return TC.create() //
			.conjunction()
				.criterion(tc)
				.negation() //
					.criterion(TC_MATCHING_ACL_PROPS)
				.close()
			.done();
	}
	// @formatter:on

}
