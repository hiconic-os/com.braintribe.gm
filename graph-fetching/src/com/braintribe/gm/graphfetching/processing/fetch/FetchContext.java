package com.braintribe.gm.graphfetching.processing.fetch;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import com.braintribe.gm.graphfetching.api.node.AbstractEntityGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityRelatedPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.query.FetchQueryFactory;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;

public interface FetchContext extends AutoCloseable {
	@Override
	void close();

	EntityIdm acquireEntity(GenericEntity entity);
	void putEntity(EntityType<?> baseType, EntityIdm entityIdm);
	
	EntityIdm resolveEntity(EntityType<?> type, Object id);
	PersistenceGmSession session();
	void enqueueToOneIfRequired(AbstractEntityGraphNode node, Map<Object, GenericEntity> entities);
	void enqueueToManyIfRequired(AbstractEntityGraphNode node, Map<Object, GenericEntity> entities);
	FetchQueryFactory queryFactory();

	<T> void processParallel(Collection<T> tasks, Consumer<T> processor, Runnable onDone);

	int bulkSize();

	int toOneSelectCountStopThreshold();
	
	double defaultJoinProbability();

	double joinProbabiltyThreshold();
	double toOneJoinThreshold();
	
	boolean polymorphicJoin();

	void fetchEntities(AbstractEntityGraphNode node, Set<Object> ids, Consumer<Map<Object, GenericEntity>> receiver, Consumer<EntityIdm> visitor);

	void fetchPropertyEntities(EntityGraphNode entityNode, EntityRelatedPropertyGraphNode propertyNode, Map<Object, GenericEntity> entities,
			Consumer<EntityIdm> visitor, Runnable onDone);
}
