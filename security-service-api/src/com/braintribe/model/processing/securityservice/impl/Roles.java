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
package com.braintribe.model.processing.securityservice.impl;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.braintribe.model.securityservice.AuthenticateCredentialsResponse;
import com.braintribe.model.securityservice.AuthenticatedUser;
import com.braintribe.model.securityservice.AuthenticatedUserSession;
import com.braintribe.model.user.Group;
import com.braintribe.model.user.Identity;
import com.braintribe.model.user.Role;
import com.braintribe.model.user.User;
import com.braintribe.model.usersession.UserSession;

public interface Roles {

	static Set<String> authenticatedCredentialsEffectiveRoles(AuthenticateCredentialsResponse authenticateCredentialsResponse) {
		if (authenticateCredentialsResponse instanceof AuthenticatedUserSession) {
			AuthenticatedUserSession authenticateedUserSession = (AuthenticatedUserSession) authenticateCredentialsResponse;
			return Optional.ofNullable(authenticateedUserSession.getUserSession()).map(UserSession::getEffectiveRoles).orElse(Collections.emptySet());
		} else if (authenticateCredentialsResponse instanceof AuthenticatedUser) {
			AuthenticatedUser authenticatedUser = (AuthenticatedUser) authenticateCredentialsResponse;
			User user = authenticatedUser.getUser();
			return user != null ? userEffectiveRoles(user) : Collections.emptySet();
		} else
			throw new IllegalStateException(
					"Unexpected AuthenticateCredentialsResponse: " + authenticateCredentialsResponse.type().getTypeSignature());
	}

	static Set<String> userEffectiveRoles(User user) {
		return effectiveUserRoleStream(user).collect(Collectors.toSet());
	}

	static Stream<String> effectiveUserRoleStream(User user) {
		if (user == null)
			return Stream.empty();

		return Stream.of( //
				roleNames(user), //
				user.getGroups().stream().flatMap(Roles::effectiveGroupRoleStream), //
				Stream.of("$all"), //
				implicitNameRoles(user, "$user-") //
		).flatMap(Function.identity());
	}

	static Stream<String> effectiveGroupRoleStream(Group group) {
		if (group == null)
			return Stream.empty();

		return Stream.concat( //
				roleNames(group), //
				implicitNameRoles(group, "$group-") //
		);
	}

	static Stream<String> implicitNameRoles(Identity identity, String prefix) {
		String name = identity.getName();
		if (name == null)
			return Stream.empty();
		else
			return Stream.of(prefix + name);
	}

	static Stream<String> roleNames(Identity identity) {
		if (identity == null)
			return Stream.empty();

		return identity.getRoles().stream() //
				.filter(r -> r != null) //
				.map(Role::getName) //
				.filter(n -> n != null);
	}

	static Role roleFromStr(String name) {
		Role role = Role.T.create();
		role.setName(name);
		return role;
	}
}
