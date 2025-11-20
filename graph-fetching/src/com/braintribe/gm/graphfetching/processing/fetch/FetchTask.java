package com.braintribe.gm.graphfetching.processing.fetch;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.braintribe.gm.graphfetching.api.node.AbstractEntityGraphNode;
import com.braintribe.model.generic.GenericEntity;

public class FetchTask {
	public final AbstractEntityGraphNode node;
	public final FetchType fetchType;
	public final Map<Object, GenericEntity> entities;
	
	public FetchTask(AbstractEntityGraphNode node, FetchType fetchType, Collection<? extends GenericEntity> entities) {
		this.node = node;
		this.fetchType = fetchType;
		this.entities = new HashMap<Object, GenericEntity>();
		
		for (GenericEntity entity: entities) {
			this.entities.put(entity.getId(), entity);
		}
	}
	
	public FetchTask(AbstractEntityGraphNode node, FetchType fetchType, Map<Object, GenericEntity> entities) {
		this.node = node;
		this.fetchType = fetchType;
		this.entities = entities;
	}
}
