package com.braintribe.gm.graphfetching.processing.fetch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.braintribe.gm.graphfetching.api.node.EntityGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityPropertyGraphNode;
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
 * Core implementation of recursive (deep) fetching of to-one entity references for a fetch graph.
 * Builds join/selection maps dynamically, autowires recursively for each subnode.
 * Batches queries for efficiency. Usage is internal to FetchProcessing.
 */
public class ToOneRecursiveFetching {
	
	private abstract class EntityMapping {
		protected List<FetchedEntityMapping> wirings = new ArrayList<>();
		protected int pos;
		protected EntityGraphNode node;
		protected Source source;
		
		public EntityMapping(EntityGraphNode node, int pos, Source source) {
			super();
			this.node = node;
			this.pos = pos;
			this.source = source;
		}

		public EntityGraphNode node() {
			return node;
		}
		
		public int pos() {
			return pos;
		}
		
		public Source source() {
			return source;
		}
		
		public List<FetchedEntityMapping> wirings() {
			return wirings;
		}
	}
	
	private class ExistingEntityMapping extends EntityMapping {

		public ExistingEntityMapping(EntityGraphNode node, int pos, Source source) {
			super(node, pos, source);
		}
	}
	
	private class FetchedEntityMapping extends EntityMapping {
		private Property property;

		public FetchedEntityMapping(EntityPropertyGraphNode node, int pos, Source source) {
			super(node, pos, source);
			property = node.property();
		}
		
		public Property property() {
			return property;
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
		query = SelectQueries.from(source).where(SelectQueries.in(
				SelectQueries.property(source, GenericEntity.id), 
				idsVar));
		
		selections = query.getSelections();
		
		int pos = select(SelectQueries.property(source, GenericEntity.id));
		
		ExistingEntityMapping entityMapping = new ExistingEntityMapping(node, pos, source);
		
		registerMappingWithJoins(entityMapping);
	}
	
	public void fetch(FetchContext context, FetchTask task) {
		ResultWiringContext wiringContext = new ResultWiringContext(task.entities);
		Set<Object> allIds = task.entities.keySet();
		
		// query joined entities in bulks
		List<Set<Object>> idBulks = CollectionTools2.splitToSets(allIds, 100);
		
		PersistenceGmSession session = context.session();
		
		for (Set<Object> ids: idBulks) {
			List<ListRecord> records = session.queryDetached().select(query).setVariable("ids", ids).list();
			wiringContext.fetchedRows.addAll(records); 
		}
		
		Map<EntityGraphNode, Map<Object, GenericEntity>> fetchEntities = wireAndExtractFetchedEntities(context, wiringContext);
		
		for (Map.Entry<EntityGraphNode, Map<Object, GenericEntity>> entry: fetchEntities.entrySet()) {
			context.enqueueToManyIfRequired(entry.getKey(), entry.getValue());
		}
	}
	
	private Map<EntityGraphNode, Map<Object, GenericEntity>> wireAndExtractFetchedEntities(FetchContext fetchContext, ResultWiringContext context) {
		List<ListRecord> fetchedRows = context.fetchedRows;
		
		Map<EntityGraphNode, Map<Object, GenericEntity>> fetchedEntities = new LinkedHashMap<>();
		
		int rowCount = fetchedRows.size(); 
		int colCount = mappings.size(); 
		
		GenericEntity entities[][] = new GenericEntity[mappings.size()][rowCount]; 
		
		for (int row = 0; row < rowCount; row++) {
			ListRecord record = fetchedRows.get(row);
			Object id = record.get(0);
			GenericEntity entity = context.existingEntitiesById.get(id);
			entities[0][row] = entity;
		}
		
		
		for (int col = 1; col < colCount; col++) {
			EntityMapping mapping = mappings.get(col);
			GenericEntity colEntities[] = entities[col];
			
			Map<Object, GenericEntity> extractedEntities = fetchedEntities.computeIfAbsent(mapping.node(), k -> new HashMap<>());
			FetchQualification fqToMany = new FetchQualification(mapping.node(), FetchType.TO_MANY);
			FetchQualification fqToOne = new FetchQualification(mapping.node(), FetchType.TO_ONE);
			
			for (int row = 0; row < rowCount; row++) {
				ListRecord record = fetchedRows.get(row);
				
				GenericEntity entity = (GenericEntity)record.get(col);
				
				if (entity == null)
					continue;
				
				EntityIdm entityIdm = fetchContext.acquireEntity(entity);
				entity = entityIdm.entity;
				
				colEntities[row] = entity;
				
				entityIdm.addHandled(fqToOne);
				
				if (!entityIdm.isHandled(fqToMany)) {
					entityIdm.addHandled(fqToMany);
					extractedEntities.put(entity.getId(), entity);
				}
			}
		}
		
		for (EntityMapping mapping: mappings)
			for (int row = 0; row < rowCount; row++)
				wireEntities(entities, mapping, row);
		
		return fetchedEntities;
	}

	/**
	 * Wires each fetched row into the actual entity instances according to the mapping tree.
	 */
	private void wireEntities(GenericEntity entities[][], EntityMapping mapping, int row) {
		GenericEntity entity = entities[mapping.pos()][row];
		
		if (entity == null)
			return;
		
		for (FetchedEntityMapping wiring: mapping.wirings()) {
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
	
	/**
	 * Resolves/assigns entity/graph nodes recursively; creates mappings for joined properties.
	 */
	private void registerMappingWithJoins(EntityMapping refererMapping) {
		mappings.add(refererMapping);
		
		 for (EntityPropertyGraphNode subNode: refererMapping.node().entityProperties()) {
			 Property property = subNode.property();
			 Join join = refererMapping.source().join(property.getName(), JoinType.left);
			 int pos = select(join);
			 FetchedEntityMapping fetchedMapping = new FetchedEntityMapping(subNode, pos, join);
			 refererMapping.wirings().add(fetchedMapping);
			 registerMappingWithJoins(fetchedMapping);
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
