// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2026
// Licensed under the Apache License, Version 2.0
// ============================================================================
package com.braintribe.model.processing.vde.reasoned.api;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.Reason;
import com.braintribe.model.generic.value.ValueDescriptor;

/** Evaluates and validates one semantic kind of {@link ValueDescriptor}. */
public interface ValueDescriptorExpert<V extends ValueDescriptor, O> {

    Maybe<O> evaluate(ValueDescriptorEvaluationContext context, V descriptor);

    default boolean cacheable(V descriptor) {
        return true;
    }

    default Reason validate(ValueDescriptorValidationContext context, V descriptor) {
        return null;
    }

    /**
     * Completes context-derived descriptor information and then validates it. Implementations may enrich the
     * descriptor, whereas {@link #validate(ValueDescriptorValidationContext, ValueDescriptor)} must be pure.
     */
    default Reason complete(ValueDescriptorValidationContext context, V descriptor) {
        return validate(context, descriptor);
    }
}
