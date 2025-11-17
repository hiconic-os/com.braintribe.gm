package com.braintribe.gm.graphfetching.processing.node;

import java.util.ArrayList;
import java.util.List;

import com.braintribe.gm.graphfetching.api.node.EntityGraphNode;
import com.braintribe.gm.graphfetching.api.node.InferableGraphNode;
import com.braintribe.gm.graphfetching.api.node.MapPropertyGraphNode;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.MapType;
import com.braintribe.model.generic.reflection.Property;

public class ConfigurableMapPropertyGraphNode extends ConfigurablePropertyGraphNode implements MapPropertyGraphNode {
	private List<EntityGraphNode> keyNodes = new ArrayList<>();
	private List<EntityGraphNode> valueNodes = new ArrayList<>();

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
		
		for (InferableGraphNode node: subNodes) {
			switch (node.name()) {
				case "KEY":
					for (InferableGraphNode keyNode: node.subNodes())
						addKeyNode(createEntityGraphNode(inferedKeyEntityType, keyNode));
					break;
				case "VALUE":
					for (InferableGraphNode valueNode: node.subNodes())
						addValueNode(createEntityGraphNode(inferedValueEntityType, valueNode));
					break;
				default:
					addValueNode(createEntityGraphNode(inferedValueEntityType, node));
					break;
			}
		}
	}
	
	private ConfigurableEntityGraphNode createEntityGraphNode(EntityType<?> inferedType, InferableGraphNode node) {
		if (inferedType == null)
			throw new IllegalArgumentException("scalar map key/value types do not allow sub nodes");
		
		EntityType<?> entityType = node.entityType();
		if (entityType == null)
			entityType = inferedType;
		return new ConfigurableEntityGraphNode(entityType, node.subNodes());
	}

	public void addKeyNode(EntityGraphNode node) {
		keyNodes.add(node);
	}
	
	public void addValueNode(EntityGraphNode node) {
		valueNodes.add(node);
	}
	
	@Override
	public MapType type() {
		return (MapType)super.type();
	}

	@Override
	public List<EntityGraphNode> keyNodes() {
		return keyNodes;
	}
	
	@Override
	public List<EntityGraphNode> valueNodes() {
		return valueNodes;
	}

}
