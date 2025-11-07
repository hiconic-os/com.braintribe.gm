package com.braintribe.gm.graphfetching.api.node;

import com.braintribe.model.generic.reflection.LinearCollectionType;

/**
 * Represents a collection-valued property node (e.g. Set<X>, List<Y>) in a fetch graph.
 * Used for both scalar and entity-typed collections.
 */
public interface CollectionPropertyGraphNode extends PropertyGraphNode {
    /**
     * @return the modelled collection type for this property (list, set, etc.)
     */
    @Override
    LinearCollectionType type();
}
