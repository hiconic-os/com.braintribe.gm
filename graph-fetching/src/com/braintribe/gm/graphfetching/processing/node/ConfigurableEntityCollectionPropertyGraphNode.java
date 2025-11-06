package com.braintribe.gm.graphfetching.processing.node;

import java.util.List;

import com.braintribe.gm.graphfetching.api.node.EntityCollectionPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.InferableGraphNode;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.LinearCollectionType;
import com.braintribe.model.generic.reflection.Property;

public class ConfigurableEntityCollectionPropertyGraphNode extends ConfigurableEntityPropertyGraphNode implements EntityCollectionPropertyGraphNode {
	private LinearCollectionType collectionType;

	public ConfigurableEntityCollectionPropertyGraphNode(Property property, EntityType<?> entityType, List<InferableGraphNode> subNodes) {
		super(property, entityType, subNodes);
		this.collectionType = (LinearCollectionType)property.getType();
	}
	
	public ConfigurableEntityCollectionPropertyGraphNode(Property property, EntityType<?> entityType) {
		super(property, entityType);
		this.collectionType = (LinearCollectionType)property.getType();
	}

	@Override
	public LinearCollectionType collectionType() {
		return collectionType;
	}
	
	@Override
	public GenericModelType type() {
		return collectionType;
	}
	
	@Override
	public GenericModelType condensedPropertyType() {
		return collectionType.getCollectionElementType();
	}
}
