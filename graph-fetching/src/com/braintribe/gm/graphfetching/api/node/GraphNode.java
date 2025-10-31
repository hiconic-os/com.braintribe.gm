package com.braintribe.gm.graphfetching.api.node;

/**
 * A node in the object/property graph describing which properties/entities should be fetched.
 */
public interface GraphNode {
    /**
     * @return The (property or node) name this node represents in the graph.
     */
    String name();
    
    String toString();
}
