package com.braintribe.gm.graphfetching.processing.node;

import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.braintribe.gm.graphfetching.api.node.EntityCollectionPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityGraphNode;
import com.braintribe.gm.graphfetching.api.node.EntityPropertyGraphNode;
import com.braintribe.gm.graphfetching.api.node.FetchQualification;
import com.braintribe.gm.graphfetching.api.node.InferableGraphNode;
import com.braintribe.gm.graphfetching.api.node.ScalarCollectionPropertyGraphNode;
import com.braintribe.gm.graphfetching.processing.fetch.FetchType;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.LinearCollectionType;
import com.braintribe.model.generic.reflection.Property;

public class ConfigurableEntityGraphNode extends BaseTypedGraphNode implements EntityGraphNode {
	private Map<Property, EntityPropertyGraphNode> entityProperties = new LinkedHashMap<>();
	private Map<Property, EntityCollectionPropertyGraphNode> entityCollectionProperties = new LinkedHashMap<>();
	private Map<Property, ScalarCollectionPropertyGraphNode> scalarCollectionProperties = new LinkedHashMap<>();
	private EntityType<?> entityType;
	private FetchQualification toManyQualification = new FetchQualification(this, FetchType.TO_MANY);
	private FetchQualification toOneQualification = new FetchQualification(this, FetchType.TO_ONE);
	
	public ConfigurableEntityGraphNode(EntityType<?> entityType, InferableGraphNode... subNodes) {
		this(entityType, Arrays.asList(subNodes));
	}
	
	public ConfigurableEntityGraphNode(EntityType<?> entityType, List<InferableGraphNode> subNodes) {
		this(entityType);
		addInferable(subNodes);
	}
	
	public ConfigurableEntityGraphNode(EntityType<?> entityType) {
		this.entityType = entityType;
	}
	
	public void addInferable(InferableGraphNode... subNodes) {
		addInferable(Arrays.asList(subNodes));
	}
	
	public void addInferable(List<InferableGraphNode> subNodes) {
		Map<Property, ConfigurablePolymorphicEntityGraphNode> entityNodesByProperty = new IdentityHashMap<>();
		
		for (InferableGraphNode node: subNodes) {
			String propertyName = node.name();
			
			Property property = entityType.getProperty(propertyName);
			
			ConfigurablePolymorphicEntityGraphNode polymorphicNode = entityNodesByProperty.computeIfAbsent(property, this::buildAndRegisterPropertyNode);
			
			if (polymorphicNode != null) {
				EntityType<?> nodeEntityType = node.entityType();
				EntityType<?> polymorphBaseType = polymorphicNode.entityType();
				
				if (!polymorphBaseType.isAssignableFrom(nodeEntityType))
					throw new IllegalArgumentException(nodeEntityType.getTypeSignature() + " is not assignable to " + polymorphBaseType.getTypeSignature());
				
				ConfigurableEntityGraphNode covariantEntityNode = new ConfigurableEntityGraphNode(nodeEntityType);
				covariantEntityNode.addInferable(node.subNodes());
				polymorphicNode.addEntityNode(covariantEntityNode);
			}
		}
	}
	
	/**
	 * returns a ConfigurablePolymorphicEntityGraphNode if the property is entity related, null if it is a scalar collection property, 
	 * otherwise throws an {@link IllegalArgumentException} 
	 */
	private ConfigurablePolymorphicEntityGraphNode buildAndRegisterPropertyNode(Property property) {
		GenericModelType propertyType = property.getType();
		
		final ConfigurablePolymorphicEntityGraphNode polymorphicNode;
		
		switch (propertyType.getTypeCode()) {
		case entityType:
			EntityType<?> entityPropertyType = (EntityType<?>)propertyType;
			polymorphicNode = new ConfigurablePolymorphicEntityGraphNode(entityPropertyType);
			EntityPropertyGraphNode entityPropertyNode = new ConfigurableEntityPropertyGraphNode(property, polymorphicNode);
			add(entityPropertyNode);
			break;
		case listType:
		case setType: {
			LinearCollectionType linearCollectionType = (LinearCollectionType)propertyType;
			GenericModelType elementType = linearCollectionType.getCollectionElementType();
			
			if (elementType.isEntity()) {
				EntityType<?> entityElementType = (EntityType<?>)elementType;
				polymorphicNode = new ConfigurablePolymorphicEntityGraphNode(entityElementType);
				EntityCollectionPropertyGraphNode entityCollectionPropertyGraphNode = new ConfigurableEntityCollectionPropertyGraphNode(property, polymorphicNode);
				add(entityCollectionPropertyGraphNode);
			}
			else {
				polymorphicNode = null;
				add(new ConfigurableScalarCollectionPropertyGraphNode(property));
			}
			break;
		}
		default:
			throw new IllegalArgumentException("Unsupported fetch graph property type " + propertyType);
		}
		
		return polymorphicNode;

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
	
	public void add(ScalarCollectionPropertyGraphNode node) {
		scalarCollectionProperties.put(node.property(), node);
	}
	
	public void add(EntityCollectionPropertyGraphNode node) {
		entityCollectionProperties.put(node.property(), node);
	}
	
	public void add(EntityPropertyGraphNode node) {
		entityProperties.put(node.property(), node);
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
