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

import com.braintribe.model.acl.Acl;
import com.braintribe.model.acl.HasAcl;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.value.PersistentEntityReference;
import com.braintribe.model.meta.data.security.Administrable;
import com.braintribe.model.processing.meta.cmd.CmdResolver;
import com.braintribe.model.processing.meta.oracle.ModelOracle;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;

/**
 * @author peter.gazdik
 */
public class AclTools {

	public static HasAcl queryAclEntity(PersistenceGmSession session, PersistentEntityReference ref) {
		return (HasAcl) session.query().entity(ref).withTraversingCriterion(AclTcs.HAS_ACL_TC).require();
	}

	public static boolean supportsAcl(PersistenceGmSession session) {
		return supportsAcl(session.getModelAccessory().getOracle());
	}

	public static boolean isHasAclAdministrable(PersistenceGmSession session) {
		return isAdministrable(session, HasAcl.T);
	}

	public static boolean isAclAdministrable(PersistenceGmSession session) {
		return isAdministrable(session, Acl.T);
	}

	private static boolean isAdministrable(PersistenceGmSession session, EntityType<?> et) {
		return isAdministrable(session.getModelAccessory().getCmdResolver(), et);
	}

	public static boolean supportsAcl(ModelOracle modelOracle) {
		return modelOracle.findEntityTypeOracle(HasAcl.T) != null;
	}

	public static boolean isHasAclAdministrable(CmdResolver cmdResolver) {
		return isAdministrable(cmdResolver, HasAcl.T);
	}

	public static boolean isAclAdministrable(CmdResolver cmdResolver) {
		return isAdministrable(cmdResolver, Acl.T);
	}

	private static boolean isAdministrable(CmdResolver cmdResolver, EntityType<?> et) {
		return cmdResolver.getMetaData().entityType(et).useCase("acl").is(Administrable.T);
	}

}
