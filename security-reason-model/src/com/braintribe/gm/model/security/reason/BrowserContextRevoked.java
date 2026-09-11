package com.braintribe.gm.model.security.reason;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/** The approval of the current browser context was revoked. */
public interface BrowserContextRevoked extends AuthenticationFailure {
	EntityType<BrowserContextRevoked> T = EntityTypes.T(BrowserContextRevoked.class);
}
