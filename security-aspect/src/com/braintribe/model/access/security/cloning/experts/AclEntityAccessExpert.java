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
package com.braintribe.model.access.security.cloning.experts;

import com.braintribe.model.access.security.query.PostQueryExpertContextImpl;
import com.braintribe.model.acl.AclOperation;
import com.braintribe.model.acl.HasAcl;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.processing.security.query.context.EntityExpertContext;
import com.braintribe.model.processing.security.query.expert.EntityAccessExpert;
import com.braintribe.model.processing.session.api.persistence.auth.SessionAuthorization;

/**
 * Verifies whether a given {@link HasAcl} entity is accessible.
 */
public class AclEntityAccessExpert implements EntityAccessExpert {

	/**
	 * @return <tt>false</tt> iff we are visiting a {@link HasAcl} instance, for which the ACL condition implies access
	 *         denial
	 */
	@Override
	public boolean isAccessGranted(EntityExpertContext expertContext) {
		GenericEntity entity = expertContext.getEntity();

		if (!(entity instanceof HasAcl))
			return true;

		/* TODO improve, this is a quick hack for now to avoid repetitive MD resolution here. Context should have some
		 * way to store information for these experts, maybe some aspects like with CMD context... */
		if (expertContext instanceof PostQueryExpertContextImpl)
			if (!((PostQueryExpertContextImpl) expertContext).needsHasAclChecks())
				return true;

		HasAcl hasAcl = (HasAcl) entity;
		SessionAuthorization sa = expertContext.getSession().getSessionAuthorization();

		return hasAcl.isOperationGranted(AclOperation.READ, sa.getUserName(), sa.getUserRoles());
	}

}
