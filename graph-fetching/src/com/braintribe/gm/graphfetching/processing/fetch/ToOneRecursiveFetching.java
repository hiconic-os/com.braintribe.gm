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
import java.util.concurrent.atomic.AtomicInteger;

import com.braintribe.gm.graphfetching.api.node.EntityGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.FetchQualification;
import com.braintribe.logging.Logger;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EssentialCollectionTypes;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.value.Variable;
import com.braintribe.model.processing.query.building.SelectQueries;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.query.From;
import com.braintribe.model.query.Join;
import com.braintribe.model.query.JoinType;
import com.braintribe.model.query.SelectQuery;
import com.braintribe.model.query.Source;
import com.braintribe.model.record.ListRecord;
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
		protected EntityGraphNode node;
		protected Source source;
		protected boolean recursionStop = false;

		public EntityMapping(int pos, Source source) {
			super();
			this.pos = pos;
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

		public Source source() {
			return source;
		}

		public List<FetchedEntityMapping> wirings() {
			return wirings;
		}
	}

	private class ExistingEntityMapping extends EntityMapping {

		private EntityGraphNode node;

		public ExistingEntityMapping(EntityGraphNode node, int pos, Source source) {
			super(pos, source);
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
		private Property property;
		private List<EntityGraphNode> covariantNodes = new ArrayList<>();
		private List<EntityGraphNode> exactNodes = new ArrayList<>();

		public FetchedEntityMapping(Property property, int pos, Source source) {
			super(pos, source);
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
	private List<Object> selections;
	private SelectQuery query;

	public ToOneRecursiveFetching(EntityGraphNode node) {
		From source = SelectQueries.source(node.entityType());
		Variable idsVar = Variable.T.create();
		idsVar.setName("ids");
		idsVar.setTypeSignature(EssentialCollectionTypes.TYPE_SET.getTypeSignature());
		query = SelectQueries.from(source).where(SelectQueries.in(SelectQueries.property(source, GenericEntity.id), idsVar));

		selections = query.getSelections();

		int pos = select(SelectQueries.property(source, GenericEntity.id));

		CyclicRecurrenceCheck check = new CyclicRecurrenceCheck();
		ExistingEntityMapping entityMapping = new ExistingEntityMapping(node, pos, source);

		registerMappingWithJoins(entityMapping, check);
	}

	public void fetch(FetchContext context, FetchTask task) {
		ResultWiringContext wiringContext = new ResultWiringContext(task.entities);
		Set<Object> allIds = task.entities.keySet();

		// query joined entities in bulks
		List<Set<Object>> idBulks = CollectionTools2.splitToSets(allIds, FetchProcessing.BULK_SIZE);

		PersistenceGmSession session = context.session();

		long nanoStart = System.nanoTime();

		context.processParallel(idBulks, ids -> {
			List<ListRecord> records = session.queryDetached().select(query).setVariable("ids", ids).list();
			synchronized (wiringContext.fetchedRows) {
				wiringContext.fetchedRows.addAll(records);
			}
		});

		// for (Set<Object> ids : idBulks) {
		// List<ListRecord> records = session.queryDetached().select(query).setVariable("ids", ids).list();
		// wiringContext.fetchedRows.addAll(records);
		// }

		Duration duration = Duration.ofNanos(System.nanoTime() - nanoStart);
		logger.trace(() -> "consumed " + duration.toMillis() + " ms for querying " + allIds.size() + " entities in " + idBulks.size()
				+ " batches with: " + query.stringify());

		long wiringNanoStart = System.nanoTime();
		PostProcessing postProcessing = wireAndExtractFetchedEntities(context, wiringContext);
		Duration wiringDuration = Duration.ofNanos(System.nanoTime() - wiringNanoStart);
		logger.trace(() -> "consumed " + wiringDuration.toMillis() + " ms for wiring " + allIds.size() + " entities");

		for (Map.Entry<EntityGraphNode, Map<Object, GenericEntity>> entry : postProcessing.toManies.entrySet()) {
			context.enqueueToManyIfRequired(entry.getKey(), entry.getValue());
		}

		for (Map.Entry<EntityGraphNode, Map<Object, GenericEntity>> entry : postProcessing.toOnes.entrySet()) {
			context.enqueueToOneIfRequired(entry.getKey(), entry.getValue());
		}
	}

	private PostProcessing wireAndExtractFetchedEntities(FetchContext fetchContext, ResultWiringContext context) {
		List<ListRecord> fetchedRows = context.fetchedRows;

		PostProcessing postProcessing = new PostProcessing();

		int rowCount = fetchedRows.size();

		GenericEntity entities[][] = new GenericEntity[mappings.size()][rowCount];

		for (int row = 0; row < rowCount; row++) {
			ListRecord record = fetchedRows.get(row);
			Object id = record.get(0);
			GenericEntity entity = context.existingEntitiesById.get(id);
			entities[0][row] = entity;
		}

		for (int mappingIdx = 1; mappingIdx < mappings.size(); ++mappingIdx) {
			EntityMapping mapping = mappings.get(mappingIdx);
			int col = mapping.pos();

			GenericEntity colEntities[] = entities[col];

			List<NodePostProcessing> nodePostProcessings = buildNodePostProcessings(postProcessing, mapping);

			for (int row = 0; row < rowCount; row++) {
				ListRecord record = fetchedRows.get(row);

				GenericEntity entity = (GenericEntity) record.get(col);

				if (entity == null)
					continue;

				EntityIdm entityIdm = fetchContext.acquireEntity(entity);
				entity = entityIdm.entity;

				colEntities[row] = entity;

				for (NodePostProcessing nodePostProcessing : nodePostProcessings)
					nodePostProcessing.handle(entityIdm);
			}
		}

		for (EntityMapping mapping : mappings)
			for (int row = 0; row < rowCount; row++)
				wireEntities(entities, mapping, row);

		return postProcessing;
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
			return toManies.computeIfAbsent(node, k -> new HashMap<>());
		}
		public Map<Object, GenericEntity> acquireToOnes(EntityGraphNode node) {
			return toOnes.computeIfAbsent(node, k -> new HashMap<>());
		}
	}

	private static class NodePostProcessing {
		private PostProcessing postProcessing;
		private Map<Object, GenericEntity> toManies;
		private Map<Object, GenericEntity> toOnes;
		private EntityGraphNode node;
		private boolean toOneAlreadyFetched;
		private boolean covariant;

		public NodePostProcessing(PostProcessing postProcessing, EntityGraphNode node, boolean toOneAlreadyFetched, boolean covariant) {
			super();
			this.postProcessing = postProcessing;
			this.node = node;
			this.toOneAlreadyFetched = toOneAlreadyFetched;
			this.covariant = covariant;
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
			if (toManies == null)
				toManies = postProcessing.acquireToManies(node);

			toManies.put(entity.getId(), entity);
		}

		private void addToOne(GenericEntity entity) {
			if (toOnes == null)
				toOnes = postProcessing.acquireToOnes(node);

			toOnes.put(entity.getId(), entity);
		}

	}

	/**
	 * Wires each fetched row into the actual entity instances according to the mapping tree.
	 */
	private void wireEntities(GenericEntity entities[][], EntityMapping mapping, int row) {
		GenericEntity entity = entities[mapping.pos()][row];

		if (entity == null)
			return;

		for (FetchedEntityMapping wiring : mapping.wirings()) {
			GenericEntity wiredEntity = entities[wiring.pos()][row];
			Property property = wiring.property();
			property.set(entity, wiredEntity);
		}
	}

	private int select(Object select) {
		int pos = selections.size();
		selections.add(select);
		return pos;
	}

	private static class CyclicRecurrenceCheck {
		private Map<EntityGraphNode, AtomicInteger> recurrences = new IdentityHashMap<>();
		private int limit = 3;
		
		public boolean add(EntityGraphNode entityNode) {
			AtomicInteger counter = recurrences.computeIfAbsent(entityNode, k -> new AtomicInteger(0));
			
			counter.incrementAndGet();
			
			return counter.get() > limit;
		}
		
		public void remove(EntityGraphNode entityNode) {
			recurrences.compute(entityNode, (n, c) -> {
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

		boolean recursionStop = check.add(refererMapping.node);
		
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
						Join join = refererMapping.source().join(property.getName(), JoinType.left);
						int pos = select(join);
						fetchedMapping = new FetchedEntityMapping(property, pos, join);
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
			check.remove(refererMapping.node);
		}
	}

	private class ResultWiringContext {
		public Map<Object, GenericEntity> existingEntitiesById = new HashMap<Object, GenericEntity>();
		public List<ListRecord> fetchedRows;

		public ResultWiringContext(Map<Object, GenericEntity> existingEntitiesById) {
			fetchedRows = new ArrayList<>(existingEntitiesById.size());
			this.existingEntitiesById = existingEntitiesById;
		}
	}

}
