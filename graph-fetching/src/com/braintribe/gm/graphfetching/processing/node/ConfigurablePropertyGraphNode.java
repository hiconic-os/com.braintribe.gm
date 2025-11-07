package com.braintribe.gm.graphfetching.processing.node;

import com.braintribe.gm.graphfetching.api.node.PropertyGraphNode;
import com.braintribe.model.generic.reflection.CollectionType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;

public abstract class ConfigurablePropertyGraphNode implements PropertyGraphNode {
	private Property property;

	public ConfigurablePropertyGraphNode(Property property) {
		super();
		this.property = property;
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
	public GenericModelType condensedPropertyType() {
		GenericModelType type = property.getType();
		if (type.isCollection())
			return ((CollectionType)type).getCollectionElementType();
		
		return type;
	}
	
	@Override
	public GenericModelType condensedType() {
		return condensedPropertyType();
	}
	
	@Override
	public GenericModelType type() {
		return property.getType();
	}
	
	@Override
	public String toString() {
		return name();
	}
	
	@Override
	public String stringify() {
		return name();
	}

}
