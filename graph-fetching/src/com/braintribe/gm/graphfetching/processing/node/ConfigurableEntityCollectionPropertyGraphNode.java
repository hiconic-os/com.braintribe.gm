package com.braintribe.gm.graphfetching.processing.node;

import java.util.List;

import com.braintribe.gm.graphfetching.api.node.AbstractEntityGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityCollectionPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.InferableGraphNode;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.LinearCollectionType;
import com.braintribe.model.generic.reflection.Property;

public class ConfigurableEntityCollectionPropertyGraphNode extends ConfigurableEntityRelatedPropertyGraphNode implements EntityCollectionPropertyGraphNode {
	public ConfigurableEntityCollectionPropertyGraphNode(Property property, AbstractEntityGraphNode entityNode) {
		super(property, entityNode);
	}
	
	public ConfigurableEntityCollectionPropertyGraphNode(Property property, EntityType<?> entityType, List<InferableGraphNode> subNodes) {
		super(property, new ConfigurableEntityGraphNode(entityType, subNodes));
	}
	
	public ConfigurableEntityCollectionPropertyGraphNode(Property property, EntityType<?> entityType) {
		super(property, new ConfigurableEntityGraphNode(entityType));
	}
	
	@Override
	public LinearCollectionType type() {
		return (LinearCollectionType)super.type();
	}

}
