// ============================================================================
// Licensed under the Apache License, Version 2.0
// ============================================================================
package com.braintribe.model.processing.vde.reasoned.api;

import com.braintribe.gm.model.reason.Reason;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.value.ValueDescriptor;

public interface ValueDescriptorValidationContext {

    Reason validate(ValueDescriptor descriptor);

    GenericModelType expectedType();
}
