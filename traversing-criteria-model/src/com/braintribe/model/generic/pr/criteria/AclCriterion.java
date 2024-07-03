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

import com.braintribe.common.attribute.AttributeContext;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * Matches if the {@link #getOperation() operation specified here} is granted for the context entity (one for which we are resolving the TC). The
 * operation is not granted only if given entity is an instance of com.braintribe.model.acl.HasAcl, and it's isOperationGranted method returns
 * <tt>false</tt>.
 * <p>
 * Roles for this check check are taken from the RoleAspect, if not available, it falls back to UserInfoAttribute in the {@link AttributeContext}.
 */
public interface AclCriterion extends StackElementCriterion {

	EntityType<AclCriterion> T = EntityTypes.T(AclCriterion.class);

	String getOperation();
	void setOperation(String operation);

	@Override
	default CriterionType criterionType() {
		return CriterionType.ACL;
	}

}
