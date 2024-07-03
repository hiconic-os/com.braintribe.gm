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

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.processing.lock.impl.SimpleCdlLocking;

/**
 * Represents a registry from which {@link ReentrableReadWriteLock}s can be retrieved, based on an identifier or an entity.
 *
 * <h2>Locking Semantics</h2>
 * 
 * Locks with the same {@link ReentrableLocking #forIdentifier(String) identifiers} are one logical lock:<br>
 * <b>ReadLocks</b> can generally be locked concurrently in unlimited amounts<br>
 * <b>WriteLocks</b> can be locked concurrently only if they share the same {@link ReentrableReadWriteLock#reentranceId() reentranceId}. This means
 * that even a single writeLock is not re-entrant and attempt to lock it a second time results in an {@link IllegalStateException}.
 * <p>
 * 
 * <h2>Implementations</h2>
 * 
 * For sample implementation (which can be used in tests for example) see {@link SimpleCdlLocking}.
 * 
 * @see ReentrableReadWriteLock
 * @see SimpleCdlLocking
 */
public interface Locking extends ReentrableLocking, LockingDeprecations {

	/** This id is used for all read locks and is thus forbidden as a parameter to {@link #withReentranceId(String)}. */
	String READ_LOCK_REENTRANCE_ID = "READ_LOCK";

	/** Creates a new {@link ReentrableReadWriteLock} with given reentranceId. See the semantics comment above. */
	ReentrableLocking withReentranceId(String reentranceId);

	// Delete these methods when LockingDeprecations is removed

	/** {@inheritDoc} */
	@Override
	default ReentrableReadWriteLock forIdentifier(String namespace, String id) {
		return ReentrableLocking.super.forIdentifier(namespace, id);
	}

	/** {@inheritDoc} */
	@Override
	default ReentrableReadWriteLock forEntity(GenericEntity entity) {
		return ReentrableLocking.super.forEntity(entity);
	}

	/** {@inheritDoc} */
	@Override
	default ReentrableReadWriteLock forType(EntityType<?> entityType) {
		return ReentrableLocking.super.forType(entityType);
	}

}
