package com.braintribe.gm.graphfetching.processing.fetch;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

import com.braintribe.gm.graphfetching.api.node.AbstractEntityGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityCollectionPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityGraphNode;
import com.braintribe.gm.graphfetching.api.node.MapPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.PolymorphicEntityGraphNode;
import com.braintribe.gm.graphfetching.api.node.ScalarCollectionPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.query.FetchJoin;
import com.braintribe.gm.graphfetching.api.query.FetchQuery;
import com.braintribe.gm.graphfetching.api.query.FetchResults;
import com.braintribe.gm.graphfetching.processing.util.FetchingTools;
import com.braintribe.logging.Logger;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.CollectionType;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.MapType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.utils.lcd.CollectionTools2;

/**
 * Utility class for (recursive) fetching of to-many (collection) relationships in an entity graph. Splits between entity collections and scalar
 * collections. Uses bulk queries and optional consumer/visitor for processing.
 */
public class ToManyFetching {
	private static final Logger logger = Logger.getLogger(ToManyFetching.class);
	
	static class CollectionFetchPlan {
		EntityMapping keyMapping;
		EntityMapping valueMapping;
		CollectionType collectionType;
		EntityGraphNode entityNode;
		Property property;
		public CollectionFetchPlan(EntityGraphNode entityNode, Property property, CollectionType collectionType) {
			super();
			this.entityNode = entityNode;
			this.property = property;
			this.collectionType = collectionType;
		}
		
		public Function<GenericEntity, GenericEntity> keyVisitor() {
			return keyMapping != null? keyMapping::visit: null;
		}
		
		public Function<GenericEntity, GenericEntity> valueVisitor() {
			return valueMapping != null? valueMapping::visit: null;
		}
		
		public void postProcess() {
			if (keyMapping != null)
				keyMapping.postProcess();
			if (valueMapping != null)
				valueMapping.postProcess();
		}
	}

	static class EntityMapping {
		private boolean polymorphicJoin;

		public List<EntityGraphNode> exactTypeNodes = new ArrayList<>();
		public List<EntityGraphNode> subTypeNodes = new ArrayList<>();
		public Property property;
		public PolymorphicEntityGraphNode possiblePolyNode;
		public List<NodePostProcessing> postProcessings;
		private FetchContext context;

		public EntityMapping(FetchContext context, Property property, PolymorphicEntityGraphNode polymorphicNode, boolean supportsSubTypeJoin) {
			super();
			this.context = context;
			this.property = property;
			this.possiblePolyNode = polymorphicNode;
			this.polymorphicJoin = supportsSubTypeJoin;
		}
		
		public void initPostProcessings() {
			PolymorphicEntityGraphNode polymorphicNode = possiblePolyNode;
			boolean polymorphic = polymorphicNode != null;
			
			postProcessings = new ArrayList<>();

			if (polymorphic) {
				PostOp subTypeToOneOp = polymorphicJoin? PostOp.mark: PostOp.none;
				
				postProcessings.add(new NodePostProcessing(polymorphicNode, false, PostOp.enqueue, PostOp.enqueue));
				buildSpecificPostProcessings(postProcessings, exactTypeNodes, false, PostOp.mark, PostOp.mark);
				buildSpecificPostProcessings(postProcessings, subTypeNodes, true, subTypeToOneOp, PostOp.mark);
			}
			else {
				buildSpecificPostProcessings(postProcessings, exactTypeNodes, false, PostOp.enqueue, PostOp.enqueue);
				buildSpecificPostProcessings(postProcessings, subTypeNodes, true, PostOp.enqueue, PostOp.enqueue);
			}
		}
		
		public GenericEntity visit(GenericEntity e) {
			EntityIdm entityIdm = context.acquireEntity(e);
			e = entityIdm.entity;
			
			for (NodePostProcessing postProcessing : postProcessings) {
				postProcessing.process(entityIdm);
			}

			return e;
		}
		
		public void postProcess() {
			for (NodePostProcessing postProcessing : postProcessings) {
				context.enqueueToManyIfRequired(postProcessing.node, postProcessing.toManies);
				context.enqueueToOneIfRequired(postProcessing.node, postProcessing.toOnes);
			}
		}
		
	}
	
	enum PostOp { none, mark, enqueue } 

	static class NodePostProcessing {
		public final AbstractEntityGraphNode node;
		public final Map<Object, GenericEntity> toOnes = new ConcurrentHashMap<>();
		public final Map<Object, GenericEntity> toManies = new ConcurrentHashMap<>();
		public final boolean isSubTypeNode;
		private final PostOp toOneOp;
		private final PostOp toManyOp;

		public NodePostProcessing(AbstractEntityGraphNode node, boolean isSubTypeNode, PostOp toOneOp, PostOp toManyOp) {
			super();
			this.node = node;
			this.isSubTypeNode = isSubTypeNode;
			this.toOneOp = toOneOp;
			this.toManyOp = toManyOp;
		}
		
		public void process(EntityIdm entityIdm) {
			GenericEntity e = entityIdm.entity;
			if (isSubTypeNode && !node.entityType().isInstance(e))
				return;

			if (toOneOp != PostOp.none)
				if (entityIdm.addHandled(node.toOneQualification()))
					if (toOneOp == PostOp.enqueue)
						toOnes.put(e.getId(), e);

			if (toManyOp != PostOp.none)
				if (entityIdm.addHandled(node.toManyQualification()))
					if (toManyOp == PostOp.enqueue)
						toManies.put(e.getId(), e);
		}
		
		@Override
		public String toString() {
			return node.entityType().getShortName() + "[toOneOp=" +  toOneOp + ", toManyOp=" + toManyOp + "]";  
		}
	}

	private static List<CollectionFetchPlan> buildPlans(FetchContext context, AbstractEntityGraphNode node, boolean supportsSubTypeJoin) {
		
		List<CollectionFetchPlan> plans = new ArrayList<>();
		
		for (EntityGraphNode entityNode: node.entityNodes()) {
			for (ScalarCollectionPropertyGraphNode scalarCollectionNode :entityNode.scalarCollectionProperties().values()) {
				CollectionFetchPlan plan = new CollectionFetchPlan(entityNode, scalarCollectionNode.property(), scalarCollectionNode.type());
				plans.add(plan);
			}
			
			for (EntityCollectionPropertyGraphNode entityCollectionNode: entityNode.entityCollectionProperties().values()) {
				EntityType<?> baseType = (EntityType<?>) entityCollectionNode.condensedPropertyType();
				Property property = entityCollectionNode.property();
				
				AbstractEntityGraphNode elementEntityNode = entityCollectionNode.entityNode();
				EntityMapping mapping = buildMapping(context, baseType, property, elementEntityNode, elementEntityNode.isPolymorphic(), supportsSubTypeJoin);
				
				CollectionFetchPlan plan = new CollectionFetchPlan(entityNode, property, entityCollectionNode.type());
				plan.valueMapping = mapping;
				plans.add(plan);
			}
			
			for (MapPropertyGraphNode mapNode: entityNode.mapProperties().values()) {
				MapType mapType = mapNode.type();
				Property property = mapNode.property();
				
				GenericModelType keyType = mapType.getKeyType();
				GenericModelType valueType = mapType.getValueType();
				
				CollectionFetchPlan plan = new CollectionFetchPlan(entityNode, property, mapType);
				plan.property = property;
				
				if (keyType.isEntity()) {
					EntityType<?> baseKeyType = (EntityType<?>) keyType;
					AbstractEntityGraphNode keyEntityNode = mapNode.keyNode();
					
					EntityMapping mapping = buildMapping(context, baseKeyType, property, keyEntityNode, keyEntityNode.isPolymorphic(), supportsSubTypeJoin);
					plan.keyMapping = mapping;
				}
				
				if (valueType.isEntity()) {
					EntityType<?> baseValueType = (EntityType<?>) valueType;
					AbstractEntityGraphNode valueEntityNode = mapNode.valueNode();
					
					EntityMapping mapping = buildMapping(context, baseValueType, property, valueEntityNode, valueEntityNode.isPolymorphic(), supportsSubTypeJoin);
					plan.valueMapping = mapping;
				}
					
				plans.add(plan);
			}
		}
		
		return plans;
	}
	
	private static EntityMapping buildMapping(FetchContext context, EntityType<?> baseType, Property property, AbstractEntityGraphNode abstractEntityNode,
			PolymorphicEntityGraphNode possiblePolyNode, boolean supportsSubTypeJoin) {
		EntityMapping mapping = new EntityMapping(context, property, possiblePolyNode, supportsSubTypeJoin);
		
		for (EntityGraphNode entityNode: abstractEntityNode.entityNodes()) {
			if (entityNode.entityType() == baseType) {
				mapping.exactTypeNodes.add(entityNode);
			} else {
				mapping.subTypeNodes.add(entityNode);
			}
		}
		
		mapping.initPostProcessings();
		return mapping;
	}
	
	private static void buildSpecificPostProcessings(List<NodePostProcessing> postProcessings,
			List<EntityGraphNode> nodes, boolean subTypeNode, PostOp toOneOp, PostOp toManyOp) {
		for (EntityGraphNode node : nodes) {
			postProcessings.add(new NodePostProcessing(node, subTypeNode, toOneOp, toManyOp));
		}
	}

	/**
	 * Fetch all to-many properties of the given node (entity and scalar collections).
	 * @return 
	 */
	public static CompletableFuture<Void> fetch(FetchContext context, AbstractEntityGraphNode node, FetchTask fetchTask) {
		boolean supportsSubTypeJoin = context.queryFactory().supportsSubTypeJoin() && context.polymorphicJoin();
		
		List<CollectionFetchPlan> plans = buildPlans(context, node, supportsSubTypeJoin);
		List<CompletableFuture<Void>> futures = new ArrayList<>(plans.size());
		for (CollectionFetchPlan plan: plans) {
			Property property = plan.property;
			Function<GenericEntity, GenericEntity> keyVisitor = plan.keyVisitor();
			Function<GenericEntity, GenericEntity> valueVisitor = plan.valueVisitor();
			
			futures.add(fetch(context, node.entityType(), plan, fetchTask, property, keyVisitor, valueVisitor));
		}
		
		return FetchingTools.futureOf(futures);
	}

	/**
	 * Bulk fetching for each collection property in the graph; assigns collections back to owning entities.
	 */
	public static <E, K> CompletableFuture<Void> fetch(FetchContext context, EntityType<?> baseType, CollectionFetchPlan plan, FetchTask fetchTask,
			Property property, Function<K, K> keyVisitor, Function<E, E> valueVisitor) {
		EntityType<?> type = plan.entityNode.entityType();
		CollectionType collectionType = plan.collectionType;
		Map<Object, GenericEntity> entityIndex = fetchTask.entities;
		
		Map<Object, Object> collections = new HashMap<>();
		
		Collection<Object> allIds = extractTypeSpecificIds(entityIndex, baseType, type, //
				e -> {
					Object collection = collectionType.createPlain();
					property.set(e, collection);
					collections.put(e.getId(), collection);
				});
		
		List<Set<Object>> idBulks = CollectionTools2.splitToSets(allIds, context.bulkSize());

		FetchQuery fetchQuery = context.queryFactory().createQuery(type, context.session().getAccessId());
		FetchJoin join = fetchQuery.from().join(property);
		join.orderByIfRequired();

		long queryNanoStart = System.nanoTime();
		
		return context.processElements(idBulks, ids -> {
			try (FetchResults results = fetchQuery.fetchFor(ids)) {
				CollectionAdder collectionAdder = createCollectionAdder(collectionType, keyVisitor, valueVisitor);
				Object curId = null;
				
				while (results.next()) {
					Object id = results.get(0);
	
					if (!id.equals(curId)) {
						curId = id;
						Object collection = collections.get(id);
						collectionAdder.setCollection(collection);
					}

					collectionAdder.addAndNotify(results);
				}
			}
		}).thenRun(() -> {
			long queryDuration = System.nanoTime() - queryNanoStart;

			logger.trace(() -> "consumed " + Duration.ofNanos(queryDuration).toMillis() + " ms for querying " + allIds.size() + " entities in "
					+ idBulks.size() + " batches with: " + fetchQuery.stringify());
			
			plan.postProcess();
		});
	}
	
	private static <K, V> CollectionAdder createCollectionAdder(CollectionType collectionType, Function<K, K> keyVisitor, Function<V,V> valueVisitor) {
		switch (collectionType.getCollectionKind()) {
		case list:
		case set:
			return new LinearCollectionAdder<V>(valueVisitor);
		case map:
			return new MapAdder<K,V>(keyVisitor, valueVisitor);
		default:
			throw new IllegalStateException("unexpected collection kind");
		}
	}
	
	private static abstract class CollectionAdder {
		public abstract void addAndNotify(FetchResults fr);
		public abstract void setCollection(Object collection);
	}
	
	private static abstract class AbstractCollectionAdder<V> extends CollectionAdder {
		protected Function<V,V> valueVisitor;
		
		public AbstractCollectionAdder(Function<V, V> valueVisitor) {
			super();
			this.valueVisitor = valueVisitor;
		}
	}
	
	private static class LinearCollectionAdder<V> extends AbstractCollectionAdder<V> {
		private Collection<V> collection;
		
		public LinearCollectionAdder(Function<V, V> valueVisitor) {
			super(valueVisitor);
		}
		
		@Override
		public void setCollection(Object collection) {
			this.collection = (Collection<V>) collection;
		}
		
		@Override
		public void addAndNotify(FetchResults fr) {
			V v = (V) fr.get(1);

			if (v != null && valueVisitor != null)
				v = valueVisitor.apply(v);

			collection.add(v);
		}
	}
	
	private static class MapAdder<K, V> extends AbstractCollectionAdder<V> {

		private Function<K, K> keyVisitor;
		private Map<K, V> map;

		public MapAdder(Function<K, K> keyVisitor, Function<V, V> valueVisitor) {
			super(valueVisitor);
			this.keyVisitor = keyVisitor;
		}
		
		@Override
		public void setCollection(Object collection) {
			this.map = (Map<K, V>) collection;
		}
		
		@Override
		public void addAndNotify(FetchResults fr) {
			K k = (K) fr.get(1);
			
			if (k != null && keyVisitor != null)
				k = keyVisitor.apply(k);

			V v = (V) fr.get(2);

			if (v != null && valueVisitor != null)
				v = valueVisitor.apply(v);

			map.put(k, v);
		}
		
	}

	private static Collection<Object> extractTypeSpecificIds(Map<Object, GenericEntity> entityIndex, EntityType<?> baseType, EntityType<?> type, Consumer<GenericEntity> specificEntityVisitor) {
		if (type == baseType) {
			for (GenericEntity entity: entityIndex.values())
				specificEntityVisitor.accept(entity);
			
			return entityIndex.keySet();
		}
		
		List<Object> resultIds = new ArrayList<Object>();
		for (Map.Entry<Object, GenericEntity> entry: entityIndex.entrySet()) {
			GenericEntity entity = entry.getValue();
			
			if (type.isInstance(entity)) {
				specificEntityVisitor.accept(entity);
				resultIds.add(entry.getKey());
			}
		}
		
		return resultIds;
	}
}
