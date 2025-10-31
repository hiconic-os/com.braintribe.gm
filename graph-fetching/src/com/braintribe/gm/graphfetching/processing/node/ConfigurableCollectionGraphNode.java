package com.braintribe.gm.graphfetching.processing.node;

import com.braintribe.gm.graphfetching.api.node.CollectionPropertyGraphNode;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.LinearCollectionType;
import com.braintribe.model.generic.reflection.Property;

public abstract class ConfigurableCollectionGraphNode implements CollectionPropertyGraphNode {
	private Property property;
	private LinearCollectionType collectionType;
	
	public ConfigurableCollectionGraphNode(Property property) {
		this.property = property;
		this.collectionType = (LinearCollectionType)property.getType();
	}

	@Override
	public String name() {
		return property.getName();
	}

	@Override
	public GenericModelType type() {
		return property.getType();
	}

	@Override
	public GenericModelType condensedType() {
		return collectionType.getCollectionElementType();
	}

	@Override
	public LinearCollectionType collectionType() {
		return collectionType;
	}
	
	@Override
	public Property property() {
		return property;
	}

}
