// ============================================================================
// Licensed under the Apache License, Version 2.0
// ============================================================================
package com.braintribe.model.processing.vde.expression.api;

import com.braintribe.codec.marshaller.api.MarshallerOption;

/**
 * Optional marshaller adapter for the otherwise marshaller-independent expression codec.
 * Marshaller implementations remain ignorant of the concrete descriptor functions.
 */
public interface ValueDescriptorExpressionCodecOption extends MarshallerOption<ValueDescriptorExpressionCodec> {
}
