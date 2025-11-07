package com.braintribe.gm.graphfetching.processing.node;

import com.braintribe.gm.graphfetching.api.node.EntityGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityRelatedPropertyGraphNode;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.Property;

public class ConfigurableEntityRelatedPropertyGraphNode extends ConfigurablePropertyGraphNode implements EntityRelatedPropertyGraphNode {

	private EntityGraphNode entityNode;

	public ConfigurableEntityRelatedPropertyGraphNode(Property property, EntityGraphNode entityNode) {
		super(property);
		this.entityNode = entityNode;
	}

	@Override
	public EntityGraphNode entityNode() {
		return entityNode;
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
		return (EntityType<?>)super.condensedPropertyType();
	}
}
