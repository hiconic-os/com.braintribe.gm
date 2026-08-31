// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2026
// Licensed under the Apache License, Version 2.0
// ============================================================================
package com.braintribe.model.processing.vde.reasoned.api;

import java.util.function.Predicate;

import com.braintribe.gm.model.reason.Reason;

@FunctionalInterface
public interface ResidualValuePolicy {

    boolean preserve(Reason reason);

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
