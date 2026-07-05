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
package com.braintribe.model.access.security.manipulation.experts.acl;

import static com.braintribe.model.access.security.common.AbstractSecurityAspectTest.ADMINISTERING_HAS_ACL_ROLE;
import static com.braintribe.model.access.security.common.AbstractSecurityAspectTest.ADMIN_ROLE;
import static com.braintribe.model.access.security.common.AbstractSecurityAspectTest.VOLUNTEER_ROLE;
import static com.braintribe.utils.lcd.CollectionTools2.asList;

import org.junit.Before;
import org.junit.Test;

import com.braintribe.model.access.security.manipulation.experts.AbstractAclIlsValidatorTest;
import com.braintribe.model.access.security.testdata.query.AclEntity;
import com.braintribe.model.acl.AclOperation;

/**
 * 
 */
public class HasAcl_custom_Tests extends AbstractAclIlsValidatorTest {

	private AclEntity aclEntity;

	@Override
	@Before
	public void setUp() {
		super.setUp();

		setUserRoles(ADMIN_ROLE);
	}

	// owner==null && acl==null || isOwner || WRITE || HasAcl is Administrable

	@Test
	public void write_NoAcl_Allow() {
		aclEntity = acls.create_NoAcl();

		validateCvm();
		assertOk();
	}

	@Test
	public void write_anyAdmin_IsAdmin_Allow() {
		aclEntity = acls.create_AnyAdmin();

		validateCvm();
		assertOk();
	}

	@Test
	public void write_anyAdmin_NoAdmin_Deny() {
		setUserRoles(VOLUNTEER_ROLE);
		aclEntity = acls.create_AnyAdmin();

		validateCvm();
		assertAclEntityError();
	}

	@Test
	public void write_anyAdmin_NoAdmin_CanAdministerHasAcl_Allow() {
		setUserRoles(VOLUNTEER_ROLE, ADMINISTERING_HAS_ACL_ROLE);
		aclEntity = acls.create_AnyAdmin();

		validateCvm();
		assertOk();
	}

	private void validateCvm() {
		validate(() -> aclEntity.setPartition("partition"));
	}

	// ####################################################
	// ## . . . . . . . . . . Delete . . . . . . . . . . ##
	// ####################################################

	@Test
	public void delete_anyAdmin_IsAdmin_Allow() {
		acls.setAssignedAclOps(asList(AclOperation.DELETE));

		aclEntity = acls.create_AnyAdmin();

		validateDelete();
		assertOk();
	}

	@Test
	public void delete_anyAdmin_IsAdminCannotDelete_Deny() {
		acls.setAssignedAclOps(asList(AclOperation.WRITE)); // default for this test, having here for better readability

		aclEntity = acls.create_AnyAdmin();

		validateDelete();
		assertAclEntityError();
	}

	@Test
	public void delete_anyAdmin_IsAdminCannotDelete_CanAdministerHasAcl_Deny() {
		setUserRoles(ADMIN_ROLE, ADMINISTERING_HAS_ACL_ROLE);
		acls.setAssignedAclOps(asList(AclOperation.WRITE)); // default for this test, having here for better readability

		aclEntity = acls.create_AnyAdmin();

		validateDelete();
		assertOk();
	}

	@Test
	public void delete_anyAdmin_NoAdmin_Deny() {
		setUserRoles(VOLUNTEER_ROLE);
		acls.setAssignedAclOps(asList(AclOperation.DELETE));

		aclEntity = acls.create_AnyAdmin();

		validateDelete();
		assertAclEntityError();
	}

	private void validateDelete() {
		validate(() -> aclEntity.session().deleteEntity(aclEntity));
	}

	private void assertAclEntityError() {
		assertNumberOfErrors(1);
		assertErrors(AclEntity.T, null); // propertyName is null
	}

}
