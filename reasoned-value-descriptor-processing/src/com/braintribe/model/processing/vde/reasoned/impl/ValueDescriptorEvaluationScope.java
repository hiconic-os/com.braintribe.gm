// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2026
// Licensed under the Apache License, Version 2.0
// ============================================================================
package com.braintribe.model.processing.vde.reasoned.impl;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Supplier;

import com.braintribe.model.processing.vde.reasoned.api.ValueDescriptorEvaluationContext;

/** A Java-17-compatible dynamically scoped bridge used by typed model getters. */
public final class ValueDescriptorEvaluationScope {

    private static final ThreadLocal<Deque<ValueDescriptorEvaluationContext>> contexts = ThreadLocal.withInitial(ArrayDeque::new);

    private ValueDescriptorEvaluationScope() {
    }

    public static ValueDescriptorEvaluationContext current() {
        return contexts.get().peek();
    }

    public static <T> T with(ValueDescriptorEvaluationContext context, Supplier<T> action) {
        Deque<ValueDescriptorEvaluationContext> stack = contexts.get();
        stack.push(context);
        try {
            return action.get();
        } finally {
            stack.pop();
            if (stack.isEmpty())
                contexts.remove();
        }
    }
}
