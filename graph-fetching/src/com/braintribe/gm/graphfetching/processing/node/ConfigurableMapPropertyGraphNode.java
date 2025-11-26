package com.braintribe.gm.graphfetching.processing.node;

import java.util.ArrayList;
import java.util.List;

import com.braintribe.gm.graphfetching.api.node.AbstractEntityGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityGraphNode;
import com.braintribe.gm.graphfetching.api.node.InferableGraphNode;
import com.braintribe.gm.graphfetching.api.node.KeyValueType;
import com.braintribe.gm.graphfetching.api.node.MapPropertyGraphNode;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.MapType;
import com.braintribe.model.generic.reflection.Property;

public class ConfigurableMapPropertyGraphNode extends ConfigurablePropertyGraphNode implements MapPropertyGraphNode {
	private AbstractEntityGraphNode keyNode;
	private AbstractEntityGraphNode valueNode;
	private KeyValueType keyValueType;

	public ConfigurableMapPropertyGraphNode(Property property) {
		super(property);
	}
	
	public ConfigurableMapPropertyGraphNode(Property property, List<InferableGraphNode> subNodes) {
		super(property);
		
		MapType mapType = type();
		
		GenericModelType keyType = mapType.getKeyType();
		GenericModelType valueType = mapType.getValueType();
		
		EntityType<?> inferedKeyEntityType = keyType.isEntity()? (EntityType<?>)keyType: null;
		EntityType<?> inferedValueEntityType = valueType.isEntity()? (EntityType<?>)valueType: null;
		
		if (inferedKeyEntityType != null)
			if (inferedValueEntityType != null)
				keyValueType = KeyValueType.ENTITY_ENTITY;
			else
				keyValueType = KeyValueType.ENTITY_SCALAR;
		else
			if (inferedValueEntityType != null)
				keyValueType = KeyValueType.SCALAR_ENTITY;
			else
				keyValueType = KeyValueType.SCALAR_SCALAR;
			

		List<ConfigurableEntityGraphNode> keyNodes = new ArrayList<>();
		List<ConfigurableEntityGraphNode> valueNodes = new ArrayList<>();
		
		for (InferableGraphNode node: subNodes) {
			switch (node.name()) {
				case "KEY":
					for (InferableGraphNode keyNode: node.subNodes())
						keyNodes.add(createEntityGraphNode(inferedKeyEntityType, keyNode));
					break;
				case "VALUE":
					for (InferableGraphNode valueNode: node.subNodes())
						valueNodes.add(createEntityGraphNode(inferedValueEntityType, valueNode));
					break;
				default:
					valueNodes.add(createEntityGraphNode(inferedValueEntityType, node));
					break;
			}
		}
		
		keyNode = buildEntityNodeOptional(inferedKeyEntityType, keyNodes);
		valueNode = buildEntityNodeOptional(inferedValueEntityType, valueNodes);
	}
	
	private AbstractEntityGraphNode buildEntityNodeOptional(EntityType<?> baseType, List<ConfigurableEntityGraphNode> nodes) {
		switch (nodes.size()) {
		case 0: return null;
		case 1: return nodes.iterator().next();
		default:
			ConfigurablePolymorphicEntityGraphNode polyNode = new ConfigurablePolymorphicEntityGraphNode(baseType);
			for (EntityGraphNode node: nodes) 
				polyNode.addEntityNode(node);
			
			return polyNode;
		}
	}
	
	@Override
	public KeyValueType keyValueType() {
		return keyValueType;
	}
	
	private ConfigurableEntityGraphNode createEntityGraphNode(EntityType<?> inferedType, InferableGraphNode node) {
		if (inferedType == null)
			throw new IllegalArgumentException("scalar map key/value types do not allow sub nodes");
		
		EntityType<?> entityType = node.entityType();
		if (entityType == null)
			entityType = inferedType;
		return new ConfigurableEntityGraphNode(entityType, node.subNodes());
	}

	@Override
	public MapType type() {
		return (MapType)super.type();
	}
	
	public void setKeyNode(AbstractEntityGraphNode keyNode) {
		this.keyNode = keyNode;
	}
	
	public void setValueNode(AbstractEntityGraphNode valueNode) {
		this.valueNode = valueNode;
	}

	@Override
	public AbstractEntityGraphNode keyNode() {
		return keyNode;
	}
	
	@Override
	public AbstractEntityGraphNode valueNode() {
		return valueNode;
	}
}
