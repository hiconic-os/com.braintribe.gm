package com.braintribe.gm.graphfetching.processing.fetch;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import com.braintribe.gm.graphfetching.api.node.EntityCollectionPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityGraphNode;
import com.braintribe.gm.graphfetching.api.node.ScalarCollectionPropertyGraphNode;
import com.braintribe.logging.Logger;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EssentialCollectionTypes;
import com.braintribe.model.generic.reflection.LinearCollectionType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.TypeCode;
import com.braintribe.model.generic.value.Variable;
import com.braintribe.model.processing.query.building.SelectQueries;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.query.From;
import com.braintribe.model.query.Join;
import com.braintribe.model.query.SelectQuery;
import com.braintribe.model.query.conditions.Condition;
import com.braintribe.model.query.functions.ListIndex;
import com.braintribe.model.record.ListRecord;
import com.braintribe.utils.lcd.CollectionTools2;

/**
 * Utility class for (recursive) fetching of to-many (collection) relationships in an entity graph. Splits between entity collections and scalar
 * collections. Uses bulk queries and optional consumer/visitor for processing.
 */
public class ToManyFetching {
	private static final Logger logger = Logger.getLogger(ToManyFetching.class);

	static class EntityMapping {
		public List<EntityGraphNode> exactNodes = new ArrayList<>();
		public List<EntityGraphNode> covariantNodes = new ArrayList<>();
		public Property property;

		public EntityMapping(Property property) {
			super();
			this.property = property;
		}
	}

	static class NodePostProcessing {
		public EntityGraphNode node;
		public Map<Object, GenericEntity> toOnes = new LinkedHashMap<Object, GenericEntity>();
		public Map<Object, GenericEntity> toManies = new LinkedHashMap<Object, GenericEntity>();
		public boolean covariant;

		public NodePostProcessing(EntityGraphNode node, boolean covariant) {
			super();
			this.node = node;
			this.covariant = covariant;
		}
	}

	private static Map<Property, EntityMapping> buildMappings(List<EntityCollectionPropertyGraphNode> nodes) {
		Map<Property, EntityMapping> mappings = new LinkedHashMap<>();

		for (EntityCollectionPropertyGraphNode node : nodes) {
			EntityGraphNode entityNode = node.entityNode();
			EntityMapping mapping = mappings.computeIfAbsent(node.property(), EntityMapping::new);

			if (entityNode.entityType() == node.condensedPropertyType()) {
				mapping.exactNodes.add(entityNode);
			} else {
				mapping.covariantNodes.add(entityNode);
			}
		}

		return mappings;
	}

	private static List<NodePostProcessing> buildPostProcessings(EntityMapping mapping) {
		List<NodePostProcessing> postProcessings = new ArrayList<>();
		for (EntityGraphNode exactNode : mapping.exactNodes) {
			postProcessings.add(new NodePostProcessing(exactNode, false));
		}

		for (EntityGraphNode covariantNode : mapping.covariantNodes) {
			postProcessings.add(new NodePostProcessing(covariantNode, true));
		}

		return postProcessings;
	}

	/**
	 * Fetch all to-many properties of the given node (entity and scalar collections).
	 */
	public static void fetch(FetchContext context, EntityGraphNode node, FetchTask fetchTask) {
		List<EntityCollectionPropertyGraphNode> entityCollectionProperties = node.entityCollectionProperties();
		List<ScalarCollectionPropertyGraphNode> scalarCollectionProperties = node.scalarCollectionProperties();

		// entity collections
		if (!entityCollectionProperties.isEmpty()) {
			Collection<EntityMapping> mappings = buildMappings(entityCollectionProperties).values();

			for (EntityMapping mapping : mappings) {
				List<NodePostProcessing> postProcessings = buildPostProcessings(mapping);

				Property property = mapping.property;

				fetch(context, node.entityType(), fetchTask, (LinearCollectionType) property.getType(), property, (GenericEntity e) -> {
					EntityIdm entityIdm = context.acquireEntity(e);
					e = entityIdm.entity;

					for (NodePostProcessing postProcessing : postProcessings) {
						EntityGraphNode entityNode = postProcessing.node;
						if (postProcessing.covariant && !entityNode.entityType().isInstance(e))
							continue;

						if (!entityIdm.isHandled(entityNode.toOneQualification())) {
							entityIdm.addHandled(entityNode.toOneQualification());
							postProcessing.toOnes.put(e.getId(), e);
						}

						if (!entityIdm.isHandled(entityNode.toManyQualification())) {
							entityIdm.addHandled(entityNode.toManyQualification());
							postProcessing.toManies.put(e.getId(), e);
						}
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
		if (!scalarCollectionProperties.isEmpty()) {
			for (ScalarCollectionPropertyGraphNode collectionNode : scalarCollectionProperties) {
				fetch(context, node.entityType(), fetchTask, collectionNode.type(), collectionNode.property(), null);
			}
		}
	}

	/**
	 * Bulk fetching for each collection property in the graph; assigns collections back to owning entities.
	 */
	public static <E> void fetch(FetchContext context, EntityType<?> type, FetchTask fetchTask, LinearCollectionType collectionType,
			Property property, Function<E, E> visitor) {
		PersistenceGmSession session = context.session();
		Map<Object, GenericEntity> entityIndex = fetchTask.entities;

		Set<Object> allIds = entityIndex.keySet();
		List<Set<Object>> idBulks = CollectionTools2.splitToSets(allIds, FetchProcessing.BULK_SIZE);

		for (GenericEntity entity : fetchTask.entities.values()) {
			property.set(entity, collectionType.createPlain());
		}

		SelectQuery query = ToManyQueries.joinQuery(type, property);

		long queryNanoStart = System.nanoTime();

		List<ListRecord> collectedListRecords = new ArrayList<>();

		context.processParallel(idBulks, ids -> {
			List<ListRecord> results = session.queryDetached().select(query).setVariable("ids", ids).list();

			synchronized (collectedListRecords) {
				collectedListRecords.addAll(results);
			}
		});

		long queryDuration = System.nanoTime() - queryNanoStart;

		long wiringNanoStart = System.nanoTime();

		for (ListRecord record : collectedListRecords) {
			Object id = record.get(0);
			E element = (E) record.get(1);

			Object curId = null;
			Collection<E> curCollection = null;

			if (!id.equals(curId)) {
				curId = id;
				GenericEntity entity = entityIndex.get(id);
				curCollection = property.get(entity);
			}

			if (visitor != null)
				element = visitor.apply(element);

			curCollection.add(element);
		}

		long wiringDurationTotal = System.nanoTime() - wiringNanoStart;

		// long nanosTotal = 0;
		// long wiringNanosTotal = 0;
		//
		// for (Set<Object> ids : idBulks) {
		//
		// long nanoStart = System.nanoTime();
		// List<ListRecord> results = session.queryDetached().select(query).setVariable("ids", ids).list();
		// nanosTotal += System.nanoTime() - nanoStart;
		//
		// long wiringNanoStart = System.nanoTime();
		// Object curId = null;
		// Collection<E> curCollection = null;
		//
		// for (ListRecord record : results) {
		// Object id = record.get(0);
		// E element = (E) record.get(1);
		//
		// if (!id.equals(curId)) {
		// curId = id;
		// GenericEntity entity = entityIndex.get(id);
		// curCollection = property.get(entity);
		// }
		//
		// if (visitor != null)
		// element = visitor.apply(element);
		//
		// curCollection.add(element);
		// }
		//
		// wiringNanosTotal += System.nanoTime() - wiringNanoStart;
		// }

		if (logger.isTraceEnabled()) {
			logger.trace("consumed " + Duration.ofNanos(queryDuration).toMillis() + " ms for querying " + allIds.size() + " entities in "
					+ idBulks.size() + " batches with: " + query.stringify());
			logger.trace("consumed " + Duration.ofNanos(wiringDurationTotal).toMillis() + " ms for wiring " + allIds.size() + " entities in "
					+ idBulks.size() + " batches with: " + query.stringify());
		}
	}

	/**
	 * Helper that composes a select query for joining to-many targets efficiently. Handles lists with ordering.
	 */
	private static class ToManyQueries extends SelectQueries {
		public static SelectQuery joinQuery(EntityType<?> refereeType, Property property) {
			From referee = source(refereeType);
			Join refered = referee.join(property.getName());

			Variable idsVar = Variable.T.create();
			idsVar.setName("ids");
			idsVar.setTypeSignature(EssentialCollectionTypes.TYPE_SET.getTypeSignature());
			Condition condition = in(property(referee, GenericEntity.id), idsVar);

			SelectQuery query = from(referee).where(condition) //
					.select(property(referee, GenericEntity.id), refered);

			if (property.getType().getTypeCode() == TypeCode.listType) {
				ListIndex listIndex = ListIndex.T.create();
				listIndex.setJoin(refered);
				query.orderBy(listIndex);
			}

			return query;
		}
	}
}
