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

import static com.braintribe.model.access.security.common.AbstractSecurityAspectTest.VOLUNTEER_ROLE;
import static com.braintribe.utils.lcd.CollectionTools2.asList;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import com.braintribe.model.access.security.common.AbstractSecurityAspectTest;
import com.braintribe.model.access.security.manipulation.experts.AbstractAclIlsValidatorTest;
import com.braintribe.model.access.security.testdata.acl.AclFactory;
import com.braintribe.model.access.security.testdata.query.AclEntity;
import com.braintribe.model.acl.AclOperation;
import com.braintribe.model.acl.HasAcl;
import com.braintribe.model.processing.security.manipulation.SecurityViolationEntry;

/**
 * 
 */
public class HasAcl_acl_Tests extends AbstractAclIlsValidatorTest {

	private AclEntity volunteerModify;
	private AclEntity volunteerReplace;
	private AclEntity volunteerRw;
	private AclEntity adminAll;

	@Override
	@Before
	public void setUp() {
		super.setUp();

		setUserRoles(VOLUNTEER_ROLE);

		acls.setAssignedAclOps(asList(AclOperation.MODIFY_ACL, AclOperation.WRITE, AclOperation.READ));
		volunteerModify = acls.create_Volunteer();
		adminAll = acls.create_AnyAdmin();

		acls.setAssignedAclOps(asList(AclOperation.REPLACE_ACL, AclOperation.WRITE, AclOperation.READ));
		volunteerReplace = acls.create_Volunteer();

		acls.setAssignedAclOps(asList(AclOperation.READ, AclOperation.WRITE));
		volunteerRw = acls.create_Volunteer();
	}

	// ###################################
	// ## . . . . . . AddAcl . . . . . .##
	// ###################################

	// anything goes

	@Test
	public void setting_Allowed() {
		AclEntity hasNoAcl = acls.create_NoAcl();
		flushAndValidate(() -> hasNoAcl.setAcl(volunteerModify.getAcl()));
		assertOk();
	}

	@Test
	public void setting_NoModifyAcl() {
		AclEntity hasNoAcl = acls.create_NoAcl();
		flushAndValidate(() -> hasNoAcl.setAcl(volunteerRw.getAcl()));
		assertOk();
	}

	@Test
	public void settingToPreliminaryHasAcl_StillDenied() {
		flushAndValidate(() -> acls.create_NoAcl().setAcl(volunteerRw.getAcl()));
		assertOk();
	}

	@Test
	public void setting_NoRole() {
		AclEntity hasNoAcl = acls.create_NoAcl();
		flushAndValidate(() -> hasNoAcl.setAcl(adminAll.getAcl()));
		assertOk();
	}

	// ###################################
	// ## . . . . . RemoveAcl . . . . . ##
	// ###################################

	// isOwner || MODIFY_ACL || REPLACE_ACL || HasAcl is Administrable

	@Test
	public void remove_OnlyOwner_IsOwner_Allowed() {
		AclEntity onlyOwner = acls.create_JustOwner();
		setUserName(AclFactory.ALLOWED_ACL_USER);

		flushAndValidate(() -> onlyOwner.setAcl(null));
		assertOk();
	}

	@Test
	public void remove_OnlyOwner_NotOwner_Deny() {
		AclEntity onlyOwner = acls.create_JustOwner();
		setUserName(AclFactory.UNKNOWN_ACL_USER);

		flushAndValidate(() -> onlyOwner.setAcl(null));
		assertAclEntityError();
	}

	@Test
	public void remove_Acl_CanModify_Allowed() {
		flushAndValidate(() -> volunteerModify.setAcl(null));
		assertOk();
	}

	@Test
	public void remove_Acl_CanReplace_Allowed() {
		flushAndValidate(() -> volunteerReplace.setAcl(null));
		assertOk();
	}

	@Test
	public void remove_OwnerOrAcl_CanReplace_Allow() {
		volunteerReplace.setOwner(AclFactory.ALLOWED_ACL_USER);
		commit();

		flushAndValidate(() -> volunteerReplace.setAcl(null));
		assertOk();
	}

	@Test
	public void removeAcl_Acl_WriteButNotModifyAcl_Deny() {
		flushAndValidate(() -> volunteerRw.setAcl(null));
		assertAclError();
	}

	@Test
	public void remove_Acl_CanWriteAndAdministerAcl_Allow() {
		setUserRoles(VOLUNTEER_ROLE, AbstractSecurityAspectTest.ADMINISTERING_HAS_ACL_ROLE);
		flushAndValidate(() -> volunteerRw.setAcl(null));
		assertOk();
	}

	@Test
	public void remove_Acl_CanAdministerAcl_Allow() {
		// Just to be sure, that we don't get an error when write-access is checked
		setUserRoles(AbstractSecurityAspectTest.ADMINISTERING_HAS_ACL_ROLE);
		flushAndValidate(() -> volunteerRw.setAcl(null));
		assertOk();
	}

	@Test
	public void remove_Acl_NoRoleDeny() {
		flushAndValidate(() -> adminAll.setAcl(null));

		assertNumberOfErrors(2); // touching admin

		Iterator<SecurityViolationEntry> it = validationResult.getViolationEntries().iterator();
		assertError(it.next(), AclEntity.T, null);
		assertError(it.next(), AclEntity.T, "acl");
	}

	// ###################################
	// ## . . . . . Helpers . . . . . . ##
	// ###################################

	protected void flushAndValidate(Runnable r) {
		commit();
		validate(r);
	}

	private void assertAclError() {
		assertNumberOfErrors(1);
		assertErrors(AclEntity.T, HasAcl.acl);
	}

	private void assertAclEntityError() {
		assertNumberOfErrors(1);
		assertErrors(AclEntity.T, null);
	}

}
