// ============================================================================
// Licensed under the Apache License, Version 2.0
// ============================================================================
package com.braintribe.model.processing.vde.expression.api;

import com.braintribe.gm.model.reason.Maybe;

/** Bidirectional compact syntax for model-defined value descriptor expressions. */
public interface ValueDescriptorExpressionCodec {

	/** Parses a complete template, which may contain static text and one or more <code>${...}</code> expressions. */
	Maybe<Object> parse(String expression);

	/** Renders a value descriptor or a descriptor-backed template to its compact expression. */
	Maybe<String> render(Object value);
}
