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
