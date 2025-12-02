package com.braintribe.gm.graphfetching.processing.fetch;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

import com.braintribe.gm.graphfetching.api.node.AbstractEntityGraphNode;
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
	void enqueueToOneIfRequired(AbstractEntityGraphNode node, Map<Object, GenericEntity> entities);
	void enqueueToManyIfRequired(AbstractEntityGraphNode node, Map<Object, GenericEntity> entities);
	FetchQueryFactory queryFactory();

	<T> void processParallel(Collection<T> tasks, Consumer<T> processor);

	int bulkSize();

	int toOneSelectCountStopThreshold();
	
	double defaultJoinProbability();

	double joinProbabiltyThreshold();
	
	boolean polymorphicJoin();
}
