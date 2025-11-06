package com.braintribe.gm.graphfetching.processing.node;

import java.util.List;

import com.braintribe.gm.graphfetching.api.node.EntityPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.InferableGraphNode;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;

public class ConfigurableEntityPropertyGraphNode extends ConfigurableEntityGraphNode implements EntityPropertyGraphNode {

	private Property property;

	public ConfigurableEntityPropertyGraphNode(Property property, List<InferableGraphNode> subNodes) {
		this(property, (EntityType<?>)property.getType(), subNodes);
	}
	
	public ConfigurableEntityPropertyGraphNode(Property property, EntityType<?> entityType) {
		super(entityType);
		this.property = property;
	}
	
	public ConfigurableEntityPropertyGraphNode(Property property, EntityType<?> entityType, List<InferableGraphNode> subNodes) {
		super(entityType, subNodes);
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
		return property.getType();
	}
	
	@Override
	public String toString() {
		return property.getName();
	}
}
