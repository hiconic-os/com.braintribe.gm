// ============================================================================
// Licensed under the Apache License, Version 2.0
// ============================================================================
package com.braintribe.model.processing.vde.expression.api;

import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.value.ValueDescriptor;

/**
 * Optional inverse of descriptor evaluation. A context-bound implementation may project a concrete value back to a
 * semantic descriptor for compact, reproducible serialization. Returning {@code null} means that the value is not
 * handled by this projection.
 */
@FunctionalInterface
public interface ValueDescriptorExpressionProjection {

	ValueDescriptor project(GenericModelType inferredType, Object value);
}
