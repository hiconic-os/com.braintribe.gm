package com.braintribe.gm.graphfetching.processing.fetch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.braintribe.gm.graphfetching.api.node.EntityCollectionPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.ScalarCollectionPropertyGraphNode;
import com.braintribe.gm.graphfetching.processing.util.FetchingTools;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.LinearCollectionType;
import com.braintribe.model.generic.reflection.Property;

public class LocalFetching {

	private Queue<Runnable> taskQueue = new LinkedList<>();
	private Map<GenericEntity, DetachedEntity> detachedEntities = new IdentityHashMap<>();
	
	public List<GenericEntity> fetch(EntityGraphNode node, Collection<? extends GenericEntity> entities) {
		List<GenericEntity> clonedEntities = new ArrayList<>(entities.size());
		
		for (GenericEntity entity: entities) {
			taskQueue.offer(() -> {
				GenericEntity detachedEntity = process(node, entity);
				clonedEntities.add(detachedEntity);
			});
		}
		
		process();
		
		return clonedEntities;
	}
	
	private void process() {
		Runnable runnable = null;
		
		while ((runnable = taskQueue.poll()) != null) {
			runnable.run();
		}
	}
	
	private GenericEntity process(EntityGraphNode node, GenericEntity entity) {
		DetachedEntity detachedEntity = acquireDetached(entity);
		
		GenericEntity clonedEntity = detachedEntity.entity;
		
		if (detachedEntity.nodes.contains(node)) {
			return clonedEntity;
		}
		
		detachedEntity.nodes.add(node);
		
		for (EntityPropertyGraphNode entityPropertyNode: node.entityProperties()) {
			Property property = entityPropertyNode.property();
			GenericEntity otherEntity = property.get(entity);
			
			if (otherEntity != null) {
				taskQueue.offer(() -> {
					GenericEntity otherDetachedEntity = process(entityPropertyNode, otherEntity);
					property.set(clonedEntity, otherDetachedEntity);
				});
			}
		}
		
		for (EntityCollectionPropertyGraphNode entityCollectionPropertyNode: node.entityCollectionProperties()) {
			Property property = entityCollectionPropertyNode.property();
			
			Collection<GenericEntity> otherEntities = property.get(entity);
			LinearCollectionType type = (LinearCollectionType) property.getType();
			
			Collection<Object> clonedCollection = type.createPlain();
			property.set(clonedEntity, clonedCollection);
			
			for (GenericEntity otherEntity: otherEntities) {
				taskQueue.offer(() -> {
					GenericEntity otherDetachedEntity = process(entityCollectionPropertyNode, otherEntity);
					clonedCollection.add(otherDetachedEntity);
				});
			}
		}
		
		for (ScalarCollectionPropertyGraphNode scalarCollectionPropertyNode: node.scalarCollectionProperties()) {
			Property property = scalarCollectionPropertyNode.property();
			
			Collection<Object> otherElements = property.get(entity);
			LinearCollectionType type = (LinearCollectionType) property.getType();
			
			Collection<Object> clonedCollection = type.createPlain();
			clonedCollection.addAll(otherElements);
			property.set(clonedEntity, clonedCollection);
		}
		
		return clonedEntity;
	}
	
	private DetachedEntity acquireDetached(GenericEntity entity) {
		return detachedEntities.computeIfAbsent(entity, this::buildDetached);
	}
	
	private DetachedEntity buildDetached(GenericEntity entity) {
		return new DetachedEntity(FetchingTools.cloneDetachment(entity));
	}
	
	private static class DetachedEntity {
		GenericEntity entity;
		List<EntityGraphNode> nodes = new ArrayList<>(1);

		public DetachedEntity(GenericEntity entity) {
			super();
			this.entity = entity;
		}
	}
}
