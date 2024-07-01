// ============================================================================
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
// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
// ============================================================================
package com.braintribe.gm.service.commons.test;

import org.junit.Test;

import com.braintribe.gm.service.commons.test.model.EvalTestAccessRequest;
import com.braintribe.gm.service.commons.test.model.EvalTestAccessAuthRequest;
import com.braintribe.model.securityservice.OpenUserSession;
import com.braintribe.model.securityservice.credentials.UserPasswordCredentials;
import com.braintribe.testing.junit.assertions.assertj.core.api.Assertions;

/**
 *
 * This test class needs to be improved and moved to another artifact. Committing only because of time limitations and code handover.
 *
 * @author Neidhart.Orlich
 * @author Dirk Scheffler
 *
 */
public class AccessProcessorTest extends CompositeServiceProcessorTestBase {
	@Test
	public void test() {
		EvalTestAccessRequest request = EvalTestAccessRequest.T.create();
		request.setDomainId("test.access");
		Object result = evaluator.eval(request).get();
		Assertions.assertThat(result).isEqualTo("test.access");
	}

	@Test
	public void test2() {
		UserPasswordCredentials credentials = UserPasswordCredentials.forUserName("tester", "7357");

		OpenUserSession openUserSession = OpenUserSession.T.create();
		openUserSession.setCredentials(credentials);
		String sessionId = openUserSession.eval(evaluator).get().getUserSession().getSessionId();

		EvalTestAccessAuthRequest request = EvalTestAccessAuthRequest.T.create();
		request.setDomainId("test.access");
		request.setSessionId(sessionId);
		Object result = evaluator.eval(request).get();
		Assertions.assertThat(result).isEqualTo("tester");
	}
}
