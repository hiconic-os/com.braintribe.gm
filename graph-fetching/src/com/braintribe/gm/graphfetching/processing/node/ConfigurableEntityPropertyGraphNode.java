package com.braintribe.gm.graphfetching.processing.node;

import java.util.List;

import com.braintribe.gm.graphfetching.api.node.EntityPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.UntypedGraphNode;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.Property;

public class ConfigurableEntityPropertyGraphNode extends ConfigurableEntityGraphNode implements EntityPropertyGraphNode {

	private Property property;

	public ConfigurableEntityPropertyGraphNode(Property property, List<UntypedGraphNode> subNodes) {
		this(property, (EntityType<?>)property.getType(), subNodes);
	}
	
	public ConfigurableEntityPropertyGraphNode(Property property, EntityType<?> entityType) {
		super(entityType);
		this.property = property;
	}
	
	public ConfigurableEntityPropertyGraphNode(Property property, EntityType<?> entityType, List<UntypedGraphNode> subNodes) {
		super(entityType, subNodes);
		this.property = property;
	}
	
	@Override
	public Property property() {
		return property;
	}
	
	@Override
	public String toString() {
		return property.getName();
	}
}
