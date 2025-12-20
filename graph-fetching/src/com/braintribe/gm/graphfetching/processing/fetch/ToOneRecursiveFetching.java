package com.braintribe.gm.graphfetching.processing.fetch;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.braintribe.gm.graphfetching.api.node.AbstractEntityGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.PolymorphicEntityGraphNode;
import com.braintribe.gm.graphfetching.api.query.FetchQuery;
import com.braintribe.gm.graphfetching.api.query.FetchQueryFactory;
import com.braintribe.gm.graphfetching.api.query.FetchResults;
import com.braintribe.gm.graphfetching.api.query.FetchSource;
import com.braintribe.logging.Logger;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.utils.lcd.CollectionTools2;

/**
 * Core implementation of recursive (deep) fetching of to-one entity references for a fetch graph. Builds join/selection maps dynamically, autowires
 * recursively for each subnode. Batches queries for efficiency. Usage is internal to FetchProcessing.
 */
public class ToOneRecursiveFetching {
	private static Logger logger = Logger.getLogger(ToOneRecursiveFetching.class);

	// select c.id, e, a, c from Company c join c.employees e join e.address a join a.city c

	private abstract class EntityMapping {
		protected List<FetchedEntityMapping> wirings = new ArrayList<>();
		protected int pos;
		protected FetchSource source;
		protected boolean recursionStop = false;
		public Property property;
		public final List<EntityGraphNode> joinableNodes = new ArrayList<>();
		public final List<EntityGraphNode> nonJoinableSubTypeNodes = new ArrayList<>();
		public PolymorphicEntityGraphNode possiblePolyNode;
		public double probability;
		public int polymorphicCount = 0; 

		public EntityMapping(FetchSource source) {
			super();
			this.pos = source.pos();
			this.source = source;
		}

		public void addJoinable(EntityGraphNode subNode) {
			joinableNodes.add(subNode);
		}

		public void addNonJoinable(EntityGraphNode subNode) {
			nonJoinableSubTypeNodes.add(subNode);
		}

		public int pos() {
			return pos;
		}

		public boolean isRecursionStop() {
			return recursionStop;
		}

		public void setRecursionStop(boolean recursionStop) {
			this.recursionStop = recursionStop;
		}

		public FetchSource source() {
			return source;
		}

		public List<FetchedEntityMapping> wirings() {
			return wirings;
		}

		public abstract EntityType<?> entityType();
	}

	private class ExistingEntityMapping extends EntityMapping {

		private AbstractEntityGraphNode node;

		public ExistingEntityMapping(AbstractEntityGraphNode node, FetchSource source) {
			super(source);
			this.node = node;
			this.probability = 1;
			
			EntityType<?> baseType = node.entityType();
			
			for (EntityGraphNode entityNode: node.entityNodes()) {
				boolean polymorphic = entityNode.entityType() == baseType;
				if (supportsSubTypeJoin || polymorphic) {
					addJoinable(entityNode);
					if (polymorphic)
						polymorphicCount++;
				}
				else
					addNonJoinable(entityNode);
			}
		}

		@Override
		public EntityType<?> entityType() {
			return node.entityType();
		}
	}

	private class FetchedEntityMapping extends EntityMapping {

		private EntityType<?> entityType;

		public FetchedEntityMapping(Property property, FetchSource source, double probability) {
			super(source);
			this.probability = probability;
			this.property = property;
			this.entityType = (EntityType<?>) property.getType();
		}

		public Property property() {
			return property;
		}

		@Override
		public EntityType<?> entityType() {
			return entityType;
		}
	}

	private final List<EntityMapping> mappings = new ArrayList<>();
	private final FetchQuery fetchQuery;
	private final boolean supportsSubTypeJoin;
	private final AbstractEntityGraphNode rootNode;
	private final ExistingEntityMapping rootMapping;
	private int selectCount = 0;
	private int selectCountStopThreshold;
	private double defaultJoinProbability;
	private double joinProbabilityThreshold;
	private double toOneJoinThreshold;
	private int joinCount = 0;

	public ToOneRecursiveFetching(FetchContext context, AbstractEntityGraphNode node) {
		selectCountStopThreshold = context.toOneSelectCountStopThreshold();
		defaultJoinProbability = context.defaultJoinProbability();
		joinProbabilityThreshold = context.joinProbabiltyThreshold();
		toOneJoinThreshold = context.toOneJoinThreshold();
		rootNode = node;
		CyclicRecurrenceCheck check = new CyclicRecurrenceCheck();

		FetchQueryFactory queryFactory = context.queryFactory();

		supportsSubTypeJoin = queryFactory.supportsSubTypeJoin() && context.polymorphicJoin();
		
		fetchQuery = queryFactory.createQuery(node.entityType(), context.session().getAccessId());
		
		FetchSource from = fetchQuery.from();
		rootMapping = new ExistingEntityMapping(node, from);

		registerMappingWithJoins(rootMapping, check);
	}

	public CompletableFuture<Void> fetch(FetchContext context, FetchTask task) {
		// if there is no sub type joining we need to enqueue subtype nodes specifically for TO_ONE
		if (!supportsSubTypeJoin) {
			for (EntityGraphNode entityNode : rootNode.entityNodes()) {
				EntityType<?> entityType = entityNode.entityType();
				if (entityType == rootNode.entityType())
					continue;

				// enqueue non joinable for TO_ONE
				Map<Object, GenericEntity> enqueuedEntities = new LinkedHashMap<>();
				for (GenericEntity entity : task.entities.values()) {
					if (!entityType.isInstance(entity))
						continue;

					EntityIdm entityIdm = context.acquireEntity(entity);
					if (entityIdm.addHandled(entityNode.toOneQualification())) {
						enqueuedEntities.put(entity.getId(), entity);
					}
				}
				context.enqueueToOneIfRequired(entityNode, enqueuedEntities);
			}
		}

		if (mappings.size() == 1) {
			return CompletableFuture.completedFuture(null);
		}

		ResultWiringContext wiringContext = new ResultWiringContext(context, task.entities);

		Set<Object> allIds = task.entities.keySet();

		// query joined entities in bulks
		List<Set<Object>> idBulks = CollectionTools2.splitToSets(allIds, context.bulkSize());

		long nanoStart = System.nanoTime();

		return context.processElements(idBulks, ids -> {
			try (FetchResults results = fetchQuery.fetchFor(ids)) {
				wiringContext.handleRows(results);
			}
		}).thenRun(() -> {
			Duration duration = Duration.ofNanos(System.nanoTime() - nanoStart);
			logger.trace(() -> "consumed " + duration.toMillis() + " ms for querying " + allIds.size() + " entities in " + idBulks.size()
					+ " batches with: " + fetchQuery.stringify());
			
			for (Map.Entry<AbstractEntityGraphNode, Map<Object, GenericEntity>> entry : wiringContext.postProcessing.toManies.entrySet()) {
				context.enqueueToManyIfRequired(entry.getKey(), entry.getValue());
			}

			for (Map.Entry<AbstractEntityGraphNode, Map<Object, GenericEntity>> entry : wiringContext.postProcessing.toOnes.entrySet()) {
				context.enqueueToOneIfRequired(entry.getKey(), entry.getValue());
			}
		});
	}

	/**
	 * This method handles the following cases in the following ways:
	 * 
	 * <h1>1. no poly node</h2>
	 * 
	 * <p>
	 * <b>Note:</b> In that case there would be either exactly one joinable node or exactly one non-joinable subtype node as there is only one entity
	 * node attachable to the property node
	 * </p>
	 * 
	 * <ul>
	 * <li>enqueue TO_MANY for joinable nodes
	 * <li>enqueue TO_MANY for non-joinable subtype nodes
	 * <li>enqueue TO_ONE for non-joinable subtype nodes
	 * </ul>
	 * <h1>2. poly node with non-joinable nodes</h1>
	 * 
	 * <p>
	 * <b>Note:</b> In that case there can be multiple joinable nodes and multiple non-joinable subtype nodes as a poly node can hold many entity
	 * nodes of both kinds
	 * </p>
	 * <ul>
	 * <li>mark TO_ONE for the poly node
	 * <li>enqueue TO_MANY for poly node
	 * <li>mark TO_MANY for joinable nodes
	 * <li>mark TO_MANY for non-joinable subtype nodes
	 * <li>enqueue TO_ONE for non-joinable subtype nodes
	 * </ul>
	 * 
	 * <h1>2. poly node without non-joinable nodes</h2>
	 * 
	 * <p>
	 * <b>Note:</b> In that case there can be multiple joinable nodes and no non-joinable subtype nodes as the cast support can join all types
	 * </p>
	 * 
	 * <ul>
	 * <li>enqueue TO_ONE for the poly node
	 * <li>enqueue TO_MANY for the poly node
	 * <li>mark TO_ONE for joinable nodes
	 * <li>mark TO_MANY for joinable nodes
	 * </ul>
	 */

	private List<NodePostProcessing> buildNodePostProcessings(PostProcessing postProcessing, EntityMapping mapping) {
		List<NodePostProcessing> processings = new ArrayList<>();

		final boolean toOneRecursionStop = mapping.isRecursionStop();

		final boolean withPolyNode = mapping.possiblePolyNode != null;
		final boolean withNonJoinableNodes = !mapping.nonJoinableSubTypeNodes.isEmpty();

		final boolean enqueuePolyToOne;
		final boolean enqueuePolyToMany;
		final boolean enqueueJoinableToOne;
		final boolean enqueueJoinableToMany;
		final boolean enqueueNonJoinableToOne;
		final boolean enqueueNonJoinableToMany;

		if (!withPolyNode) {
			enqueuePolyToOne = false;
			enqueuePolyToMany = false;
			enqueueJoinableToOne = toOneRecursionStop;
			enqueueJoinableToMany = true;
			enqueueNonJoinableToOne = true;
			enqueueNonJoinableToMany = true;
		} else if (withNonJoinableNodes) {
			enqueuePolyToOne = false;
			enqueuePolyToMany = true;
			enqueueJoinableToOne = toOneRecursionStop;
			enqueueJoinableToMany = false;
			enqueueNonJoinableToOne = true;
			enqueueNonJoinableToMany = false;
		} else {
			enqueuePolyToOne = toOneRecursionStop;
			enqueuePolyToMany = true;
			enqueueJoinableToOne = false;
			enqueueJoinableToMany = false;
			enqueueNonJoinableToOne = false;
			enqueueNonJoinableToMany = false;
		}

		if (withPolyNode)
			processings.add(new NodePostProcessing(postProcessing, mapping.possiblePolyNode, enqueuePolyToOne, enqueuePolyToMany, false));

		for (EntityGraphNode node : mapping.joinableNodes) {
			boolean subTypeNode = mapping.property.getType() != node.entityType();
			processings.add(new NodePostProcessing(postProcessing, node, enqueueJoinableToOne, enqueueJoinableToMany, subTypeNode));
		}

		for (EntityGraphNode node : mapping.nonJoinableSubTypeNodes)
			processings.add(new NodePostProcessing(postProcessing, node, enqueueNonJoinableToOne, enqueueNonJoinableToMany, true));

		return processings;
	}

	private static class PostProcessing {
		private Map<AbstractEntityGraphNode, Map<Object, GenericEntity>> toManies = new LinkedHashMap<>();
		private Map<AbstractEntityGraphNode, Map<Object, GenericEntity>> toOnes = new LinkedHashMap<>();

		public Map<Object, GenericEntity> acquireToManies(AbstractEntityGraphNode node) {
			return toManies.computeIfAbsent(node, k -> new ConcurrentHashMap<>());
		}
		public Map<Object, GenericEntity> acquireToOnes(AbstractEntityGraphNode node) {
			return toOnes.computeIfAbsent(node, k -> new ConcurrentHashMap<>());
		}
	}

	private static class NodePostProcessing {
		private final Map<Object, GenericEntity> toManies;
		private final Map<Object, GenericEntity> toOnes;
		private final AbstractEntityGraphNode node;
		private final boolean isSubTypeNode;
		private final boolean enqueueToMany;
		private final boolean enqueueToOne;

		public NodePostProcessing(PostProcessing postProcessing, AbstractEntityGraphNode node, boolean enqueueToOne, boolean enqueueToMany,
				boolean isSubTypeNode) {
			super();
			this.node = node;
			this.enqueueToOne = enqueueToOne;
			this.enqueueToMany = enqueueToMany;
			this.isSubTypeNode = isSubTypeNode;
			this.toOnes = postProcessing.acquireToOnes(node);
			this.toManies = postProcessing.acquireToManies(node);
		}

		public void handle(EntityIdm entityIdm) {
			GenericEntity entity = entityIdm.entity;

			if (isSubTypeNode && !node.entityType().isInstance(entity))
				return;

			if (entityIdm.addHandled(node.toOneQualification()) && enqueueToOne)
				toOnes.put(entity.getId(), entity);

			if (entityIdm.addHandled(node.toManyQualification()) && enqueueToMany)
				toManies.put(entity.getId(), entity);
		}
	}

	private static class CyclicRecurrenceCheck {
		private Map<Property, AtomicInteger> recurrences = new IdentityHashMap<>();
		private int limit = 3;

		public boolean add(Property property) {
			AtomicInteger counter = recurrences.computeIfAbsent(property, k -> new AtomicInteger(0));

			counter.incrementAndGet();

			return counter.get() > limit;
		}

		public void remove(Property property) {
			recurrences.compute(property, (n, c) -> {
				if (c == null || c.get() == 0)
					throw new IllegalStateException("unexpected remove");

				int counter = c.decrementAndGet();

				return counter == 0 ? null : c;
			});
		}
	}

	/**
	 * Resolves/assigns entity/graph nodes recursively; creates mappings for joined properties.
	 */
	private void registerMappingWithJoins(EntityMapping refererMapping, CyclicRecurrenceCheck check) {
		mappings.add(refererMapping);
		
		if (joinCount > toOneJoinThreshold || selectCount >= selectCountStopThreshold || refererMapping.probability < joinProbabilityThreshold) {
			refererMapping.setRecursionStop(true);
			return;
		}
		
		selectCount += refererMapping.source().scalarCount();

		boolean mappingsSizeExceeded = mappings.size() > 20;
		boolean recursionStop = check.add(refererMapping.property) || mappingsSizeExceeded;

		try {
			refererMapping.setRecursionStop(recursionStop);

			if (recursionStop)
				return;

			List<FetchedEntityMapping> newMappings = new ArrayList<>(refererMapping.joinableNodes.size());
			
			double localProbability = defaultJoinProbability;
			
			if (refererMapping.polymorphicCount > 0) {
				localProbability /= refererMapping.polymorphicCount;
			}
			
			for (EntityGraphNode joinableNode : refererMapping.joinableNodes) {
				boolean subTypeJoin = refererMapping.entityType() != joinableNode.entityType();

				for (EntityPropertyGraphNode propertyNode : joinableNode.entityProperties().values()) {
					Property property = propertyNode.property();

					FetchSource fetchSource = refererMapping.source();

					// cast if necessary
					if (subTypeJoin)
						fetchSource = fetchSource.as(joinableNode.entityType());

					joinCount++;
					FetchSource join = fetchSource.leftJoin(property);
					FetchedEntityMapping fetchedMapping = new FetchedEntityMapping(property, join, refererMapping.probability * localProbability);
					
					AbstractEntityGraphNode abstractEntityNode = propertyNode.entityNode();

					fetchedMapping.possiblePolyNode = abstractEntityNode.isPolymorphic();
					
					for (EntityGraphNode entityNode: abstractEntityNode.entityNodes()) {
						boolean polymorphic = entityNode.entityType() == property.getType();

						// are only base type properties joinable or also polymorphic ones?
						if (supportsSubTypeJoin || polymorphic) {
							fetchedMapping.addJoinable(entityNode);
							if (polymorphic)
								fetchedMapping.polymorphicCount++;
							refererMapping.wirings().add(fetchedMapping);
						} else {
							fetchedMapping.addNonJoinable(entityNode);
						}
					}

					newMappings.add(fetchedMapping);
				}
			}

			// register next level
			for (FetchedEntityMapping fetchedMapping : newMappings) {
				registerMappingWithJoins(fetchedMapping, check);
			}
		} finally {
			check.remove(refererMapping.property);
		}
	}

	private static class MappingWithPostProcessing {
		public final EntityMapping mapping;
		public final List<NodePostProcessing> postProcessings;

		public MappingWithPostProcessing(EntityMapping mapping, List<NodePostProcessing> postProcessings) {
			super();
			this.mapping = mapping;
			this.postProcessings = postProcessings;
		}
	}

	private class ResultWiringContext {
		public final FetchContext fetchContext;
		public Map<Object, GenericEntity> existingEntitiesById = new HashMap<Object, GenericEntity>();
		public final PostProcessing postProcessing = new PostProcessing();
		private List<MappingWithPostProcessing> postProcessings = new ArrayList<>();

		public ResultWiringContext(FetchContext fetchContext, Map<Object, GenericEntity> existingEntitiesById) {
			this.fetchContext = fetchContext;
			this.existingEntitiesById = existingEntitiesById;

			for (int i = 1; i < mappings.size(); ++i) {
				EntityMapping mapping = mappings.get(i);
				postProcessings.add(new MappingWithPostProcessing(mapping, buildNodePostProcessings(postProcessing, mapping)));
			}
		}

		public void handleRows(FetchResults results) {
			GenericEntity entities[] = new GenericEntity[mappings.size()];

			while (results.next()) {
				// fill current entities with identity managed entities
				Object id = results.get(0);
				GenericEntity rootEntity = existingEntitiesById.get(id);

				entities[0] = rootEntity;

				for (MappingWithPostProcessing entry : postProcessings) {
					EntityMapping mapping = entry.mapping;
					int col = mapping.pos();

					GenericEntity entity = (GenericEntity) results.get(col);

					if (entity == null) {
						entities[col] = null;
						continue;
					}

					EntityIdm entityIdm = fetchContext.acquireEntity(entity);
					entity = entityIdm.entity;

					entities[col] = entity;

					for (NodePostProcessing nodePostProcessing : entry.postProcessings)
						nodePostProcessing.handle(entityIdm);
				}

				wireRow(entities);
			}
		}

		/**
		 * Wires each fetched row into the actual entity instances according to the mapping tree.
		 */
		private void wireRow(GenericEntity entities[]) {
			GenericEntity entity = entities[rootMapping.pos()];

			wireRow(entities, rootMapping, entity);
		}

		private void wireRow(GenericEntity entities[], EntityMapping mapping, GenericEntity entity) {
			for (FetchedEntityMapping wiring : mapping.wirings()) {
				GenericEntity wiredEntity = entities[wiring.pos()];
				Property property = wiring.property();

				if (wiredEntity != null || property.getDeclaringType().isInstance(entity))
					property.set(entity, wiredEntity);

				if (wiredEntity != null)
					wireRow(entities, wiring, wiredEntity);
			}
		}
	}
}
