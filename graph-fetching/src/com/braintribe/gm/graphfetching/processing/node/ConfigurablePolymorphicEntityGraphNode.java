package com.braintribe.gm.graphfetching.processing.node;

import java.util.ArrayList;
import java.util.List;

import com.braintribe.gm.graphfetching.api.node.EntityGraphNode;
import com.braintribe.gm.graphfetching.api.node.FetchQualification;
import com.braintribe.gm.graphfetching.api.node.PolymorphicEntityGraphNode;
import com.braintribe.gm.graphfetching.processing.fetch.FetchType;
import com.braintribe.model.generic.reflection.EntityType;

public class ConfigurablePolymorphicEntityGraphNode implements PolymorphicEntityGraphNode {
	private EntityType<?> entityType;
	private List<EntityGraphNode> entityNodes = new ArrayList<>();
	private Boolean hasCollectionProperties;
	private Boolean hasEntityProperties;
	private FetchQualification toManyQualification = new FetchQualification(this, FetchType.TO_MANY);
	private FetchQualification toOneQualification = new FetchQualification(this, FetchType.TO_ONE);
	
	public ConfigurablePolymorphicEntityGraphNode(EntityType<?> entityType) {
		super();
		this.entityType = entityType;
	}
	
	public void addEntityNode(EntityGraphNode entityNode) {
		entityNodes.add(entityNode);
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
	public String stringify() {
		return null;
	}

	@Override
	public List<EntityGraphNode> entityNodes() {
		return entityNodes;
	}
	
	@Override
	public boolean hasCollectionProperties() {
		if (hasCollectionProperties == null) {
			hasCollectionProperties = determineHasCollectionProperties();
			
		}

		return hasCollectionProperties;
	}
	
	private Boolean determineHasCollectionProperties() {
		for (EntityGraphNode node: entityNodes) {
			if (node.hasCollectionProperties())
				return true;
		}
		
		return false;
	}

	@Override
	public boolean hasEntityProperties() {
		if (hasEntityProperties == null) {
			hasEntityProperties = determineHasEntityProperties();
		}

		return hasEntityProperties;
	}

	private Boolean determineHasEntityProperties() {
		for (EntityGraphNode node: entityNodes) {
			if (node.hasEntityProperties())
				return true;
		}
		
		return false;
	}
	
	@Override
	public FetchQualification toManyQualification() {
		return toManyQualification;
	}
	
	@Override
	public FetchQualification toOneQualification() {
		return toOneQualification;
	}

}
