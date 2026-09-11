package com.braintribe.gm.model.security.reason;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/** The pending approval of the current browser context was explicitly rejected. */
public interface BrowserContextRejected extends AuthenticationFailure {
	EntityType<BrowserContextRejected> T = EntityTypes.T(BrowserContextRejected.class);
}
