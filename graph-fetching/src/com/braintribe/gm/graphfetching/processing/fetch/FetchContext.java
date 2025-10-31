package com.braintribe.gm.graphfetching.processing.fetch;

import java.util.Map;

import com.braintribe.gm.graphfetching.api.node.EntityGraphNode;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;

public interface FetchContext {
	EntityIdm acquireEntity(GenericEntity entity);
	EntityIdm resolveEntity(EntityType<?> type, Object id);
	PersistenceGmSession session();
	void enqueueToOneIfRequired(EntityGraphNode node, Map<Object, GenericEntity> entities);
	void enqueueToManyIfRequired(EntityGraphNode node, Map<Object, GenericEntity> entities);
}
