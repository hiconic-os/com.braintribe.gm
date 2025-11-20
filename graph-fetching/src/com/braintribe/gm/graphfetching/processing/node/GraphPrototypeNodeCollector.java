package com.braintribe.gm.graphfetching.processing.node;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.braintribe.common.lcd.Pair;
import com.braintribe.gm.graphfetching.api.node.AbstractEntityGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityCollectionPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.PolymorphicEntityGraphNode;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.LinearCollectionType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.VdHolder;
import com.braintribe.model.generic.value.Escape;

public class GraphPrototypeNodeCollector {
	private Map<GenericEntity, EntityGraphNode> entityNodes = new IdentityHashMap<>();
	private Map<Pair<EntityType<?>, Set<GenericEntity>>, PolymorphicEntityGraphNode> polymorphicEntityNodes = new IdentityHashMap<>();
	
	public EntityGraphNode toEntityNode(GenericEntity entity) {
		EntityGraphNode existing = entityNodes.get(entity);
		
		if (existing != null)
			return existing;
		
		ConfigurableEntityGraphNode entityNode = new ConfigurableEntityGraphNode(entity.entityType());
		entityNodes.put(entity, entityNode);
		
		fillProperties(entityNode, entity);
		
		return entityNode;
	}
	
	public AbstractEntityGraphNode toEntityNode(EntityType<?> baseType, Collection<? extends GenericEntity> entities) {
		switch (entities.size()) {
		case 0:
			throw new IllegalArgumentException("AbstractEntityGraphNode needs at least one entity as description");
		case 1:
			return toEntityNode(entities.iterator().next());
		default:
			return toPolymorphicEntityNode(baseType, entities);
		}
	}
	
	public PolymorphicEntityGraphNode toPolymorphicEntityNode(EntityType<?> baseType, Collection<? extends GenericEntity> entities) {
		Pair<EntityType<?>, Set<GenericEntity>> key = Pair.of(baseType, new HashSet<>(entities));
		
		PolymorphicEntityGraphNode existing = polymorphicEntityNodes.get(key);
		
		if (existing != null)
			return existing;
		
		ConfigurablePolymorphicEntityGraphNode polymorphicEntityNode = new ConfigurablePolymorphicEntityGraphNode(baseType);
		polymorphicEntityNodes.put(key, polymorphicEntityNode);
		
		for (GenericEntity entity: entities) {
			polymorphicEntityNode.addEntityNode(toEntityNode(entity));
		}
		
		return polymorphicEntityNode;
	}
	
	private EntityPropertyGraphNode toEntityPropertyGraphNode(Property property, EntityType<?> baseType, Collection<? extends GenericEntity> entities) {
		return new ConfigurableEntityPropertyGraphNode(property, toEntityNode(baseType, entities));
	}
	
	private EntityCollectionPropertyGraphNode toEntityCollectionPropertyGraphNode(Property property, EntityType<?> baseType, Collection<? extends GenericEntity> entities) {
		return new ConfigurableEntityCollectionPropertyGraphNode(property, toEntityNode(baseType, entities));
	}
	
	private void fillProperties(ConfigurableEntityGraphNode node, GenericEntity entity) {
		EntityType<GenericEntity> entityType = entity.entityType();
		
		for (Property property: entityType.getProperties()) {
			GenericModelType propertyType = property.getType();

			switch (propertyType.getTypeCode()) {
			
				case entityType: {
					Object value = property.getDirectUnsafe(entity);
					
					if (value != null) {
						EntityType<?> entityPropertyType = (EntityType<?>)propertyType;
						Escape escape = (Escape)VdHolder.getValueDescriptorIfPossible(value);
						final List<GenericEntity> entities;
						
						if (escape != null)
							entities = (List<GenericEntity>) escape.getValue();
						else
							entities = Collections.singletonList((GenericEntity)value);
						
						node.add(toEntityPropertyGraphNode(property, entityPropertyType, entities));
					}
					break;
				}
				
				case listType:
				case setType: {
					Object value = property.getDirectUnsafe(entity);
					if (value == null)
						break;
					
					LinearCollectionType linearCollectionType = (LinearCollectionType)propertyType;
					GenericModelType elementType = linearCollectionType.getCollectionElementType();
					if (elementType.isScalar()) {
						node.add(new ConfigurableScalarCollectionPropertyGraphNode(property));
					}
					else {
						EntityType<?> entityElementType = (EntityType<?>)elementType;
						Collection<? extends GenericEntity> entities = (Collection<? extends GenericEntity>)value;
						
						node.add(toEntityCollectionPropertyGraphNode(property, entityElementType, entities));
					}
					
					break;
				}
				
				default:
					break;
			}
		}
	}

}
