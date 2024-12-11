// ============================================================================
package com.braintribe.model.processing.session.api.managed;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.value.EntityReference;

import jsinterop.annotations.JsMethod;

/**
 * A view on the cache (typically a Smood) of the {@link ManagedGmSession}.
 */
public interface EntitiesView {

	/**
	 * Returns an unmodifiable {@link Set} or all entities registered in the smood. This method returns a live view on the
	 * data in the smood, which means it does not have almost any memory overhead, but on the other hand it's the caller who
	 * is responsible for synchronization. If some other thread would modify the smood while we are iterating over this
	 * collection, we would get a {@link ConcurrentModificationException}.
	 */
	Collection<GenericEntity> getAllEntities();

	/** Return all instances of given type.. */
	<T extends GenericEntity> Set<T> getEntitiesPerType(EntityType<T> entityType);

	/** Returns entity by it's <tt>type</tt> and <tt>id</tt>. If no entity is found, exception is thrown. */
	<T extends GenericEntity> T getEntity(EntityType<T> entityType, Object id);

	/** Returns entity by corresponding <tt>reference</tt>. If no entity is found, exception is thrown. */
	@JsMethod(name = "getEntityByReference")
	<T extends GenericEntity> T getEntity(EntityReference entityReference);

	/** Returns entity by it's <tt>type</tt> and <tt>id</tt>. If no entity is found, <tt>null</tt> is returned. */
	<T extends GenericEntity> T findEntity(EntityType<T> entityType, Object id);

	/** Returns entity by corresponding <tt>reference</tt>. If no entity is found, <tt>null</tt> is returned. */
	@JsMethod(name = "findEntityByReference")
	<T extends GenericEntity> T findEntity(EntityReference reference);

	/** Returns entity by corresponding <tt>globalId</tt>. If no entity is found, <tt>null</tt> is returned. */
	<T extends GenericEntity> T findEntityByGlobalId(String globalId);

}
