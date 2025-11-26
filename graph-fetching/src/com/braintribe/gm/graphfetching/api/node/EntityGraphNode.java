package com.braintribe.gm.graphfetching.api.node;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.Property;

/**
 * Represents a typed root or intermediate entity node in a fetch graph. 
 * Allows inspection of sub-properties and types for recursive/deep fetch plans.
 */
public interface EntityGraphNode extends AbstractEntityGraphNode  {
    /**
     * @return The entity property nodes (to-one relationships) of this node.
     */
    Map<Property, EntityPropertyGraphNode> entityProperties();
    /**
     * @return Collection properties with scalar values (e.g. List<String>)
     */
    Map<Property, ScalarCollectionPropertyGraphNode> scalarCollectionProperties();
    /**
     * @return Collection properties where elements are themselves entities
     */
    Map<Property, EntityCollectionPropertyGraphNode> entityCollectionProperties();
    
    /**
     * @return Map properties of various KEY:VALUE combinations of scalar and entity
     */
    Map<Property, MapPropertyGraphNode> mapProperties();
    
    /**
     * @return The entity type this node represents
     */
    EntityType<?> entityType();
    
    @Override
    default List<EntityGraphNode> entityNodes() {
    		return Collections.singletonList(this);
    }
    
    @Override
    default PolymorphicEntityGraphNode isPolymorphic() {
    		return null;
    }
}
