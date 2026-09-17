// ============================================================================
// Licensed under the Apache License, Version 2.0
// ============================================================================
package com.braintribe.model.processing.vde.reasoned.impl;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.value.ValueDescriptor;
import com.braintribe.model.processing.core.expert.api.MutableDenotationMap;
import com.braintribe.model.processing.core.expert.impl.PolymorphicDenotationMap;
import com.braintribe.model.processing.vde.reasoned.api.ValueDescriptorExpert;

public class ValueDescriptorExpertRegistry {

	private final MutableDenotationMap<ValueDescriptor, ValueDescriptorExpert<?, ?>> experts = new PolymorphicDenotationMap<>();

	public <V extends ValueDescriptor, O> ValueDescriptorExpertRegistry register( //
			EntityType<V> descriptorType, ValueDescriptorExpert<? super V, O> expert) {

		experts.put(descriptorType, expert);
		return this;
	}

	public <V extends ValueDescriptor> ValueDescriptorExpert<V, Object> find(EntityType<V> descriptorType) {
		return experts.find(descriptorType);
	}

}
