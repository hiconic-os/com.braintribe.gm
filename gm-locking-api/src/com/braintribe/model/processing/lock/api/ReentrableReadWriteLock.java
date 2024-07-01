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
package com.braintribe.model.processing.lock.api;

import java.util.concurrent.locks.ReadWriteLock;

/**
 * A non-reentrant {@link ReadWriteLock} with a re-entry possible with another instance (as for writes, reads are re-entrant of course).
 * <p>
 * This lock has a special value - {@link #reentranceId()}, which can be used to create another lock with the same lockId. Such locks are not blocking
 * each other.
 * 
 * @see Locking
 * 
 * @author peter.gazdik
 */
public interface ReentrableReadWriteLock extends ReadWriteLock {

	String lockId();

	/** See {@link Locking#withReentranceId(String)}. */
	String reentranceId();

}
