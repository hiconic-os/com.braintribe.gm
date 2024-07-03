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
package com.braintribe.model.processing.rpc.test.commons;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.braintribe.model.processing.webrpc.client.GmWebRpcClientAuthorizationContext;

/**
 * A {@link GmWebRpcClientAuthorizationContext} which keeps the notified authorization failures in a ThreadLocal.
 */
public class TestRpcClientAuthorizationContext extends GmWebRpcClientAuthorizationContext {

	private static ThreadLocal<Set<Throwable>> failures = new ThreadLocal<Set<Throwable>>();

	@Override
	public void onAuthorizationFailure(Throwable authorizationFailure) {

		super.onAuthorizationFailure(authorizationFailure);

		if (failures.get() == null) {
			Set<Throwable> set = new HashSet<>();
			set.add(authorizationFailure);
			failures.set(set);
		} else {
			failures.get().add(authorizationFailure);
		}

	}

	public Set<Throwable> getNotifiedFailures() {
		Set<Throwable> set = failures.get();
		if (set == null) {
			set = Collections.emptySet();
		}
		return set;
	}

}
