package com.braintribe.gm.graphfetching.api.node;

/**
 * Represents a node for a collection-valued entity reference (to-many) in a fetch graph.
 * Combines collection details and entity property semantics.
 */
public interface EntityCollectionPropertyGraphNode extends EntityRelatedPropertyGraphNode, CollectionPropertyGraphNode {
	// intentionally left empty
}
