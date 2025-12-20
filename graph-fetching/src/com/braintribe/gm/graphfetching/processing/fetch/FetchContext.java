package com.braintribe.gm.graphfetching.processing.fetch;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import com.braintribe.gm.graphfetching.api.node.AbstractEntityGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityRelatedPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.MapPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.ScalarCollectionPropertyGraphNode;
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
	void enqueueFlatIfRequired(AbstractEntityGraphNode node, Map<Object, GenericEntity> entities);
	void enqueue(FetchTask fetchTask);
	
	FetchQueryFactory queryFactory();

	int bulkSize();

	int toOneSelectCountStopThreshold();
	
	double defaultJoinProbability();

	double joinProbabiltyThreshold();
	double toOneJoinThreshold();
	
	boolean polymorphicJoin();

	CompletableFuture<Map<Object, GenericEntity>> fetchEntities(AbstractEntityGraphNode node, Set<Object> ids, Consumer<EntityIdm> visitor);

	CompletableFuture<Void> fetchPropertyEntities(EntityGraphNode entityNode, EntityRelatedPropertyGraphNode propertyNode, Map<Object, GenericEntity> entities,
			Consumer<EntityIdm> visitor);

	CompletableFuture<Void> fetchScalarPropertyCollections(EntityGraphNode entityNode, ScalarCollectionPropertyGraphNode propertyNode,
			Map<Object, GenericEntity> entities);

	<T> CompletableFuture<Void> processElements(Collection<T> tasks, Consumer<T> processor);

	void notifyError(Throwable ex);

	void observe(CompletableFuture<?> future);

	CompletableFuture<Void> fetchMap(EntityGraphNode entityNode, MapPropertyGraphNode propertyNode,
			Map<Object, GenericEntity> entities, Consumer<EntityIdm> keyVisitor, Consumer<EntityIdm> valueVisitor);

}
