// ============================================================================
// Licensed under the Apache License, Version 2.0
// ============================================================================
package com.braintribe.model.processing.vde.reasoned.api;

import com.braintribe.gm.model.reason.Reason;
import com.braintribe.model.generic.value.ValueDescriptor;

/**
 * Controls whether a descriptor is evaluated in the current phase.
 * <p>
 * Returning a reason deliberately defers the complete descriptor subtree. The reason is passed through the
 * {@link ResidualValuePolicy}, which keeps reporting of unresolved dependencies identical to ordinary failed evaluation.
 */
@FunctionalInterface
public interface ValueDescriptorEvaluationPolicy {

	/** Returns {@code null} to evaluate, or the reason why this phase must preserve the descriptor. */
	Reason deferReason(ValueDescriptorEvaluationContext context, ValueDescriptor descriptor);
}
