package com.braintribe.gm.graphfetching.processing.fetch;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import com.braintribe.gm.graphfetching.api.node.EntityCollectionPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityGraphNode;
import com.braintribe.gm.graphfetching.api.node.ScalarCollectionPropertyGraphNode;
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
 * Utility class for (recursive) fetching of to-many (collection) relationships in an entity graph.
 * Splits between entity collections and scalar collections.
 * Uses bulk queries and optional consumer/visitor for processing.
 */
public class ToManyFetching {
	
	/**
	 * Fetch all to-many properties of the given node (entity and scalar collections).
	 */
	public static void fetch(FetchContext context, EntityGraphNode node, FetchTask fetchTask) {
		List<EntityCollectionPropertyGraphNode> entityCollectionProperties = node.entityCollectionProperties();
		List<ScalarCollectionPropertyGraphNode> scalarCollectionProperties = node.scalarCollectionProperties();
		
		// entity collections
		if (!entityCollectionProperties.isEmpty()) {
			for (EntityCollectionPropertyGraphNode collectionNode: entityCollectionProperties) {
				FetchQualification fqToOne = new FetchQualification(collectionNode, FetchType.TO_ONE);
				FetchQualification fqToMany = new FetchQualification(collectionNode, FetchType.TO_MANY);

				Map<Object, GenericEntity> toManyFetches = new HashMap<>();
				Map<Object, GenericEntity> toOneFetches = new HashMap<>();
				
				fetch(context, node.entityType(), fetchTask, collectionNode.collectionType(), collectionNode.property(), 
						(GenericEntity e) -> {
							EntityIdm entityIdm = context.acquireEntity(e);
							e = entityIdm.entity;
							
							if (!entityIdm.isHandled(fqToOne)) {
								entityIdm.addHandled(fqToOne);
								toOneFetches.put(e.getId(), e);
							}
							
							if (!entityIdm.isHandled(fqToMany)) {
								entityIdm.addHandled(fqToMany);
								toManyFetches.put(e.getId(), e);
							}

							return e;
						});
				
				context.enqueueToManyIfRequired(collectionNode, toManyFetches);
				context.enqueueToOneIfRequired(collectionNode, toOneFetches);
			}
		}
		
		// scalar collections
		if (!scalarCollectionProperties.isEmpty()) {
			for (ScalarCollectionPropertyGraphNode collectionNode: scalarCollectionProperties) {
				fetch(context, node.entityType(), fetchTask, collectionNode.collectionType(), collectionNode.property(), null);
			}
		}
		
	}
	
	/**
	 * Bulk fetching for each collection property in the graph; assigns collections back to owning entities.
	 */
	public static <E> void fetch(FetchContext context, EntityType<?> type, FetchTask fetchTask, LinearCollectionType collectionType, Property property, Function<E,E> visitor) {
		PersistenceGmSession session = context.session();
		Map<Object, GenericEntity> entityIndex = fetchTask.entities;
		
		List<Set<Object>> allIds = CollectionTools2.splitToSets(entityIndex.keySet(), 100);
		
		SelectQuery query = ToManyQueries.joinQuery(type, property);
		
		for (Set<Object> ids: allIds) {

			List<ListRecord> results = session.queryDetached().select(query).setVariable("ids", ids).list();
			
			Object curId = null;
			Collection<E> curCollection = null;

			for (ListRecord record : results) {
				Object id = record.get(0);
				@SuppressWarnings("unchecked")
				E element = (E) record.get(1);
				
				if (!id.equals(curId)) {
					curId = id;
					GenericEntity entity = entityIndex.get(id);
					@SuppressWarnings("unchecked")
					Collection<E> collection = (Collection<E>)collectionType.createPlain();
					curCollection = collection; 
					property.set(entity, curCollection);
				}
				
				if (visitor != null)
					element = visitor.apply(element);
				
				curCollection.add(element);
			}
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
