package com.braintribe.logging.level;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.essential.InternalError;
import com.braintribe.logging.Logger;
import com.braintribe.logging.level.LogLevelConfiguration.LogLevelEntry;
import com.braintribe.logging.level.persistence.LogLevelPersistence;

public class LogLevelManager {
	private static final Logger logger = Logger.getLogger(LogLevelManager.class);

	private final LogLevelPersistence deployedLogLevels;
	private final LogLevelPersistence persistentLogLevels;
	private final LogLevelFramework logLevelFramework;
	private final LogLevelConfiguration logLevelConfiguration;

	public LogLevelManager(LogLevelPersistence deployedLogLevels, LogLevelPersistence persistentLogLevels, LogLevelFramework logLevelFramework) {
		this(deployedLogLevels, persistentLogLevels, logLevelFramework, new LogLevelConfiguration());
	}

	public LogLevelManager(LogLevelPersistence deployedLogLevels, LogLevelPersistence persistentLogLevels, LogLevelFramework logLevelFramework,
			LogLevelConfiguration logLevelConfiguration) {
		this.deployedLogLevels = Objects.requireNonNull(deployedLogLevels, "deployedLogLevels");
		this.persistentLogLevels = Objects.requireNonNull(persistentLogLevels, "persistentLogLevels");
		this.logLevelFramework = Objects.requireNonNull(logLevelFramework, "logLevelFramework");
		this.logLevelConfiguration = Objects.requireNonNull(logLevelConfiguration, "logLevelConfiguration");
	}

	public void applyEffectiveLogLevels() {
		Map<String, String> deployedLevels = deployedLogLevels.getLogLevels();
		Map<String, String> persistentLevels = persistentLogLevels.getLogLevels();
		Map<String, String> effectiveLevels = logLevelConfiguration.resolveEffectiveLogLevels(deployedLevels, persistentLevels);
		Set<String> configuredLevels = new LinkedHashSet<>(logLevelFramework.getConfiguredLogLevels().keySet());

		logLevelFramework.clearLogLevels(configuredLevels);
		logLevelFramework.applyLogLevels(effectiveLevels);
		logAppliedLevels(deployedLevels, persistentLevels, effectiveLevels, configuredLevels);
	}

	public Map<String, LogLevelEntry> describeLogLevels() {
		return logLevelConfiguration.describeLogLevels(deployedLogLevels.getLogLevels(), persistentLogLevels.getLogLevels(),
				logLevelFramework.getConfiguredLogLevels());
	}

	public Map<String, String> getPackagedLogLevels() {
		return deployedLogLevels.getLogLevels();
	}

	public Map<String, String> getRuntimeLogLevels() {
		return persistentLogLevels.getLogLevels();
	}

	public Map<String, String> getEffectiveLogLevels() {
		return logLevelConfiguration.resolveEffectiveLogLevels(getPackagedLogLevels(), getRuntimeLogLevels());
	}

	public Set<String> getKnownLoggerNames() {
		return logLevelFramework.getKnownLoggerNames();
	}

	public Maybe<Void> assignRuntimeLogLevel(String name, String level) {
		try {
			Map<String, String> levels = new LinkedHashMap<>();

			levels.put(name, level);
			persistentLogLevels.updateLogLevels(levels, new LinkedHashSet<String>());
			return Maybe.complete(null);
		} catch (Exception e) {
			return InternalError.from(e, "Failed to set runtime log level").asMaybe();
		}
	}

	public Maybe<Void> resetRuntimeLogLevel(String name) {
		try {
			Set<String> namesToRemove = new LinkedHashSet<>();

			namesToRemove.add(name);
			persistentLogLevels.updateLogLevels(new LinkedHashMap<String, String>(), namesToRemove);
			return Maybe.complete(null);
		} catch (Exception e) {
			return InternalError.from(e, "Failed to reset runtime log level").asMaybe();
		}
	}

	public Maybe<Void> clearRuntimeLogLevels() {
		try {
			persistentLogLevels.clearLogLevels();
			return Maybe.complete(null);
		} catch (Exception e) {
			return InternalError.from(e, "Failed to clear runtime log levels").asMaybe();
		}
	}

	public Maybe<Void> updateRuntimeLogLevels(Map<String, String> levels, Set<String> namesToRemove) {
		try {
			persistentLogLevels.updateLogLevels(levels == null ? new LinkedHashMap<String, String>() : levels,
					namesToRemove == null ? new LinkedHashSet<String>() : namesToRemove);
			return Maybe.complete(null);
		} catch (Exception e) {
			return InternalError.from(e, "Failed to update runtime log levels").asMaybe();
		}
	}

	private void logAppliedLevels(Map<String, String> deployedLevels, Map<String, String> persistentLevels, Map<String, String> effectiveLevels,
			Set<String> clearedLevels) {
		if (!logger.isInfoEnabled()) {
			return;
		}

		StringBuilder message = new StringBuilder();
		message.append("Applied ");
		message.append(effectiveLevels.size());
		message.append(" effective log level");
		if (effectiveLevels.size() != 1) {
			message.append('s');
		}
		message.append(" after clearing ");
		message.append(clearedLevels.size());
		message.append(" configured log level");
		if (clearedLevels.size() != 1) {
			message.append('s');
		}

		if (effectiveLevels.isEmpty()) {
			message.append('.');
			logger.info(message.toString());
			return;
		}

		message.append(": ");
		boolean first = true;
		for (Map.Entry<String, String> entry: effectiveLevels.entrySet()) {
			if (!first) {
				message.append(", ");
			}

			first = false;
			message.append(entry.getKey());
			message.append('=');
			message.append(entry.getValue());
			message.append(" (");
			message.append(persistentLevels.containsKey(entry.getKey()) ? "runtime" : deployedLevels.containsKey(entry.getKey()) ? "packaged" : "effective");
			message.append(')');
		}

		logger.info(message.toString());
	}

}
