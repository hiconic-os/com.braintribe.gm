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
package com.braintribe.model.access.security.manipulation.acl;

import static com.braintribe.utils.lcd.CollectionTools2.asSet;
import static org.fest.assertions.Assertions.assertThat;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.braintribe.model.access.security.common.AbstractSecurityAspectTest;
import com.braintribe.model.acl.Acl;
import com.braintribe.model.acl.AclCustomEntry;
import com.braintribe.model.acl.AclEntry;
import com.braintribe.model.acl.AclOperation;
import com.braintribe.model.acl.AclPermission;
import com.braintribe.model.acl.AclStandardEntry;
import com.braintribe.model.processing.query.fluent.EntityQueryBuilder;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.query.EntityQuery;

public class AclAccessibilityInterceptorTestBase extends AbstractSecurityAspectTest {

	protected Acl acl(PersistenceGmSession session, String name, Function<PersistenceGmSession, AclEntry>... aclFactories) {
		List<AclEntry> entries = Stream.of(aclFactories) //
				.map(f -> f.apply(session)) //
				.collect(Collectors.toList());

		return acl(session, name, entries);
	}

	protected Acl acl(PersistenceGmSession session, String name, List<AclEntry> entries) {
		Acl acl = acquireAcl(session, name);
		acl.setName(name);
		acl.getEntries().addAll(entries);

		return acl;
	}

	private Acl acquireAcl(PersistenceGmSession session, String name) {
		Acl existing = queryAcl(session, name);

		return existing != null ? existing : session.create(Acl.T);
	}

	protected AclStandardEntry aclEntry(PersistenceGmSession session, AclPermission permission, String role, AclOperation operation) {
		AclStandardEntry result = session.create(AclStandardEntry.T);
		result.setPermission(permission);
		result.setRole(role);
		result.setOperation(operation);

		return result;
	}

	protected AclCustomEntry aclEntry(PersistenceGmSession session, AclPermission permission, String role, String operation) {
		AclCustomEntry result = session.create(AclCustomEntry.T);
		result.setPermission(permission);
		result.setRole(role);
		result.setOperation(operation);

		return result;
	}

	// ################################################
	// ## . . . . . . . . Assertions . . . . . . . . ##
	// ################################################

	protected String not(String role) {
		return "!" + role;
	}

	protected void assertAcl(String name, String... accessibility) {
		refreshDelegateSession();
		
		Acl persistdAcl = queryAcl(name);

		assertThat(persistdAcl).as("No acl found with name: " + name).isNotNull();
		assertThat(persistdAcl.getAccessibility()).isEqualTo(asSet(accessibility));
	}

	private Acl queryAcl(String name) {
		return queryAcl(delegateSession, name);
	}

	private Acl queryAcl(PersistenceGmSession session, String name) {
		return session.query().entities(aclByName(name)).unique();
	}

	private EntityQuery aclByName(String name) {
		return EntityQueryBuilder.from(Acl.T).where().property("name").eq(name).done();
	}

}
