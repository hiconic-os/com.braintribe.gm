package com.braintribe.gm.graphfetching.processing.fetch;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.braintribe.gm.graphfetching.api.node.EntityGraphNode;
import com.braintribe.gm.graphfetching.api.query.FetchQueryFactory;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;

public interface FetchContext extends AutoCloseable {
	@Override
	void close();

	EntityIdm acquireEntity(GenericEntity entity);
	EntityIdm resolveEntity(EntityType<?> type, Object id);
	PersistenceGmSession session();
	void enqueueToOneIfRequired(EntityGraphNode node, Map<Object, GenericEntity> entities);
	void enqueueToManyIfRequired(EntityGraphNode node, Map<Object, GenericEntity> entities);
	void enqueueToOneIfRequired(EntityGraphNode node, Map<Object, GenericEntity> entities, List<EntityGraphNode> covariants);
	void enqueueToManyIfRequired(EntityGraphNode node, Map<Object, GenericEntity> entities, List<EntityGraphNode> covariants);
	FetchQueryFactory queryFactory();

	<T> void processParallel(Collection<T> tasks, Consumer<T> processor);

	int bulkSize();
}
