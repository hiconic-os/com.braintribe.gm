package com.braintribe.gm.graphfetching.processing.fetch;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import com.braintribe.gm.graphfetching.api.node.AbstractEntityGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityCollectionPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityRelatedPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.FetchQualification;
import com.braintribe.gm.graphfetching.api.node.MapPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.ScalarCollectionPropertyGraphNode;
import com.braintribe.gm.graphfetching.processing.util.FetchingTools;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.AbsentEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.VdHolder;
import com.braintribe.model.generic.value.ValueDescriptor;

public class FlatFetching {
	private final FetchContext context;
	private FetchTask task;

	public FlatFetching(FetchContext context, FetchTask task) {
		super();
		this.context = context;
		this.task = task;
	}
	
	public CompletableFuture<Void> fetch() {
		List<PropertyFetch> propertyFetches = new ArrayList<>(); 
		
		for (EntityGraphNode entityNode: task.node.entityNodes()) {
			Map<Object, GenericEntity> entities = FetchingTools.filterSpecificType(task.node.entityType(), entityNode.entityType(), task.entities);
			
			if (entities.isEmpty())
				continue;
			
			for (EntityPropertyGraphNode entityPropertyNode : entityNode.entityProperties().values())
				propertyFetches.add(new EntityPropertyFetch(entityNode, entityPropertyNode, entities));
			
			for (EntityCollectionPropertyGraphNode entityCollectionPropertyNode : entityNode.entityCollectionProperties().values())
				propertyFetches.add(new EntityCollectionPropertyFetch(entityNode, entityCollectionPropertyNode, entities));
			
			for (ScalarCollectionPropertyGraphNode scalarCollectionPropertyNode : entityNode.scalarCollectionProperties().values())
				propertyFetches.add(new ScalarCollectionPropertyFetch(entityNode, scalarCollectionPropertyNode, entities));
			
			for (MapPropertyGraphNode mapPropertyNode : entityNode.mapProperties().values())
				propertyFetches.add(new MapPropertyFetch(entityNode, mapPropertyNode, entities));
				
		}
		
		return context.processElements(propertyFetches, PropertyFetch::fetch);
	}
	
	private interface PropertyFetch {
		void fetch();
	}
	
	private class EntityPostProcessing {
		private Map<Object, GenericEntity> newEntities = new ConcurrentHashMap<>();
		private FetchQualification fetchQualification;
		private AbstractEntityGraphNode entityNode;
		private boolean postProcessingNecessary;
		private FetchPathNode fetchPath;
		
		protected EntityPostProcessing(AbstractEntityGraphNode entityNode, FetchPathNode fetchPath) {
			this.entityNode = entityNode;
			this.fetchPath = fetchPath;
			this.fetchQualification = entityNode.allQualification();
			postProcessingNecessary = entityNode.hasCollectionOrEntityProperties();
		}
		
		protected void visit(EntityIdm entityIdm) {
			if (!postProcessingNecessary)
				return;
			
			if (entityIdm.addHandled(fetchQualification)) {
				GenericEntity entity = entityIdm.entity;
				newEntities.put(entity.getId(), entity);
			}
		}
		
		protected void postProcess(CompletableFuture<?> future) {
			context.observe(future.thenRun(this::enqueue));
		}
		
		protected void enqueue() {
			if (newEntities.isEmpty())
				return;
			
			FetchTask fetchTask = new FetchTask(entityNode, FetchType.ALL_FLAT, newEntities, fetchPath);
			context.enqueue(fetchTask);
		}
	}
	
	private class EntityPropertyFetch extends EntityPostProcessing implements PropertyFetch {
		private EntityPropertyGraphNode entityPropertyNode;
		private EntityGraphNode entityNode;
		private Map<Object, GenericEntity> entities;

		public EntityPropertyFetch(EntityGraphNode entityNode, EntityPropertyGraphNode entityPropertyNode, Map<Object, GenericEntity> entities) {
			super(entityPropertyNode.entityNode(), new FetchPathNode(task.fetchPath, entityPropertyNode));
			this.entityNode = entityNode;
			this.entityPropertyNode = entityPropertyNode;
			this.entities = entities;
		}
		
		@Override
		public void fetch() {
			EntityType<?> baseType = entityPropertyNode.entityNode().entityType();
			Property property = entityPropertyNode.property();
			
			Map<GenericEntity, Object> lookupCases = new IdentityHashMap<>();
			
			Map<Object, GenericEntity> joinCases = new HashMap<>();
			
			for (GenericEntity entity: entities.values()) {
				// TODO: remove
//				EntityType<?> entityType = entity.entityType();
//				
//				EntityType<?> declaringType = property.getDeclaringType();
//				if (baseType != entityType && declaringType.isInstance(entity))
//					continue;
				
				Object value = property.setDirect(entity, null);
				
				if (value == null)
					continue;
				
				if (VdHolder.isVdHolder(value)) {
					VdHolder vdHolder = (VdHolder)value;
					
					ValueDescriptor vd = vdHolder.vd;
					
					if (vd instanceof AbsentEntity) {
						AbsentEntity ref = (AbsentEntity)vd;
						lookupCases.put(entity, ref.getRefId());
					}
					else {
						joinCases.put(entity.getId(), entity);
					}
				}
			}
			
			List<CompletableFuture<?>> futures = new ArrayList<>(2);
			
			if (!lookupCases.isEmpty()) {
				CompletableFuture<Void> future = context.fetchEntities(entityPropertyNode.entityNode(), new HashSet<>(lookupCases.values()), this::visit) //
					.thenAccept(lookupEntities -> {
						for (Map.Entry<GenericEntity, Object> entry : lookupCases.entrySet()) {
							GenericEntity entity = entry.getKey();
							Object id = entry.getValue();
							GenericEntity toOneEntity = lookupEntities.get(id);
							property.set(entity, toOneEntity);
						}
					}
				);
				
				futures.add(future);
			}
			
			if (!joinCases.isEmpty())
				futures.add(context.fetchPropertyEntities(entityNode, entityPropertyNode, joinCases, this::visit)); 
			
			if (futures.isEmpty())
				return;
			
			postProcess(CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])));
		}
		
	}
	
	private class EntityCollectionPropertyFetch extends EntityPostProcessing implements PropertyFetch {
		final EntityGraphNode entityNode;
		final EntityRelatedPropertyGraphNode entityRelatedPropertyGraphNode;
		final Map<Object, GenericEntity> entities;
		
		public EntityCollectionPropertyFetch(EntityGraphNode entityNode, EntityCollectionPropertyGraphNode entityCollectionPropertyNode, Map<Object, GenericEntity> entities) {
			super(entityCollectionPropertyNode.entityNode(), new FetchPathNode(task.fetchPath, entityCollectionPropertyNode));
			this.entityNode = entityNode;
			this.entityRelatedPropertyGraphNode = entityCollectionPropertyNode;
			this.entities = entities;
		}

		@Override
		public void fetch() {
			postProcess(context.fetchPropertyEntities(entityNode, entityRelatedPropertyGraphNode, entities, this::visit));
		}
	}
	
	private class ScalarCollectionPropertyFetch implements PropertyFetch {
		ScalarCollectionPropertyGraphNode scalarCollectionPropertyNode;
		private EntityGraphNode entityNode;
		private Map<Object, GenericEntity> entities;
		
		public ScalarCollectionPropertyFetch(EntityGraphNode entityNode, ScalarCollectionPropertyGraphNode scalarCollectionPropertyNode, Map<Object, GenericEntity> entities) {
			super();
			this.entityNode = entityNode;
			this.scalarCollectionPropertyNode = scalarCollectionPropertyNode;
			this.entities = entities;
		}
		
		@Override
		public void fetch() {
			context.fetchScalarPropertyCollections(entityNode, scalarCollectionPropertyNode, entities) //
				.whenComplete((r,ex) -> {
					if (ex != null)
						context.notifyError(ex);
				});
		}
	}
	
	private class MapPropertyFetch implements PropertyFetch {
		MapPropertyGraphNode mapPropertyNode;
		private EntityGraphNode entityNode;
		private EntityPostProcessing keyPostProcessing;
		private EntityPostProcessing valuePostProcessing;
		private Map<Object, GenericEntity> entities;
		
		public MapPropertyFetch(EntityGraphNode entityNode, MapPropertyGraphNode mapPropertyNode, Map<Object, GenericEntity> entities) {
			super();
			this.entityNode = entityNode;
			this.mapPropertyNode = mapPropertyNode;
			this.entities = entities;
			
			AbstractEntityGraphNode keyNode = mapPropertyNode.keyNode();
			AbstractEntityGraphNode valueNode = mapPropertyNode.valueNode();
			
			FetchPathNode fetchPath = new FetchPathNode(task.fetchPath, mapPropertyNode);
			
			if (keyNode != null)
				keyPostProcessing = new EntityPostProcessing(keyNode, fetchPath);
			
			if (valueNode != null)
				valuePostProcessing = new EntityPostProcessing(valueNode, fetchPath);
		}
		
		@Override
		public void fetch() {
			Consumer<EntityIdm> keyVisitor = keyPostProcessing != null? keyPostProcessing::visit: null; 
			Consumer<EntityIdm> valueVisitor = valuePostProcessing != null? valuePostProcessing::visit: null; 
					
			postProcess(context.fetchMap(entityNode, mapPropertyNode, entities, keyVisitor, valueVisitor));
		}
		
		private void postProcess(CompletableFuture<?> future) {
			context.observe(future.thenRun(this::enqueue));
		}
		
		private void enqueue() {
			if (keyPostProcessing != null)
				keyPostProcessing.enqueue();
			
			if (valuePostProcessing != null)
				valuePostProcessing.enqueue();
		}
	}
}
