package com.braintribe.gm.graphfetching.processing.node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
	private List<EntityPropertyGraphNode> entityProperties = new ArrayList<>();
	private List<EntityCollectionPropertyGraphNode> entityCollectionProperties = new ArrayList<>();
	private List<ScalarCollectionPropertyGraphNode> scalarCollectionProperties = new ArrayList<>();
	private EntityType<?> entityType;
	private FetchQualification toManyQualification = new FetchQualification(this, FetchType.TO_MANY);
	private FetchQualification toOneQualification = new FetchQualification(this, FetchType.TO_ONE);
	
	public ConfigurableEntityGraphNode(EntityType<?> entityType, InferableGraphNode... subNodes) {
		this(entityType, Arrays.asList(subNodes));
	}
	
	public ConfigurableEntityGraphNode(EntityType<?> entityType, List<InferableGraphNode> subNodes) {
		this(entityType);
		infer(subNodes);
	}
	
	public ConfigurableEntityGraphNode(EntityType<?> entityType) {
		this.entityType = entityType;
	}
	
	private void infer(List<InferableGraphNode> subNodes) {
		for (InferableGraphNode node: subNodes) {
			String propertyName = node.name();
			
			Property property = entityType.getProperty(propertyName);
			
			GenericModelType propertyType = property.getType();
			
			switch (propertyType.getTypeCode()) {
			case entityType:
				EntityType<?> entityPropertyType = covariantType((EntityType<?>)propertyType, node.entityType());
				entityProperties.add(new ConfigurableEntityPropertyGraphNode(property, entityPropertyType, node.subNodes()));
				break;
			case listType:
			case setType: {
				LinearCollectionType linearCollectionType = (LinearCollectionType)propertyType;
				GenericModelType elementType = linearCollectionType.getCollectionElementType();
				
				if (elementType.isEntity()) {
					EntityType<?> entityElementType = covariantType((EntityType<?>)elementType, node.entityType());
					entityCollectionProperties.add(
							new ConfigurableEntityCollectionPropertyGraphNode(property, entityElementType, node.subNodes()));
				}
				else {
					scalarCollectionProperties.add(
							new ConfigurableScalarCollectionPropertyGraphNode(property));
				}
				break;
			}
			default:
				throw new IllegalArgumentException("Unsupported fetch graph property type " + propertyType);
			}
		}
	}
	
	private EntityType<?> covariantType(EntityType<?> exact, EntityType<?> covariant) {
		if (covariant == null)
			return exact;
		
		if (!exact.isAssignableFrom(covariant))
			throw new IllegalArgumentException(covariant.getTypeSignature() + " is not assignable to " + exact.getTypeSignature());
			
		return covariant;
					
					
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
