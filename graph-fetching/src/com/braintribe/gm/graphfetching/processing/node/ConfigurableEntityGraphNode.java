package com.braintribe.gm.graphfetching.processing.node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.braintribe.gm.graphfetching.api.node.EntityCollectionPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.ScalarCollectionPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.UntypedGraphNode;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.LinearCollectionType;
import com.braintribe.model.generic.reflection.Property;

public class ConfigurableEntityGraphNode implements EntityGraphNode {
	private List<EntityPropertyGraphNode> entityProperties = new ArrayList<>();
	private List<EntityCollectionPropertyGraphNode> entityCollectionProperties = new ArrayList<>();
	private List<ScalarCollectionPropertyGraphNode> scalarCollectionProperties = new ArrayList<>();
	private EntityType<?> entityType;
	
	public ConfigurableEntityGraphNode(EntityType<?> entityType, UntypedGraphNode... subNodes) {
		this(entityType, Arrays.asList(subNodes));
	}
	
	public ConfigurableEntityGraphNode(EntityType<?> entityType, List<UntypedGraphNode> subNodes) {
		this(entityType);
		infer(subNodes);
	}
	
	public ConfigurableEntityGraphNode(EntityType<?> entityType) {
		this.entityType = entityType;
	}
	
	private void infer(List<UntypedGraphNode> subNodes) {
		for (UntypedGraphNode node: subNodes) {
			String propertyName = node.name();
			
			Property property = entityType.getProperty(propertyName);
			
			GenericModelType propertyType = property.getType();
			
			switch (propertyType.getTypeCode()) {
			case entityType:
				entityProperties.add(new ConfigurableEntityPropertyGraphNode(property, node.subNodes()));
				break;
			case listType:
			case setType: {
				LinearCollectionType linearCollectionType = (LinearCollectionType)propertyType;
				GenericModelType elementType = linearCollectionType.getCollectionElementType();
				
				if (elementType.isEntity()) {
					entityCollectionProperties.add(
							new ConfigurableEntityCollectionPropertyGraphNode(property, (EntityType<?>)elementType, node.subNodes()));
				}
				else {
					scalarCollectionProperties.add(
							new ConfigurableScalarCollectionPropertyGraphNode(property));
				}
			}
				
			default:
				throw new IllegalArgumentException("Unsupported fetch graph property type " + propertyType);
			}
		}
	}
	
	@Override
	public GenericModelType condensedType() {
		return entityType;
	}
	
	@Override
	public GenericModelType type() {
		return entityType;
	}
	
	@Override
	public EntityType<?> entityType() {
		return entityType;
	}

	@Override
	public String name() {
		return entityType.getShortName();
	}
	
	@Override
	public List<EntityCollectionPropertyGraphNode> entityCollectionProperties() {
		return entityCollectionProperties;
	}
	
	@Override
	public List<EntityPropertyGraphNode> entityProperties() {
		return entityProperties;
	}
	
	@Override
	public List<ScalarCollectionPropertyGraphNode> scalarCollectionProperties() {
		return scalarCollectionProperties;
	}
	
	public void add(ScalarCollectionPropertyGraphNode node) {
		scalarCollectionProperties.add(node);
	}
	
	public void add(EntityCollectionPropertyGraphNode node) {
		entityCollectionProperties.add(node);
	}
	
	public void add(EntityPropertyGraphNode node) {
		entityProperties.add(node);
	}
	
	@Override
	public boolean isLeaf() {
		return entityProperties.isEmpty() && entityCollectionProperties.isEmpty() && scalarCollectionProperties.isEmpty();
	}
	
	@Override
	public boolean hasCollectionProperties() {
		return !entityCollectionProperties.isEmpty() || !scalarCollectionProperties.isEmpty();
	}
	
	@Override
	public boolean hasEntityProperties() {
		return !entityProperties.isEmpty();
	}
	
	@Override
	public String toString() {
		return name();
	}
}
