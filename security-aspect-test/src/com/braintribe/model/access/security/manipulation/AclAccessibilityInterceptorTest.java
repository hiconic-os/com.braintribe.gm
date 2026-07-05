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
package com.braintribe.model.access.security.manipulation;

import static com.braintribe.model.acl.AclPermission.DENY;
import static com.braintribe.model.acl.AclPermission.GRANT;

import org.junit.Test;

import com.braintribe.model.access.security.manipulation.acl.AclAccessibilityInterceptorTestBase;
import com.braintribe.model.acl.AclEntry;
import com.braintribe.model.acl.AclOperation;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;

public class AclAccessibilityInterceptorTest extends AclAccessibilityInterceptorTestBase {

	private final AclAccessibilityInterceptorTest i = this;

	@Test
	public void singleGrantCreated() throws Exception {
		acl(aopSession, "defaultAcl", //
				i::grantAdminRead //
		);
		aopSession.commit();

		assertAcl("defaultAcl", //
				ADMIN_ROLE //
		);
	}

	@Test
	public void singleDenyCreated() throws Exception {
		acl(aopSession, "defaultAcl", //
				i::denyVolunteerRead //
		);
		aopSession.commit();

		assertAcl("defaultAcl", //
				not(VOLUNTEER_ROLE) //
		);
	}

	@Test
	public void ignoresNonReadOperations() throws Exception {
		acl(aopSession, "defaultAcl", //
				i::grantAdminRead, //
				i::grantVolunteerWrite //
		);
		aopSession.commit();

		assertAcl("defaultAcl", //
				ADMIN_ROLE //
		);
	}

	@Test
	public void singleDenyAdded() throws Exception {
		acl(delegateSession, "defaultAcl", //
				i::grantAdminRead //
		);
		delegateSession.commit();
		
		acl(aopSession, "defaultAcl", //
				i::denyVolunteerRead //
		);
		aopSession.commit();

		assertAcl("defaultAcl", //
				ADMIN_ROLE, //
				not(VOLUNTEER_ROLE) //
		);
	}

	private AclEntry grantAdminRead(PersistenceGmSession entityFactory) {
		return aclEntry(entityFactory, GRANT, ADMIN_ROLE, AclOperation.READ);
	}

	
	private AclEntry denyVolunteerRead(PersistenceGmSession entityFactory) {
		return aclEntry(entityFactory, DENY, VOLUNTEER_ROLE, AclOperation.READ);
	}

	private AclEntry grantVolunteerWrite(PersistenceGmSession entityFactory) {
		return aclEntry(entityFactory, DENY, VOLUNTEER_ROLE, AclOperation.WRITE);
	}
	
}
