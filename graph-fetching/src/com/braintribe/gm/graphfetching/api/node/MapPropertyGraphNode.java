package com.braintribe.gm.graphfetching.api.node;

import java.util.List;

import com.braintribe.model.generic.reflection.MapType;

public interface MapPropertyGraphNode extends PropertyGraphNode {
	@Override
	MapType type();
	
	/**
	 * optional entity nodes for map key in case it has EntityType
	 */
	List<EntityGraphNode> keyNodes();
	
	/**
	 * optional entity nodes for map value in case it has EntityType
	 */
	List<EntityGraphNode> valueNodes();
}
