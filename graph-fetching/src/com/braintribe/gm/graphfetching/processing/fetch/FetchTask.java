package com.braintribe.gm.graphfetching.processing.fetch;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.braintribe.gm.graphfetching.api.node.EntityGraphNode;
import com.braintribe.model.generic.GenericEntity;

public class FetchTask {
	public final EntityGraphNode node;
	public final FetchType fetchType;
	public final Map<Object, GenericEntity> entities;
	public final List<EntityGraphNode> covariants;
	
	public FetchTask(EntityGraphNode node, FetchType fetchType, Collection<? extends GenericEntity> entities) {
		this(node, fetchType, entities, Collections.emptyList());
	}
	
	public FetchTask(EntityGraphNode node, FetchType fetchType, Collection<? extends GenericEntity> entities, List<EntityGraphNode> covariants) {
		this.node = node;
		this.fetchType = fetchType;
		this.entities = new HashMap<Object, GenericEntity>();
		
		for (GenericEntity entity: entities) {
			this.entities.put(entity.getId(), entity);
		}
		this.covariants = covariants;
	}
	
	public FetchTask(EntityGraphNode node, FetchType fetchType, Map<Object, GenericEntity> entities) {
		this(node, fetchType, entities, Collections.emptyList());
	}
	
	public FetchTask(EntityGraphNode node, FetchType fetchType, Map<Object, GenericEntity> entities, List<EntityGraphNode> covariants) {
		this.node = node;
		this.fetchType = fetchType;
		this.entities = entities;
		this.covariants = covariants;
	}
}
