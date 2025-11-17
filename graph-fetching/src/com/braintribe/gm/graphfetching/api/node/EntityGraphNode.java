package com.braintribe.gm.graphfetching.api.node;

import java.util.List;

import com.braintribe.model.generic.reflection.EntityType;

/**
 * Represents a typed root or intermediate entity node in a fetch graph. 
 * Allows inspection of sub-properties and types for recursive/deep fetch plans.
 */
public interface EntityGraphNode extends TypedGraphNode  {
    /**
     * @return The entity property nodes (to-one relationships) of this node.
     */
    List<EntityPropertyGraphNode> entityProperties();
    /**
     * @return Collection properties with scalar values (e.g. List<String>)
     */
    List<ScalarCollectionPropertyGraphNode> scalarCollectionProperties();
    /**
     * @return Collection properties where elements are themselves entities
     */
    List<EntityCollectionPropertyGraphNode> entityCollectionProperties();
    /**
     * @return The entity type this node represents
     */
    EntityType<?> entityType();
    /**
     * @return true if no sub-properties are to be fetched (leaf node)
     */
    boolean isLeaf();
    /**
     * @return true if there are collection properties
     */
    boolean hasCollectionProperties();
    /**
     * @return true if there are to-one entity properties
     */
    boolean hasEntityProperties();
    
    FetchQualification toOneQualification();
    
    FetchQualification toManyQualification();
    
    List<EntityGraphNode> covariants();
}
