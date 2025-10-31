package com.braintribe.gm.graphfetching.api.node;

/**
 * Represents an untyped node within a property/entity graph, for dynamic fetch plan construction.
 */
public interface UntypedGraphNode extends GraphNode {
    /**
     * @return the sub-nodes (children) of this node, recursively describing the fetch structure
     */
    java.util.List<UntypedGraphNode> subNodes();
}
