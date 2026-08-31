// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2026
// Licensed under the Apache License, Version 2.0
// ============================================================================
package com.braintribe.model.processing.vde.reasoned.impl;

import static com.braintribe.gm.model.reason.UnsatisfiedMaybeTunneling.getOrTunnel;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.PropertyAccessInterceptor;
import com.braintribe.model.processing.vde.reasoned.api.ValueDescriptorEvaluationContext;

/** Transparently projects VD-backed model properties while a reasoned evaluation scope is active. */
public class ValueDescriptorEvaluationPai extends PropertyAccessInterceptor {

    @Override
    public Object getProperty(Property property, GenericEntity entity, boolean isVd) {
        if (isVd)
            return next.getProperty(property, entity, true);

        ValueDescriptorEvaluationContext context = ValueDescriptorEvaluationScope.current();
        if (context == null)
            return next.getProperty(property, entity, false);

        Object rawValue = next.getProperty(property, entity, false);
        return getOrTunnel(context.evaluateValue(rawValue));
    }
}
