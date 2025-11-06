package com.braintribe.gm.graphfetching.api.node;

import java.util.List;

import com.braintribe.model.generic.reflection.EntityType;

/**
 * Represents an untyped node within a property/entity graph, for dynamic fetch plan construction.
 */
public interface InferableGraphNode extends GraphNode {
	/** optional entity type to support polymorphism */
	EntityType<?> entityType();
    /**
     * @return the sub-nodes (children) of this node, recursively describing the fetch structure
     */
    List<InferableGraphNode> subNodes();
}
