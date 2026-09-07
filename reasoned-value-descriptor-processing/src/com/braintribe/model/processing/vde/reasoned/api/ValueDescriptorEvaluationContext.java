// ============================================================================
// Licensed under the Apache License, Version 2.0
// ============================================================================
package com.braintribe.model.processing.vde.reasoned.api;

import static com.braintribe.gm.model.reason.UnsatisfiedMaybeTunneling.getOrTunnel;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.model.generic.value.ValueDescriptor;

public interface ValueDescriptorEvaluationContext {

    <T> Maybe<T> evaluate(ValueDescriptor descriptor);

    /** Evaluates a direct descriptor, a VD holder, or returns a static value unchanged. */
    <T> Maybe<T> evaluateValue(Object value);

    default <T> T evaluateValueOrTunnel(Object value) {
        return getOrTunnel(evaluateValue(value));
    }

    <T> T getAspect(Class<T> aspectType);
}
