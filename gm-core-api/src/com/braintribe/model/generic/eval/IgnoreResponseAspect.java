// ============================================================================
package com.braintribe.model.generic.eval;

import com.braintribe.common.attribute.TypeSafeAttribute;
import com.braintribe.processing.async.api.AsyncCallback;

/**
 * This is relevant when evaluating a request asynchronously and pass null as {@link AsyncCallback}, indicating we are not interested in the response
 * and want the all to return as soon as possible.
 */
public interface IgnoreResponseAspect extends TypeSafeAttribute<Boolean> {
	// Intentionally left blank
}
