package com.braintribe.gm.graphfetching.processing.node;

import java.util.List;

import com.braintribe.gm.graphfetching.api.node.EntityGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.InferableGraphNode;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.Property;

public class ConfigurableEntityPropertyGraphNode extends ConfigurableEntityRelatedPropertyGraphNode implements EntityPropertyGraphNode {

	public ConfigurableEntityPropertyGraphNode(Property property, EntityGraphNode entityNode) {
		super(property, entityNode);
	}
	
	public ConfigurableEntityPropertyGraphNode(Property property, List<InferableGraphNode> subNodes) {
		this(property, (EntityType<?>)property.getType(), subNodes);
	}
	
	public ConfigurableEntityPropertyGraphNode(Property property, EntityType<?> entityType) {
		super(property, new ConfigurableEntityGraphNode(entityType));
	}
	
	public ConfigurableEntityPropertyGraphNode(Property property, EntityType<?> entityType, List<InferableGraphNode> subNodes) {
		super(property, new ConfigurableEntityGraphNode(entityType, subNodes));
	}
}
