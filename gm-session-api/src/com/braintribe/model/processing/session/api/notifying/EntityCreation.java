package com.braintribe.model.processing.session.api.notifying;

import com.braintribe.model.generic.GenericEntity;

public interface EntityCreation<T extends GenericEntity> {

	EntityCreation<T> raw();

	/**
	 * Specifies the new entity's partition.
	 * <p>
	 * NOTE that this value is set before manipulation tracking is on and the instantiation manipulation will be notified with this value already set.
	 */
	EntityCreation<T> withPartition(String partition);

	/** Equivalent to calling {@link #global(String)} with a random UUID as a parameter. */
	T globalWithRandomUuid();

	/**
	 * Creates a new entity with the specified globalId.
	 * <p>
	 * NOTE that the globalId value is set before manipulation tracking is on and the instantiation manipulation will be notified with this value
	 * already set.
	 */
	T global(String globalId);

	/**
	 * Creates a new entity with the specified id.
	 * <p>
	 * NOTE that the id value is set before manipulation tracking is on and the instantiation manipulation will be notified with this value already
	 * set.
	 */
	T persistent(Object id);

	T preliminary();

}
