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

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.session.GmSession;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;

/**
 * @see Locking
 * 
 * @author peter.gazdik
 */
public interface ReentrableLocking {

	/**
	 * Creates a {@link ReentrableReadWriteLock} with given identifier.
	 */
	ReentrableReadWriteLock forIdentifier(String id);

	/**
	 * Equivalent to {@code forIdentifier(namespace + ":" + id)}
	 */
	default ReentrableReadWriteLock forIdentifier(String namespace, String id) {
		return forIdentifier(namespace + ":" + id);
	}

	/**
	 * Creates a String s representing given entity and calls {@code forIdentifier("entity", s)}
	 */
	default ReentrableReadWriteLock forEntity(GenericEntity entity) {
		StringBuilder builder = new StringBuilder();
		EntityType<?> entityType = entity.entityType();
		builder.append(entityType.getTypeSignature());

		Object id = entity.getId();
		if (id != null) {
			builder.append('[');
			builder.append(id.toString());
			builder.append(']');

			GmSession session = entity.session();
			if (session instanceof PersistenceGmSession) {
				PersistenceGmSession persistenceGmSession = (PersistenceGmSession) session;
				String accessId = persistenceGmSession.getAccessId();
				if (accessId != null) {
					builder.append('@');
					builder.append(accessId);
				}
			}
		}

		String identifier = builder.toString();
		return forIdentifier("entity", identifier);
	}

	/**
	 * Equivalent to {@code forIdentifier("entity-type", entityType.getTypeSignature())}
	 */
	default ReentrableReadWriteLock forType(EntityType<?> entityType) {
		return forIdentifier("entity-type", entityType.getTypeSignature());
	}

}
