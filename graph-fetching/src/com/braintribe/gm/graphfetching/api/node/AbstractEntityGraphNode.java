package com.braintribe.gm.graphfetching.api.node;

import java.util.List;

import com.braintribe.model.generic.reflection.EntityType;

public interface AbstractEntityGraphNode extends TypedGraphNode {
	@Override
	EntityType<?> condensedType();
	
	@Override
	EntityType<?> type();
	
	EntityType<?> entityType();
	
	List<EntityGraphNode> entityNodes();
	
    /**
     * @return true if there are collection properties
     */
    boolean hasCollectionProperties();
    /**
     * @return true if there are to-one entity properties
     */
    boolean hasEntityProperties();
    
    PolymorphicEntityGraphNode isPolymorphic();
    
    FetchQualification toOneQualification();
    
    FetchQualification toManyQualification();
    
    FetchQualification allQualification();
}
