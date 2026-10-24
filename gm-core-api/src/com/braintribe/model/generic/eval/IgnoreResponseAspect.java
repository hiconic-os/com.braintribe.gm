// ============================================================================
package com.braintribe.model.generic.eval;

import com.braintribe.common.attribute.TypeSafeAttribute;
import com.braintribe.processing.async.api.AsyncCallback;

/**
 * This is relevant when evaluating a request asynchronously and pass null as {@link AsyncCallback}, indicating (to the processor) we are not
 * interested in the response and want the call to return as soon as possible.
 * <p>
 * Typically a processor would just return an empty instance of its response type.
 */
public interface IgnoreResponseAspect extends TypeSafeAttribute<Boolean> {
	// Intentionally left blank
}
