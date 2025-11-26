package com.braintribe.gm.graphfetching.api.node;

import com.braintribe.model.generic.reflection.MapType;

public interface MapPropertyGraphNode extends PropertyGraphNode {
	@Override
	MapType type();
	
	KeyValueType keyValueType();
	
	/**
	 * optional entity node for map key in case it has EntityType
	 */
	AbstractEntityGraphNode keyNode();
	
	/**
	 * optional entity node for map value in case it has EntityType
	 */
	AbstractEntityGraphNode valueNode();
}
