package com.braintribe.gm.graphfetching.api.node;

import com.braintribe.model.generic.reflection.GenericModelType;

/**
 * Represents a fetch graph node that carries type information about its referenced value or entity.
 */
public interface TypedGraphNode extends GraphNode {
    /**
     * @return the generic model type of this node (may be entity or value)
     */
    GenericModelType type();
    /**
     * @return a possibly condensed or simplified type for advanced fetch/serialization scenarios
     */
    GenericModelType condensedType();
}
