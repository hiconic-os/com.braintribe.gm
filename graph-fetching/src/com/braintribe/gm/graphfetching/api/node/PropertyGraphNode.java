package com.braintribe.gm.graphfetching.api.node;

import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;

/**
 * Represents any node in the fetch graph that corresponds to a model property (attribute or reference).
 * Holds type and reflection information.
 */
public interface PropertyGraphNode extends TypedGraphNode {
    /**
     * @return The reflected property info (see model reflection API)
     */
    Property property();
    GenericModelType condensedPropertyType();
}
