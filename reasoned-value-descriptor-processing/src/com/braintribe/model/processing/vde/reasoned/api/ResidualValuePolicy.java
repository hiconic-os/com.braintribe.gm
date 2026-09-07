// ============================================================================
// Licensed under the Apache License, Version 2.0
// ============================================================================
package com.braintribe.model.processing.vde.reasoned.api;

import java.util.function.Predicate;

import com.braintribe.gm.model.reason.Reason;
import com.braintribe.model.generic.value.ValueDescriptor;

@FunctionalInterface
public interface ResidualValuePolicy {

    boolean preserve(Reason reason);

    /** Called after a descriptor was retained because {@link #preserve(Reason)} accepted its reason. */
    default void preserved(ValueDescriptor descriptor, Reason reason) {
        // Optional observation hook, e.g. for collecting unresolved configuration variables.
    }

    static ResidualValuePolicy rejectAll() {
        return reason -> false;
    }

    static ResidualValuePolicy preserveAll() {
        return reason -> true;
    }

    static ResidualValuePolicy preserving(Predicate<? super Reason> predicate) {
        return predicate::test;
    }
}
