package com.braintribe.logging.level;

import com.braintribe.gm.model.reason.Maybe;

public interface LogLevelUpdateDispatcher {
	void setLogLevelManager(LogLevelManager logLevelManager);

	Maybe<Void> dispatchUpdate();
}
