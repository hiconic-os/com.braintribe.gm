package com.braintribe.gm.graphfetching.processing.fetch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import com.braintribe.gm.graphfetching.api.node.EntityCollectionPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.FetchQualification;
import com.braintribe.gm.graphfetching.api.node.MapPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.ScalarCollectionPropertyGraphNode;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.AbsentEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.VdHolder;
import com.braintribe.model.generic.value.ValueDescriptor;

public class FlatFetching {
	private final FetchContext context;
	private final boolean supportsSubTypeJoin;
	private FetchTask task;

	public FlatFetching(FetchContext context, FetchTask task) {
		super();
		this.context = context;
		this.task = task;
		this.supportsSubTypeJoin = context.queryFactory().supportsSubTypeJoin();
	}
	
	public void fetch() {
		List<PropertyFetch> propertyFetches = new ArrayList<>(); 
		
		for (EntityGraphNode entityNode: task.node.entityNodes()) {
			for (EntityPropertyGraphNode entityPropertyNode : entityNode.entityProperties().values())
				propertyFetches.add(new EntityPropertyFetch(entityNode, entityPropertyNode));
			
			for (EntityCollectionPropertyGraphNode entityCollectionPropertyNode : entityNode.entityCollectionProperties().values())
				propertyFetches.add(new EntityCollectionPropertyFetch(entityCollectionPropertyNode));
			
			for (ScalarCollectionPropertyGraphNode scalarCollectionPropertyNode : entityNode.scalarCollectionProperties().values())
				propertyFetches.add(new ScalarCollectionPropertyFetch(scalarCollectionPropertyNode));
			
			for (MapPropertyGraphNode mapPropertyNode : entityNode.mapProperties().values())
				propertyFetches.add(new MapPropertyFetch(mapPropertyNode));
				
		}
		
		context.processParallel(propertyFetches, PropertyFetch::fetch, () -> { /* TODO */ });
	}
	
	private abstract class PropertyFetch {
		public abstract void fetch();
	}
	
	private class EntityPropertyFetch extends PropertyFetch {
		private EntityPropertyGraphNode entityPropertyNode;
		private EntityGraphNode entityNode;
		private Map<Object, GenericEntity> newEntities = new HashMap<>();

		public EntityPropertyFetch(EntityGraphNode entityNode, EntityPropertyGraphNode entityPropertyNode) {
			super();
			this.entityNode = entityNode;
			this.entityPropertyNode = entityPropertyNode;
		}
		
		@Override
		public void fetch() {
			EntityType<?> baseType = entityPropertyNode.entityNode().entityType();
			Property property = entityPropertyNode.property();
			
			Map<GenericEntity, Object> lookupCases = new IdentityHashMap<>();
			
			Map<Object, GenericEntity> joinCases = new HashMap<>();
			
			for (GenericEntity entity: task.entities.values()) {
				EntityType<?> entityType = entity.entityType();
				EntityType<?> declaringType = property.getDeclaringType();
				if (baseType != entityType && declaringType.isInstance(entity))
					continue;
				
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
				
				property.setDirect(entity, null);
			}
			
			FetchQualification allQualification = entityPropertyNode.entityNode().allQualification();
			
			
			
			if (!lookupCases.isEmpty()) {
				context.fetchEntities(entityPropertyNode.entityNode(), new HashSet<>(lookupCases.values()),
						// visitor
						lookupEntities -> {
							for (Map.Entry<GenericEntity, Object> entry : lookupCases.entrySet()) {
								GenericEntity entity = entry.getKey();
								Object id = entry.getValue();
								GenericEntity toOneEntity = lookupEntities.get(id);
								property.set(entity, toOneEntity);
							}
						},
						// onDone
						entityIdm -> {
							if (entityIdm.addHandled(allQualification)) {
								GenericEntity entity = entityIdm.entity;
								newEntities.put(entity.getId(), entity);
							}
						}
				);
			}
			
			if (!joinCases.isEmpty()) {
				context.fetchPropertyEntities(entityNode, entityPropertyNode, joinCases, 
						// visitor
						entityIdm -> {
							
						}, 
						// onDone
						() -> {
					
						}
				);
			}
		}
		
	}
	
	private class EntityCollectionPropertyFetch extends PropertyFetch {
		EntityCollectionPropertyGraphNode entityCollectionPropertyNode;
		EntityGraphNode entityNode;
		
		public EntityCollectionPropertyFetch(EntityGraphNode entityNode, EntityCollectionPropertyGraphNode entityCollectionPropertyNode) {
			super();
			this.entityNode = entityNode;
			this.entityCollectionPropertyNode = entityCollectionPropertyNode;
		}

		@Override
		public void fetch() {
			context.fetchPropertyEntities(entityNode, entityCollectionPropertyNode, task.entities,
					// visitor
					entityIdm -> {
						
					}, 
					// onDone
					() -> {
						
					});
		}
	}
	
	private class ScalarCollectionPropertyFetch extends PropertyFetch {
		ScalarCollectionPropertyGraphNode scalarCollectionPropertyNode;
		
		public ScalarCollectionPropertyFetch(ScalarCollectionPropertyGraphNode scalarCollectionPropertyNode) {
			super();
			this.scalarCollectionPropertyNode = scalarCollectionPropertyNode;
		}
		
		@Override
		public void fetch() {
			
		}
	}
	
	private class MapPropertyFetch extends PropertyFetch {
		MapPropertyGraphNode mapPropertyNode;
		
		public MapPropertyFetch(MapPropertyGraphNode mapPropertyNode) {
			super();
			this.mapPropertyNode = mapPropertyNode;
		}
		
		@Override
		public void fetch() {
			
		}
	}
}
