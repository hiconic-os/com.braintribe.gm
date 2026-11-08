package com.braintribe.logging.level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
import com.braintribe.logging.level.persistence.LogLevelProfilePersistence;

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
		Map<String, String> deployedLevels = getPackagedLogLevels();
		Map<String, String> persistentLevels = getRuntimeLogLevels();
		logInvalidLogLevels("packaged", deployedLevels);
		logInvalidLogLevels("runtime", persistentLevels);
		Map<String, String> effectiveLevels = getEffectiveLogLevels();
		Set<String> configuredLevels = new LinkedHashSet<>(logLevelFramework.getConfiguredLogLevels().keySet());

		logLevelFramework.clearLogLevels(configuredLevels);
		logLevelFramework.applyLogLevels(effectiveLevels);
		logAppliedLevels(deployedLevels, persistentLevels, effectiveLevels, configuredLevels);
	}

	public Map<String, LogLevelEntry> describeLogLevels() {
		return logLevelConfiguration.describeLogLevels(getPackagedLogLevels(), getRuntimeLogLevels(),
				logLevelFramework.getConfiguredLogLevels());
	}

	public Map<String, String> getPackagedLogLevels() {
		return mergeProfiles(getPackagedProfiles(), getPackagedProfileOrder());
	}

	public Map<String, String> getRuntimeLogLevels() {
		return mergeProfiles(getRuntimeProfiles(), getEffectiveProfileOrder());
	}

	public Map<String, String> getEffectiveLogLevels() {
		Map<String, Map<String, String>> profiles = new LinkedHashMap<>(getPackagedProfiles());
		profiles.putAll(getRuntimeProfiles());
		return mergeProfiles(profiles, getEffectiveProfileOrder());
	}

	public String getRootLogLevel() {
		return logLevelFramework.getEffectiveLogLevel(LogLevelNames.ROOT);
	}

	public Map<String, Map<String, String>> getPackagedProfiles() {
		return profiles(deployedLogLevels, LogLevelProfileNames.PACKAGED_DEFAULT);
	}

	public Map<String, Map<String, String>> getRuntimeProfiles() {
		return profiles(persistentLogLevels, LogLevelProfileNames.RUNTIME_DEFAULT);
	}

	public List<String> getPackagedProfileOrder() {
		if (deployedLogLevels instanceof LogLevelProfilePersistence) {
			LogLevelProfilePersistence profilePersistence = (LogLevelProfilePersistence) deployedLogLevels;
			List<String> order = profilePersistence.getProfileCascade();
			if (order != null && !order.isEmpty())
				return Collections.unmodifiableList(new ArrayList<>(order));
		}
		return Collections.singletonList(LogLevelProfileNames.PACKAGED_DEFAULT);
	}

	public List<String> getRuntimeProfileOrder() {
		if (persistentLogLevels instanceof LogLevelProfilePersistence) {
			LogLevelProfilePersistence profilePersistence = (LogLevelProfilePersistence) persistentLogLevels;
			List<String> order = profilePersistence.getProfileCascade();
			return order == null ? Collections.<String>emptyList() : Collections.unmodifiableList(new ArrayList<>(order));
		}
		return persistentLogLevels.getLogLevels().isEmpty() ? Collections.<String>emptyList()
				: Collections.singletonList(LogLevelProfileNames.RUNTIME_DEFAULT);
	}

	public List<String> getEffectiveProfileOrder() {
		List<String> runtimeOrder = getRuntimeProfileOrder();
		return runtimeOrder.isEmpty() ? getPackagedProfileOrder() : runtimeOrder;
	}

	public boolean isRuntimeProfileOrderActive() {
		return !getRuntimeProfileOrder().isEmpty();
	}

	public Set<String> getKnownLoggerNames() {
		return logLevelFramework.getKnownLoggerNames();
	}

	public Maybe<Void> assignRuntimeLogLevel(String name, String level) {
		return updateRuntimeProfileLogLevels(LogLevelProfileNames.RUNTIME_DEFAULT, Collections.singletonMap(name, level),
				Collections.<String>emptySet());
	}

	public Maybe<Void> resetRuntimeLogLevel(String name) {
		return updateRuntimeProfileLogLevels(LogLevelProfileNames.RUNTIME_DEFAULT, Collections.<String, String>emptyMap(),
				Collections.singleton(name));
	}

	public Maybe<Void> clearRuntimeLogLevels() {
		try {
			if (persistentLogLevels instanceof LogLevelProfilePersistence)
				((LogLevelProfilePersistence) persistentLogLevels).clearProfileLogLevels(LogLevelProfileNames.RUNTIME_DEFAULT);
			else
				persistentLogLevels.clearLogLevels();
			return Maybe.complete(null);
		} catch (Exception e) {
			return InternalError.from(e, "Failed to clear runtime log levels").asMaybe();
		}
	}

	public Maybe<Void> updateRuntimeLogLevels(Map<String, String> levels, Set<String> namesToRemove) {
		return updateRuntimeProfileLogLevels(LogLevelProfileNames.RUNTIME_DEFAULT, levels, namesToRemove);
	}

	public Maybe<Void> updateRuntimeProfileLogLevels(String profile, Map<String, String> levels, Set<String> namesToRemove) {
		try {
			String requiredProfile = requireMutableRuntimeProfile(profile);
			if (persistentLogLevels instanceof LogLevelProfilePersistence) {
				LogLevelProfilePersistence profilePersistence = (LogLevelProfilePersistence) persistentLogLevels;
				profilePersistence.updateProfileLogLevels(requiredProfile, levels == null ? Collections.<String, String>emptyMap() : levels,
						namesToRemove == null ? Collections.<String>emptySet() : namesToRemove);
				ensureProfileInRuntimeOrder(requiredProfile);
			} else {
				if (!LogLevelProfileNames.RUNTIME_DEFAULT.equals(requiredProfile))
					throw new UnsupportedOperationException("Runtime log-level profiles are not enabled");
				persistentLogLevels.updateLogLevels(levels == null ? Collections.<String, String>emptyMap() : levels,
						namesToRemove == null ? Collections.<String>emptySet() : namesToRemove);
			}
			return Maybe.complete(null);
		} catch (Exception e) {
			return InternalError.from(e, "Failed to update runtime log-level profile").asMaybe();
		}
	}

	public Maybe<Void> deleteRuntimeProfile(String profile) {
		try {
			String requiredProfile = requireMutableRuntimeProfile(profile);
			if (getEffectiveProfileOrder().contains(requiredProfile))
				throw new IllegalStateException("Log-level profile is still used by the effective profile order: " + requiredProfile);
			requireRuntimeProfilePersistence().clearProfileLogLevels(requiredProfile);
			return Maybe.complete(null);
		} catch (Exception e) {
			return InternalError.from(e, "Failed to delete runtime log-level profile").asMaybe();
		}
	}

	public Maybe<Void> mutateRuntimeProfileOrder(com.braintribe.gm.model.logging.level.api.LogLevelProfileOrderMutation mutation,
			String profile, Integer position) {
		try {
			Objects.requireNonNull(mutation, "mutation");
			LogLevelProfilePersistence persistence = requireRuntimeProfilePersistence();
			if (mutation == com.braintribe.gm.model.logging.level.api.LogLevelProfileOrderMutation.RESET) {
				persistence.clearProfileCascade();
				return Maybe.complete(null);
			}

			String requiredProfile = requireExistingProfile(profile);
			List<String> order = new ArrayList<>(getRuntimeProfileOrder());
			if (order.isEmpty())
				order.addAll(getPackagedProfileOrder());

			switch (mutation) {
				case APPEND:
					if (!order.contains(requiredProfile))
						order.add(requiredProfile);
					break;
				case INSERT:
				case MOVE:
					order.remove(requiredProfile);
					int index = position == null ? order.size() : Math.max(0, Math.min(position, order.size()));
					order.add(index, requiredProfile);
					break;
				case REMOVE:
					order.remove(requiredProfile);
					break;
				case RESET:
					throw new IllegalStateException("Handled above");
			}
			if (order.isEmpty())
				throw new IllegalArgumentException("The effective log-level profile order cannot be empty; use reset instead");
			persistence.updateProfileCascade(order);
			return Maybe.complete(null);
		} catch (Exception e) {
			return InternalError.from(e, "Failed to update log-level profile order").asMaybe();
		}
	}

	private void ensureProfileInRuntimeOrder(String profile) {
		LogLevelProfilePersistence persistence = requireRuntimeProfilePersistence();
		List<String> order = new ArrayList<>(getRuntimeProfileOrder());
		if (order.isEmpty())
			order.addAll(getPackagedProfileOrder());
		if (!order.contains(profile)) {
			order.add(profile);
			persistence.updateProfileCascade(order);
		}
	}

	private String requireExistingProfile(String profile) {
		String required = requireProfile(profile);
		if (!getPackagedProfiles().containsKey(required) && !getRuntimeProfiles().containsKey(required))
			throw new IllegalArgumentException("Unknown log-level profile: " + required);
		return required;
	}

	private String requireMutableRuntimeProfile(String profile) {
		String required = requireProfile(profile == null ? LogLevelProfileNames.RUNTIME_DEFAULT : profile);
		if (getPackagedProfiles().containsKey(required))
			throw new IllegalArgumentException("Packaged log-level profiles are immutable: " + required);
		return required;
	}

	private static String requireProfile(String profile) {
		String required = profile == null ? "" : profile.trim();
		if (required.isEmpty())
			throw new IllegalArgumentException("Log-level profile must not be empty");
		return required;
	}

	private LogLevelProfilePersistence requireRuntimeProfilePersistence() {
		if (persistentLogLevels instanceof LogLevelProfilePersistence)
			return (LogLevelProfilePersistence) persistentLogLevels;
		throw new UnsupportedOperationException("Runtime log-level profiles are not enabled");
	}

	private static Map<String, Map<String, String>> profiles(LogLevelPersistence persistence, String defaultProfile) {
		if (persistence instanceof LogLevelProfilePersistence)
			return copyProfiles(((LogLevelProfilePersistence) persistence).getProfiles());
		Map<String, String> levels = persistence.getLogLevels();
		return levels == null || levels.isEmpty() ? Collections.<String, Map<String, String>>emptyMap()
				: Collections.singletonMap(defaultProfile, new LinkedHashMap<>(levels));
	}

	private static Map<String, Map<String, String>> copyProfiles(Map<String, Map<String, String>> source) {
		Map<String, Map<String, String>> copy = new LinkedHashMap<>();
		if (source != null)
			source.forEach((name, levels) -> copy.put(name,
					levels == null ? Collections.<String, String>emptyMap() : new LinkedHashMap<>(levels)));
		return copy;
	}

	private Map<String, String> mergeProfiles(Map<String, Map<String, String>> profiles, List<String> order) {
		Map<String, String> result = new LinkedHashMap<>();
		if (order != null)
			for (String profile : order) {
				Map<String, String> levels = profiles.get(profile);
				if (levels != null)
					levels.forEach((name, level) -> {
						if (LogLevelConfiguration.isSupportedLogLevel(level))
							result.put(name, level);
					});
			}
		return result;
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
			message.append(appliedSource(entry.getKey(), deployedLevels, persistentLevels));
			message.append(')');
		}

		logger.info(message.toString());
	}

	private String appliedSource(String name, Map<String, String> deployedLevels, Map<String, String> persistentLevels) {
		if (persistentLevels != null && persistentLevels.containsKey(name) && LogLevelConfiguration.isSupportedLogLevel(persistentLevels.get(name))) {
			return "runtime";
		}
		if (deployedLevels != null && deployedLevels.containsKey(name) && LogLevelConfiguration.isSupportedLogLevel(deployedLevels.get(name))) {
			return "packaged";
		}
		return "effective";
	}

	private void logInvalidLogLevels(String source, Map<String, String> levels) {
		if (levels == null || levels.isEmpty()) {
			return;
		}

		for (Map.Entry<String, String> entry: levels.entrySet()) {
			if (!LogLevelConfiguration.isSupportedLogLevel(entry.getValue())) {
				logger.warn("Ignoring unsupported " + source + " log level [" + entry.getValue() + "] for logger [" + entry.getKey() + "]");
			}
		}
	}

}
