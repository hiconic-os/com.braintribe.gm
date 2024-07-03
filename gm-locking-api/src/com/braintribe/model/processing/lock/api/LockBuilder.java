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
package com.braintribe.model.processing.lock.api;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

public interface LockBuilder {
	Lock exclusive();
	Lock shared();
	
	@Deprecated
	LockBuilder caller(String callerSignature);
	
	/**
	 * NetworkTools.getNetworkAddress().getHostAddress()
	 * 
	 */
	@Deprecated
	LockBuilder machine(String machineSignature);

	@Deprecated
	LockBuilder holderId(String holderId);

	/**
	 * @deprecated Use {@link #lockTtl(long, TimeUnit)} instead. 
	 */
	@Deprecated
	LockBuilder lockTimeout(long time, TimeUnit unit);
	@Deprecated
	LockBuilder lockTtl(long time, TimeUnit unit);
}
