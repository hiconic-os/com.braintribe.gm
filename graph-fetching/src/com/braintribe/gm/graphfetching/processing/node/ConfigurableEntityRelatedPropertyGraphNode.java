package com.braintribe.gm.graphfetching.processing.node;

import com.braintribe.gm.graphfetching.api.node.EntityGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityRelatedPropertyGraphNode;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.LinearCollectionType;
import com.braintribe.model.generic.reflection.Property;

public class ConfigurableEntityRelatedPropertyGraphNode implements EntityRelatedPropertyGraphNode {

	private Property property;
	private EntityGraphNode entityNode;

	public ConfigurableEntityRelatedPropertyGraphNode(Property property, EntityGraphNode entityNode) {
		this.property = property;
		this.entityNode = entityNode;
	}

	@Override
	public EntityGraphNode entityNode() {
		return entityNode;
	}
	
	@Override
	public String name() {
		return property.getName();
	}
	
	@Override
	public Property property() {
		return property;
	}
	
	@Override
	public GenericModelType type() {
		return property.getType();
	}

	@Override
	public EntityType<?> condensedType() {
		return entityNode.entityType();
	}

	@Override
	public String stringify() {
		return null;
	}
	
	@Override
	public EntityType<?> condensedPropertyType() {
		GenericModelType propertyType = property.getType();
		if (propertyType.isEntity()) {
			return (EntityType<?>)propertyType;
		}
		
		LinearCollectionType collectionType = (LinearCollectionType) propertyType;
		return (EntityType<?>)collectionType.getCollectionElementType();
	}
	
	@Override
	public String toString() {
		return property.getName();
	}
}
