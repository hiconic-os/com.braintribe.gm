package com.braintribe.logging.level;

import java.util.Objects;

import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.Required;
import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.essential.InternalError;

public class DirectLogLevelUpdateDispatcher implements LogLevelUpdateDispatcher {
	private LogLevelManager logLevelManager;

	@Override
	public Maybe<Void> dispatchUpdate() {
		try {
			logLevelManager.applyEffectiveLogLevels();
			return Maybe.complete(null);
		} catch (Exception e) {
			return InternalError.from(e, "Failed to update effective log levels").asMaybe();
		}
	}

	@Override
	@Configurable
	@Required
	public void setLogLevelManager(LogLevelManager logLevelManager) {
		this.logLevelManager = Objects.requireNonNull(logLevelManager, "logLevelManager");
	}
}
