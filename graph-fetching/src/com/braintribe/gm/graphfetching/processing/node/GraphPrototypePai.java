package com.braintribe.gm.graphfetching.processing.node;

import java.util.Collection;

import com.braintribe.gm.graphfetching.api.node.EntityCollectionPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityPropertyGraphNode;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.collection.LinearCollectionBase;
import com.braintribe.model.generic.enhance.FieldAccessingPropertyAccessInterceptor;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.LinearCollectionType;
import com.braintribe.model.generic.reflection.ListType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.PropertyAccessInterceptor;
import com.braintribe.model.generic.reflection.SetType;

public class GraphPrototypePai extends PropertyAccessInterceptor {
	
	public final static GraphPrototypePai INSTANCE = new GraphPrototypePai(); 
	
	private GraphPrototypePai() {
		next = FieldAccessingPropertyAccessInterceptor.INSTANCE;
	}
	
	@Override
	public Object setProperty(Property property, GenericEntity entity, Object value, boolean isVd) {
		return super.setProperty(property, entity, value, isVd);
	}

	private Object createDefault(GenericModelType type) {
		switch (type.getTypeCode()) {
			case entityType: return createDefaultEntity((EntityType<?>)type);
			case setType: return createDefaultCollection((SetType)type);
			case listType: return createDefaultCollection((ListType)type);
			default: return type.getDefaultValue();
		}
	}
	
	private GenericEntity createDefaultEntity(EntityType<?> type) {
		return type.isAbstract()? null: type.create(GraphPrototypePai.INSTANCE);
	}
	
	private Collection<?> createDefaultCollection(LinearCollectionType type) {
		GenericModelType elementType = type.getCollectionElementType();
		LinearCollectionBase<Object> collection = type.createPlain();
		collection.add(createDefault(elementType));
		return collection;
	}
	
	@Override
	public Object getProperty(Property property, GenericEntity entity, boolean isVd) {
		Object value = super.getProperty(property, entity, isVd);
		if (value != null)
			return value;

		GenericModelType type = property.getType();
		value = createDefault(type);
		
		super.setProperty(property, entity, value, isVd);
		
		return value;
	}
	
	public static EntityGraphNode convert(GenericEntity entity) {
		ConfigurableEntityGraphNode node = new ConfigurableEntityGraphNode(entity.entityType());
		fillProperties(node, entity);
		
		return node;
	}
	
	private static EntityPropertyGraphNode toEntityPropertyGraphNode(Property property, GenericEntity entity) {
		ConfigurableEntityPropertyGraphNode node = new ConfigurableEntityPropertyGraphNode(property, entity.entityType());
		fillProperties(node, entity);
		
		return node;
	}
	
	private static EntityCollectionPropertyGraphNode toEntityCollectionGraphNode(Property property, GenericEntity entity) {
		ConfigurableEntityCollectionPropertyGraphNode node = new ConfigurableEntityCollectionPropertyGraphNode(property, entity.entityType());
		fillProperties(node, entity);
		
		return node;
	}
	
	private static void fillProperties(ConfigurableEntityGraphNode node, GenericEntity entity) {
		EntityType<GenericEntity> entityType = entity.entityType();
		
		for (Property property: entityType.getProperties()) {
			GenericModelType propertyType = property.getType();

			switch (propertyType.getTypeCode()) {
			
				case entityType: {
					Object value = property.getDirectUnsafe(entity);
					if (value != null)
						node.add(toEntityPropertyGraphNode(property, (GenericEntity)value));
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
						Collection<?> collection = (Collection<?>)value;
						if (collection.isEmpty())
							break;
						
						GenericEntity entityElement = (GenericEntity)collection.iterator().next();
						node.add(toEntityCollectionGraphNode(property, entityElement));
					}
					
					break;
				}
				
				default:
					break;
			}
		}
	}
}
