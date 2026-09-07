// ============================================================================
// Licensed under the Apache License, Version 2.0
// ============================================================================
package com.braintribe.model.processing.vde.reasoned.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.value.ValueDescriptor;
import com.braintribe.model.processing.vde.reasoned.api.ValueDescriptorExpert;

public class ValueDescriptorExpertRegistry {

    private final Map<EntityType<? extends ValueDescriptor>, ValueDescriptorExpert<?, ?>> concreteExperts = new LinkedHashMap<>();
    private final Map<EntityType<? extends ValueDescriptor>, ValueDescriptorExpert<?, ?>> abstractExperts = new LinkedHashMap<>();

    public <V extends ValueDescriptor, O> ValueDescriptorExpertRegistry register(EntityType<V> descriptorType,
            ValueDescriptorExpert<? super V, O> expert) {
        concreteExperts.put(descriptorType, expert);
        return this;
    }

    public <V extends ValueDescriptor, O> ValueDescriptorExpertRegistry registerAbstract(EntityType<V> descriptorType,
            ValueDescriptorExpert<? super V, O> expert) {
        abstractExperts.put(descriptorType, expert);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <V extends ValueDescriptor> ValueDescriptorExpert<V, Object> find(EntityType<V> descriptorType) {
        ValueDescriptorExpert<?, ?> expert = concreteExperts.get(descriptorType);
        if (expert != null)
            return (ValueDescriptorExpert<V, Object>) expert;

        for (Map.Entry<EntityType<? extends ValueDescriptor>, ValueDescriptorExpert<?, ?>> entry : abstractExperts.entrySet()) {
            if (entry.getKey().isAssignableFrom(descriptorType))
                return (ValueDescriptorExpert<V, Object>) entry.getValue();
        }

        return null;
    }
}
