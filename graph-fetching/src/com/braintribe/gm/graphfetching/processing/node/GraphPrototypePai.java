package com.braintribe.gm.graphfetching.processing.node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.braintribe.gm.graphfetching.api.node.EntityCollectionPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityPropertyGraphNode;
import com.braintribe.gm.graphfetching.processing.util.PrototypeMap;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.collection.LinearCollectionBase;
import com.braintribe.model.generic.enhance.FieldAccessingPropertyAccessInterceptor;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.LinearCollectionType;
import com.braintribe.model.generic.reflection.ListType;
import com.braintribe.model.generic.reflection.MapType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.PropertyAccessInterceptor;
import com.braintribe.model.generic.reflection.SetType;
import com.braintribe.model.generic.reflection.VdHolder;
import com.braintribe.model.generic.value.Escape;

public class GraphPrototypePai extends PropertyAccessInterceptor {
	
	public final static GraphPrototypePai INSTANCE = new GraphPrototypePai(); 
	
	private GraphPrototypePai() {
		next = FieldAccessingPropertyAccessInterceptor.INSTANCE;
	}
	
	private List<GenericEntity> getEntityPropertyMultiplex(Property property, GenericEntity entity) {
		Escape escape = (Escape) next.getProperty(property, entity, true);
		
		if (escape != null)
			return (List<GenericEntity>) escape.getValue();

		escape = Escape.T.create();
		final List<GenericEntity> entities = new ArrayList<>();
		escape.setValue(entities);
		next.setProperty(property, entity, escape, true);
		return entities;
	}
	
	@Override
	public Object setProperty(Property property, GenericEntity entity, Object value, boolean isVd) {
		switch (property.getType().getTypeCode()) {
		case entityType:
			final List<GenericEntity> entities = getEntityPropertyMultiplex(property, entity);
			
			entities.add((GenericEntity)value);
			
			if (entities.size() == 1)
				return null;
			
			return entities.get(entities.size() - 2);
		case mapType:
			Map<Object, Object> map = (Map<Object,Object>)value;
			
			Map<Object, Object> managedMap = (Map<Object,Object>)getProperty(property, entity, isVd);
			managedMap.clear();
			
			if (map != null)
				managedMap.putAll(map);
			return managedMap;
		default:
			return next.setProperty(property, entity, value, isVd);
		}
	}

	private Object createDefault(GenericModelType type) {
		switch (type.getTypeCode()) {
			case entityType: return createDefaultEntity((EntityType<?>)type);
			case setType: return createDefaultCollection((SetType)type);
			case listType: return createDefaultCollection((ListType)type);
			case mapType: return createDefaultMap((MapType)type);
			default: return type.getDefaultValue();
		}
	}
	
	private GenericEntity createDefaultEntity(EntityType<?> type) {
		return type.isAbstract()? null: type.create(GraphPrototypePai.INSTANCE);
	}
	
	private Map<?, ?> createDefaultMap(MapType mapType) {
		GenericModelType keyType = mapType.getKeyType();
		GenericModelType valueType = mapType.getValueType();
		
		Object key = createDefault(keyType);
		Object value = createDefault(valueType);
		
		PrototypeMap map = new PrototypeMap();
		
		map.put(key, value);
		
		return map;
	}
	
	private Collection<?> createDefaultCollection(LinearCollectionType type) {
		GenericModelType elementType = type.getCollectionElementType();
		LinearCollectionBase<Object> collection = type.createPlain();
		collection.add(createDefault(elementType));
		return collection;
	}
	
	@Override
	public Object getProperty(Property property, GenericEntity entity, boolean isVd) {
		if (isVd)
			return next.getProperty(property, entity, isVd);
		
		GenericModelType propertyType = property.getType();
		if (propertyType.isEntity()) {
			List<GenericEntity> entities = getEntityPropertyMultiplex(property, entity);
			
			if (!entities.isEmpty())
				return entities.get(0);
			
			GenericEntity e = createDefaultEntity((EntityType<?>)propertyType);
			
			entities.add(e);
			return e;
		}
		else {
			Object value = super.getProperty(property, entity, isVd);
			if (value != null)
				return value;

			GenericModelType type = property.getType();
			value = createDefault(type);
			
			super.setProperty(property, entity, value, isVd);
			
			return value;
		}
	}
	
	public static EntityGraphNode convert(GenericEntity entity) {
		ConfigurableEntityGraphNode node = new ConfigurableEntityGraphNode(entity.entityType());
		fillProperties(node, entity);
		
		return node;
	}
	
	private static EntityPropertyGraphNode toEntityPropertyGraphNode(Property property, GenericEntity entity) {
		return new ConfigurableEntityPropertyGraphNode(property, convert(entity));
	}
	
	private static EntityCollectionPropertyGraphNode toEntityCollectionGraphNode(Property property, GenericEntity entity) {
		return new ConfigurableEntityCollectionPropertyGraphNode(property, convert(entity));
	}
	
	private static void fillProperties(ConfigurableEntityGraphNode node, GenericEntity entity) {
		EntityType<GenericEntity> entityType = entity.entityType();
		
		for (Property property: entityType.getProperties()) {
			GenericModelType propertyType = property.getType();

			switch (propertyType.getTypeCode()) {
			
				case entityType: {
					Object value = property.getDirectUnsafe(entity);
					
					if (value != null) {
						Escape escape = (Escape)VdHolder.getValueDescriptorIfPossible(value);
						
						if (escape != null) {
							List<GenericEntity> entities = (List<GenericEntity>) escape.getValue();
							for (GenericEntity e: entities)
								node.add(toEntityPropertyGraphNode(property, e));
						}
						else
							node.add(toEntityPropertyGraphNode(property, (GenericEntity)value));
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
						Collection<? extends GenericEntity> collection = (Collection<? extends GenericEntity>)value;
						
						for (GenericEntity entityElement: collection) {
							node.add(toEntityCollectionGraphNode(property, entityElement));	
						}
					}
					
					break;
				}
				
				default:
					break;
			}
		}
	}
}
