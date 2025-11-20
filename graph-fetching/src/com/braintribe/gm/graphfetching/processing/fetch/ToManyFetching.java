package com.braintribe.gm.graphfetching.processing.fetch;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import com.braintribe.gm.graphfetching.api.node.AbstractEntityGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityCollectionPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityGraphNode;
import com.braintribe.gm.graphfetching.api.node.PolymorphicEntityGraphNode;
import com.braintribe.gm.graphfetching.api.node.ScalarCollectionPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.query.FetchJoin;
import com.braintribe.gm.graphfetching.api.query.FetchQuery;
import com.braintribe.gm.graphfetching.api.query.FetchResults;
import com.braintribe.logging.Logger;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.CollectionType;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.LinearCollectionType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.utils.lcd.CollectionTools2;

/**
 * Utility class for (recursive) fetching of to-many (collection) relationships in an entity graph. Splits between entity collections and scalar
 * collections. Uses bulk queries and optional consumer/visitor for processing.
 */
public class ToManyFetching {
	private static final Logger logger = Logger.getLogger(ToManyFetching.class);

	static class EntityMapping {
		public List<EntityGraphNode> exactTypeNodes = new ArrayList<>();
		public List<EntityGraphNode> subTypeNodes = new ArrayList<>();
		public Property property;
		public PolymorphicEntityGraphNode possiblePolyNode;

		public EntityMapping(Property property, PolymorphicEntityGraphNode polymorphicNode) {
			super();
			this.property = property;
			this.possiblePolyNode = polymorphicNode;
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

	private static List<EntityMapping> buildMappings(Collection<EntityCollectionPropertyGraphNode> nodes) {
		List<EntityMapping> mappings = new ArrayList<ToManyFetching.EntityMapping>(nodes.size());
		for (EntityCollectionPropertyGraphNode collectionNode : nodes) {
			AbstractEntityGraphNode abstractEntityNode = collectionNode.entityNode();
			PolymorphicEntityGraphNode possiblePolyNode = abstractEntityNode.isPolymorphic();
			
			EntityMapping mapping = new EntityMapping(collectionNode.property(), possiblePolyNode);
			
			for (EntityGraphNode entityNode: abstractEntityNode.entityNodes()) {
				if (entityNode.entityType() == collectionNode.condensedPropertyType()) {
					mapping.exactTypeNodes.add(entityNode);
				} else {
					mapping.subTypeNodes.add(entityNode);
				}
			}
			
			mappings.add(mapping);
		}

		return mappings;
	}

	private static List<NodePostProcessing> buildPostProcessings(EntityMapping mapping, boolean supportsSubTypeJoin) {
		PolymorphicEntityGraphNode polymorphicNode = mapping.possiblePolyNode;
		boolean polymorphic = polymorphicNode != null;
		
		List<NodePostProcessing> postProcessings = new ArrayList<>();

		if (polymorphic) {
			PostOp subTypeToOneOp = supportsSubTypeJoin? PostOp.mark: PostOp.none;
			
			postProcessings.add(new NodePostProcessing(polymorphicNode, false, PostOp.enqueue, PostOp.enqueue));
			buildSpecificPostProcessings(postProcessings, mapping.exactTypeNodes, false, PostOp.mark, PostOp.mark);
			buildSpecificPostProcessings(postProcessings, mapping.subTypeNodes, true, subTypeToOneOp, PostOp.mark);
		}
		else {
			buildSpecificPostProcessings(postProcessings, mapping.exactTypeNodes, false, PostOp.enqueue, PostOp.enqueue);
			buildSpecificPostProcessings(postProcessings, mapping.subTypeNodes, true, PostOp.enqueue, PostOp.enqueue);
		}

		return postProcessings;
	}

	private static void buildSpecificPostProcessings(List<NodePostProcessing> postProcessings,
			List<EntityGraphNode> nodes, boolean subTypeNode, PostOp toOneOp, PostOp toManyOp) {
		for (EntityGraphNode node : nodes) {
			postProcessings.add(new NodePostProcessing(node, subTypeNode, toOneOp, toManyOp));
		}
	}

	/**
	 * Fetch all to-many properties of the given node (entity and scalar collections).
	 */
	public static void fetch(FetchContext context, AbstractEntityGraphNode node, FetchTask fetchTask) {
		boolean supportsSubTypeJoin = context.queryFactory().supportsSubTypeJoin();
		// entity collections
		for (EntityGraphNode entityNode: node.entityNodes()) {
			Collection<EntityCollectionPropertyGraphNode> entityCollectionProperties = entityNode.entityCollectionProperties().values();
			if (entityCollectionProperties.isEmpty())
				continue;
			
			List<EntityMapping> mappings = buildMappings(entityCollectionProperties);

			for (EntityMapping mapping : mappings) {
				List<NodePostProcessing> postProcessings = buildPostProcessings(mapping, supportsSubTypeJoin);

				Property property = mapping.property;

				fetch(context, node.entityType(), entityNode.entityType(),fetchTask, (LinearCollectionType) property.getType(), property, (GenericEntity e) -> {
					EntityIdm entityIdm = context.acquireEntity(e);
					e = entityIdm.entity;
					
					for (NodePostProcessing postProcessing : postProcessings) {
						postProcessing.process(entityIdm);
					}

					return e;
				});

				for (NodePostProcessing postProcessing : postProcessings) {
					context.enqueueToManyIfRequired(postProcessing.node, postProcessing.toManies);
					context.enqueueToOneIfRequired(postProcessing.node, postProcessing.toOnes);
				}
			}
		}
		
		// scalar collections
		for (EntityGraphNode entityNode: node.entityNodes()) {
			for (ScalarCollectionPropertyGraphNode collectionNode : entityNode.scalarCollectionProperties().values()) {
				fetch(context, node.entityType(), entityNode.entityType(), fetchTask, collectionNode.type(), collectionNode.property(), null);
			}
		}
	}

	/**
	 * Bulk fetching for each collection property in the graph; assigns collections back to owning entities.
	 */
	public static <E> void fetch(FetchContext context, EntityType<?> baseType, EntityType<?> type, FetchTask fetchTask, CollectionType collectionType,
			Property property, Function<E, E> visitor) {
		Map<Object, GenericEntity> entityIndex = fetchTask.entities;

		Collection<Object> allIds = extractTypeSpecificIds(entityIndex, baseType, type, //
				e -> property.set(e, collectionType.createPlain()));
		
		List<Set<Object>> idBulks = CollectionTools2.splitToSets(allIds, context.bulkSize());

		FetchQuery fetchQuery = context.queryFactory().createQuery(type, context.session().getAccessId());
		FetchJoin join = fetchQuery.from().join(property);
		join.orderByIfRequired();

		long queryNanoStart = System.nanoTime();

		context.processParallel(idBulks, ids -> {
			FetchResults results = fetchQuery.fetchFor(ids);

			Object curId = null;
			Collection<E> curCollection = null;
			
			while (results.next()) {
				Object id = results.get(0);
				E element = (E) results.get(1);

				if (!id.equals(curId)) {
					curId = id;
					GenericEntity entity = entityIndex.get(id);
					curCollection = property.get(entity);
				}

				if (visitor != null)
					element = visitor.apply(element);

				curCollection.add(element);
			}
		});

		long queryDuration = System.nanoTime() - queryNanoStart;

		logger.trace(() -> "consumed " + Duration.ofNanos(queryDuration).toMillis() + " ms for querying " + allIds.size() + " entities in "
				+ idBulks.size() + " batches with: " + fetchQuery.stringify());
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
