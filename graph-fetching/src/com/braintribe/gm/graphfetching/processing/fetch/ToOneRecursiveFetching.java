package com.braintribe.gm.graphfetching.processing.fetch;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.braintribe.gm.graphfetching.api.node.EntityGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.FetchQualification;
import com.braintribe.gm.graphfetching.api.query.FetchQuery;
import com.braintribe.gm.graphfetching.api.query.FetchResults;
import com.braintribe.gm.graphfetching.api.query.FetchSource;
import com.braintribe.logging.Logger;
import com.braintribe.model.generic.GenericEntity;
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

		public EntityMapping(FetchSource source) {
			super();
			this.pos = source.pos();
			this.source = source;
		}

		public abstract List<? extends EntityGraphNode> exactNodes();
		public abstract List<? extends EntityGraphNode> covariantNodes();

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
	}

	private class ExistingEntityMapping extends EntityMapping {

		private EntityGraphNode node;

		public ExistingEntityMapping(EntityGraphNode node, FetchSource source) {
			super(source);
			this.node = node;
		}

		@Override
		public List<EntityGraphNode> covariantNodes() {
			return Collections.emptyList();
		}

		@Override
		public List<EntityGraphNode> exactNodes() {
			return Collections.singletonList(node);
		}
	}

	private class FetchedEntityMapping extends EntityMapping {
		private List<EntityGraphNode> covariantNodes = new ArrayList<>();
		private List<EntityGraphNode> exactNodes = new ArrayList<>();

		public FetchedEntityMapping(Property property, FetchSource source) {
			super(source);
			this.property = property;
		}

		public Property property() {
			return property;
		}

		public void addExact(EntityGraphNode subNode) {
			exactNodes.add(subNode);
		}

		public void addCovariant(EntityGraphNode subNode) {
			covariantNodes.add(subNode);
		}

		@Override
		public List<EntityGraphNode> covariantNodes() {
			return covariantNodes;
		}

		@Override
		public List<EntityGraphNode> exactNodes() {
			return exactNodes;
		}
	}

	private List<EntityMapping> mappings = new ArrayList<>();
	private FetchQuery fetchQuery;
	private FetchContext context;

	public ToOneRecursiveFetching(FetchContext context, EntityGraphNode node) {
		CyclicRecurrenceCheck check = new CyclicRecurrenceCheck();
		
		fetchQuery = context.queryFactory().createQuery(node.entityType(), context.session().getAccessId());
		
		FetchSource from = fetchQuery.from();
		ExistingEntityMapping entityMapping = new ExistingEntityMapping(node, from);
		
		registerMappingWithJoins(entityMapping, check);
	}

	public void fetch(FetchContext context, FetchTask task) {
		ResultWiringContext wiringContext = new ResultWiringContext(context, task.entities);
		
		Set<Object> allIds = task.entities.keySet();

		// query joined entities in bulks
		List<Set<Object>> idBulks = CollectionTools2.splitToSets(allIds, context.bulkSize());

		long nanoStart = System.nanoTime();
		
		context.processParallel(idBulks, ids -> {
			FetchResults results = fetchQuery.fetchFor(ids);
			wiringContext.handleRows(results);
		});

		Duration duration = Duration.ofNanos(System.nanoTime() - nanoStart);
		logger.trace(() -> "consumed " + duration.toMillis() + " ms for querying " + allIds.size() + " entities in " + idBulks.size()
				+ " batches with: " + fetchQuery.stringify());

		for (Map.Entry<EntityGraphNode, Map<Object, GenericEntity>> entry : wiringContext.postProcessing.toManies.entrySet()) {
			context.enqueueToManyIfRequired(entry.getKey(), entry.getValue());
		}

		for (Map.Entry<EntityGraphNode, Map<Object, GenericEntity>> entry : wiringContext.postProcessing.toOnes.entrySet()) {
			context.enqueueToOneIfRequired(entry.getKey(), entry.getValue());
		}
	}

	private List<NodePostProcessing> buildNodePostProcessings(PostProcessing postProcessing, EntityMapping mapping) {
		List<NodePostProcessing> processings = new ArrayList<>();

		boolean toOneAlreadyFetched = !mapping.isRecursionStop();
		
		for (EntityGraphNode node : mapping.exactNodes())
			processings.add(new NodePostProcessing(postProcessing, node, toOneAlreadyFetched, false));

		for (EntityGraphNode node : mapping.covariantNodes())
			processings.add(new NodePostProcessing(postProcessing, node, false, true));

		return processings;
	}

	private static class PostProcessing {
		private Map<EntityGraphNode, Map<Object, GenericEntity>> toManies = new LinkedHashMap<EntityGraphNode, Map<Object, GenericEntity>>();
		private Map<EntityGraphNode, Map<Object, GenericEntity>> toOnes = new LinkedHashMap<EntityGraphNode, Map<Object, GenericEntity>>();

		public Map<Object, GenericEntity> acquireToManies(EntityGraphNode node) {
			return toManies.computeIfAbsent(node, k -> new ConcurrentHashMap<>());
		}
		public Map<Object, GenericEntity> acquireToOnes(EntityGraphNode node) {
			return toOnes.computeIfAbsent(node, k -> new ConcurrentHashMap<>());
		}
	}

	private static class NodePostProcessing {
		private Map<Object, GenericEntity> toManies;
		private Map<Object, GenericEntity> toOnes;
		private EntityGraphNode node;
		private boolean toOneAlreadyFetched;
		private boolean covariant;

		public NodePostProcessing(PostProcessing postProcessing, EntityGraphNode node, boolean toOneAlreadyFetched, boolean covariant) {
			super();
			this.node = node;
			this.toOneAlreadyFetched = toOneAlreadyFetched;
			this.covariant = covariant;
			this.toOnes = postProcessing.acquireToOnes(node);
			this.toManies = postProcessing.acquireToManies(node);
		}

		public void handle(EntityIdm entityIdm) {
			if (covariant && !node.entityType().isInstance(entityIdm.entity))
				return;

			if (toOneAlreadyFetched)
				entityIdm.addHandled(node.toOneQualification());	
			else
				handleToOne(entityIdm);
			
			handleToMany(entityIdm);
		}

		private void handleToMany(EntityIdm entityIdm) {
			FetchQualification fqToMany = node.toManyQualification();
			if (!entityIdm.isHandled(fqToMany)) {
				entityIdm.addHandled(fqToMany);
				addToMany(entityIdm.entity);
			}
		}

		private void handleToOne(EntityIdm entityIdm) {
			FetchQualification fqToOne = node.toOneQualification();
			if (!entityIdm.isHandled(fqToOne)) {
				entityIdm.addHandled(fqToOne);
				addToOne(entityIdm.entity);
			}
		}

		private void addToMany(GenericEntity entity) {
			toManies.put(entity.getId(), entity);
		}

		private void addToOne(GenericEntity entity) {
			toOnes.put(entity.getId(), entity);
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
				
				return counter == 0? null: c;
			});
		}
	}
	
	/**
	 * Resolves/assigns entity/graph nodes recursively; creates mappings for joined properties.
	 */
	private void registerMappingWithJoins(EntityMapping refererMapping, CyclicRecurrenceCheck check) {
		mappings.add(refererMapping);

		boolean recursionStop = check.add(refererMapping.property);
		
		try {
			refererMapping.setRecursionStop(recursionStop);
			
			if (recursionStop)
				return;
	
			Map<Property, FetchedEntityMapping> propertyMappings = new LinkedHashMap<>();
	
			for (EntityGraphNode exactNode : refererMapping.exactNodes()) {
				for (EntityPropertyGraphNode subNode : exactNode.entityProperties()) {
					EntityGraphNode entityNode = subNode.entityNode();
					Property property = subNode.property();
	
					FetchedEntityMapping fetchedMapping = propertyMappings.get(property);
	
					if (fetchedMapping == null) {
						FetchSource join = refererMapping.source().leftJoin(property);
						fetchedMapping = new FetchedEntityMapping(property, join);
						propertyMappings.put(property, fetchedMapping);
					}
	
					// is it a property polymorphic node?
					if (entityNode.entityType() == property.getType()) {
						fetchedMapping.addExact(entityNode);
						refererMapping.wirings().add(fetchedMapping);
					} else {
						fetchedMapping.addCovariant(entityNode);
					}
				}
			}
	
			// register next level
			for (FetchedEntityMapping fetchedMapping : propertyMappings.values()) {
				registerMappingWithJoins(fetchedMapping, check);
			}
		}
		finally {
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
		
				for (MappingWithPostProcessing entry: postProcessings) {
					EntityMapping mapping = entry.mapping;
					int col = mapping.pos();
		
					GenericEntity entity = (GenericEntity)results.get(col);
		
					if (entity == null)
						continue;
		
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
			for (EntityMapping mapping : mappings) {
				GenericEntity entity = entities[mapping.pos()];
				
				if (entity == null)
					continue;
				
				for (FetchedEntityMapping wiring : mapping.wirings()) {
					GenericEntity wiredEntity = entities[wiring.pos()];
					Property property = wiring.property();
					property.set(entity, wiredEntity);
				}
			}
		}
	}
}
