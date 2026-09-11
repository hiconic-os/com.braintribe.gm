package com.braintribe.gm.model.security.reason;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/** The approval of the current browser context has expired. */
public interface BrowserContextExpired extends AuthenticationFailure {
	EntityType<BrowserContextExpired> T = EntityTypes.T(BrowserContextExpired.class);
}
