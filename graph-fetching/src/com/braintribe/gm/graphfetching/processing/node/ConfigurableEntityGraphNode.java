package com.braintribe.gm.graphfetching.processing.node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.braintribe.gm.graphfetching.api.node.AbstractEntityGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityCollectionPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.FetchQualification;
import com.braintribe.gm.graphfetching.api.node.InferableGraphNode;
import com.braintribe.gm.graphfetching.api.node.MapPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.ScalarCollectionPropertyGraphNode;
import com.braintribe.gm.graphfetching.processing.fetch.FetchType;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.LinearCollectionType;
import com.braintribe.model.generic.reflection.MapType;
import com.braintribe.model.generic.reflection.Property;

public class ConfigurableEntityGraphNode extends BaseTypedGraphNode implements EntityGraphNode {
	private Map<Property, EntityPropertyGraphNode> entityProperties = new LinkedHashMap<>();
	private Map<Property, EntityCollectionPropertyGraphNode> entityCollectionProperties = new LinkedHashMap<>();
	private Map<Property, ScalarCollectionPropertyGraphNode> scalarCollectionProperties = new LinkedHashMap<>();
	private Map<Property, MapPropertyGraphNode> mapProperties = new LinkedHashMap<>();
	private EntityType<?> entityType;
	private FetchQualification toManyQualification = new FetchQualification(this, FetchType.TO_MANY);
	private FetchQualification toOneQualification = new FetchQualification(this, FetchType.TO_ONE);
	
	public ConfigurableEntityGraphNode(EntityType<?> entityType, InferableGraphNode... subNodes) {
		this(entityType, Arrays.asList(subNodes));
	}
	
	public ConfigurableEntityGraphNode(EntityType<?> entityType, List<InferableGraphNode> subNodes) {
		this(entityType);
		configureProperties(subNodes);
	}
	
	public ConfigurableEntityGraphNode(EntityType<?> entityType) {
		this.entityType = entityType;
	}
	
	public void configureProperties(InferableGraphNode... subNodes) {
		configureProperties(Arrays.asList(subNodes));
	}
	
	public void configureProperties(List<InferableGraphNode> subNodes) {
		Map<Property, List<InferableGraphNode>> inferableNodesByProperty = new LinkedHashMap<>();
		
		for (InferableGraphNode node: subNodes) {
			String propertyName = node.name();
			
			Property property = entityType.getProperty(propertyName);
			
			inferableNodesByProperty.computeIfAbsent(property, k -> new ArrayList<>()).add(node);
		}
		
		for (Map.Entry<Property, List<InferableGraphNode>> entry: inferableNodesByProperty.entrySet())
			configureProperty(entry.getKey(), entry.getValue());
	}
	
	
	private void configureProperty(Property property, List<InferableGraphNode> inferableNodes) {
		GenericModelType propertyType = property.getType();
		
		switch (propertyType.getTypeCode()) {
		case entityType: configureEntityProperty(property, inferableNodes); break;
		case listType: configureCollectionProperty(property, inferableNodes); break;
		case setType: configureCollectionProperty(property, inferableNodes); break;
		case mapType: configureMapProperty(property, inferableNodes); break;
		default:
			throw new IllegalArgumentException("Unsupported fetch graph property type " + propertyType);
		}
	}

	private void configureMapProperty(Property property, List<InferableGraphNode> inferableNodes) {
		GenericModelType propertyType = property.getType();
		MapType mapType = (MapType)propertyType;
		GenericModelType keyType = mapType.getKeyType();
		GenericModelType valueType = mapType.getValueType();
		
		EntityType<?> keyEntityType = keyType.isEntity()? (EntityType<?>)keyType: null;
		EntityType<?> valueEntityType = valueType.isEntity()? (EntityType<?>)valueType: null;
		
		ConfigurableMapPropertyGraphNode propertyNode = new ConfigurableMapPropertyGraphNode(property);
		
		List<InferableGraphNode> keyInferableNodes = new ArrayList<>();
		List<InferableGraphNode> valueInferableNodes = new ArrayList<>();
		
		for (InferableGraphNode inferableNode: inferableNodes) {
			switch (inferableNode.name()) {
			case "KEY":
				keyInferableNodes.addAll(inferableNode.subNodes());
				break;
			case "VALUE":
				valueInferableNodes.addAll(inferableNode.subNodes());
				break;
			default:
				if (valueEntityType != null)
					valueInferableNodes.add(inferableNode);
				if (keyEntityType != null)
					keyInferableNodes.add(inferableNode);
				break;
			}
		}
		
		if (keyEntityType != null)
			propertyNode.setKeyNode(abstractEntityGraphNode(keyEntityType, keyInferableNodes));
		
		if (valueEntityType != null)
			propertyNode.setValueNode(abstractEntityGraphNode(valueEntityType, valueInferableNodes));
		
		add(propertyNode);
	}

	private void configureCollectionProperty(Property property, List<InferableGraphNode> inferableNodes) {
		GenericModelType propertyType = property.getType();
		LinearCollectionType linearCollectionType = (LinearCollectionType)propertyType;
		GenericModelType elementType = linearCollectionType.getCollectionElementType();
		
		if (elementType.isEntity()) {
			EntityType<?> entityElementType = (EntityType<?>)elementType;
			AbstractEntityGraphNode abstractEntityGraphNode = abstractEntityGraphNode(entityElementType, inferableNodes);
			ConfigurableEntityCollectionPropertyGraphNode propertyNode = new ConfigurableEntityCollectionPropertyGraphNode(property, abstractEntityGraphNode);
			add(propertyNode);
		}
		else {
			ConfigurableScalarCollectionPropertyGraphNode propertyNode = new ConfigurableScalarCollectionPropertyGraphNode(property);
			add(propertyNode);
		}
	}

	private void configureEntityProperty(Property property, List<InferableGraphNode> inferableNodes) {
		GenericModelType propertyType = property.getType();
		EntityType<?> entityPropertyType = (EntityType<?>)propertyType;

		final AbstractEntityGraphNode abstractEntityNode = abstractEntityGraphNode(entityPropertyType, inferableNodes);
		ConfigurableEntityPropertyGraphNode propertyNode = new ConfigurableEntityPropertyGraphNode(property, abstractEntityNode);
		add(propertyNode);
	}
	
	private AbstractEntityGraphNode abstractEntityGraphNode(EntityType<?> polymorphBaseType, List<InferableGraphNode> inferableNodes) {
		if (inferableNodes.isEmpty())
			return null;
		
		if (inferableNodes.size() > 1) {
			ConfigurablePolymorphicEntityGraphNode polymorphicNode = new ConfigurablePolymorphicEntityGraphNode(polymorphBaseType);
			
			for (InferableGraphNode inferableNode: inferableNodes) {
				polymorphicNode.addEntityNode(entityGraphNode(polymorphBaseType, inferableNode));
			}
			return polymorphicNode;
		}
		else {
			return entityGraphNode(polymorphBaseType, inferableNodes.iterator().next());
		}
	}
	
	private ConfigurableEntityGraphNode entityGraphNode(EntityType<?> polymorphBaseType, InferableGraphNode node) {
		EntityType<?> entityType = covariantType(polymorphBaseType, node.entityType());
		
		ConfigurableEntityGraphNode entityNode = new ConfigurableEntityGraphNode(entityType);
		entityNode.configureProperties(node.subNodes());
		
		return entityNode;
	}
	
	private EntityType<?> covariantType(EntityType<?> exact, EntityType<?> covariant) {
		if (covariant == null)
			return exact;
		
		if (!exact.isAssignableFrom(covariant))
			throw new IllegalArgumentException(covariant.getTypeSignature() + " is not assignable to " + exact.getTypeSignature());
			
		return covariant;
	}
	
	@Override
	public EntityType<?> condensedType() {
		return entityType;
	}
	
	@Override
	public EntityType<?> type() {
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
	public Map<Property, EntityCollectionPropertyGraphNode> entityCollectionProperties() {
		return entityCollectionProperties;
	}
	
	@Override
	public Map<Property, EntityPropertyGraphNode> entityProperties() {
		return entityProperties;
	}
	
	@Override
	public Map<Property, ScalarCollectionPropertyGraphNode> scalarCollectionProperties() {
		return scalarCollectionProperties;
	}
	
	@Override
	public Map<Property, MapPropertyGraphNode> mapProperties() {
		return mapProperties;
	}
	
	public void add(ScalarCollectionPropertyGraphNode node) {
		scalarCollectionProperties.put(node.property(), node);
	}
	
	public void add(EntityCollectionPropertyGraphNode node) {
		entityCollectionProperties.put(node.property(), node);
	}
	
	public void add(EntityPropertyGraphNode node) {
		entityProperties.put(node.property(), node);
	}
	
	public void add(MapPropertyGraphNode node) {
		mapProperties.put(node.property(), node);
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
	public FetchQualification toManyQualification() {
		return toManyQualification;
	}
	
	@Override
	public FetchQualification toOneQualification() {
		return toOneQualification;
	}
	
	@Override
	public String toString() {
		return name();
	}
}
