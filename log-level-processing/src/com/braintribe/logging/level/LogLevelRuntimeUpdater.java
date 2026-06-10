package com.braintribe.logging.level;

import java.util.Objects;
import java.util.Map;
import java.util.Set;

import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.Required;
import com.braintribe.gm.model.reason.Maybe;

public class LogLevelRuntimeUpdater {
	private LogLevelManager logLevelManager;
	private LogLevelUpdateDispatcher updateDispatcher;

	public Maybe<Void> assignRuntimeLogLevel(String name, String level) {
		Maybe<Void> update = requireLogLevelManager().assignRuntimeLogLevel(name, level);
		return dispatchWhenSatisfied(update);
	}

	public Maybe<Void> resetRuntimeLogLevel(String name) {
		Maybe<Void> update = requireLogLevelManager().resetRuntimeLogLevel(name);
		return dispatchWhenSatisfied(update);
	}

	public Maybe<Void> clearRuntimeLogLevels() {
		Maybe<Void> update = requireLogLevelManager().clearRuntimeLogLevels();
		return dispatchWhenSatisfied(update);
	}

	public Maybe<Void> updateRuntimeLogLevels(Map<String, String> levels, Set<String> namesToRemove) {
		Maybe<Void> update = requireLogLevelManager().updateRuntimeLogLevels(levels, namesToRemove);
		return dispatchWhenSatisfied(update);
	}

	@Configurable
	@Required
	public void setLogLevelManager(LogLevelManager logLevelManager) {
		this.logLevelManager = Objects.requireNonNull(logLevelManager, "logLevelManager");
	}

	@Configurable
	@Required
	public void setUpdateDispatcher(LogLevelUpdateDispatcher updateDispatcher) {
		this.updateDispatcher = Objects.requireNonNull(updateDispatcher, "updateDispatcher");
	}

	private Maybe<Void> dispatchWhenSatisfied(Maybe<Void> update) {
		if (update.isUnsatisfied()) {
			return update.propagateReason();
		}

		return requireUpdateDispatcher().dispatchUpdate();
	}

	private LogLevelManager requireLogLevelManager() {
		return Objects.requireNonNull(logLevelManager, "logLevelManager");
	}

	private LogLevelUpdateDispatcher requireUpdateDispatcher() {
		return Objects.requireNonNull(updateDispatcher, "updateDispatcher");
	}
}
