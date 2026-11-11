package com.braintribe.logging.level.persistence;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.braintribe.logging.level.LogLevelProfileNames;

/** Profile-aware extension of the legacy single-map persistence contract. */
public interface LogLevelProfilePersistence extends LogLevelPersistence {
	Map<String, Map<String, String>> getProfiles();

	default List<String> getProfileCascade() {
		return Collections.emptyList();
	}

	default boolean hasProfileCascade() {
		return !getProfileCascade().isEmpty();
	}

	void updateProfileLogLevels(String profile, Map<String, String> levels, Set<String> namesToRemove);

	void clearProfileLogLevels(String profile);

	default void updateProfileCascade(List<String> profiles) {
		throw new UnsupportedOperationException("Profile cascades are read-only");
	}

	default void clearProfileCascade() {
		throw new UnsupportedOperationException("Profile cascades are read-only");
	}

	@Override
	default Map<String, String> getLogLevels() {
		Map<String, String> levels = getProfiles().get(LogLevelProfileNames.PACKAGED_DEFAULT);
		return levels == null ? Collections.<String, String>emptyMap() : levels;
	}

	@Override
	default void updateLogLevels(Map<String, String> levels, Set<String> namesToRemove) {
		updateProfileLogLevels(LogLevelProfileNames.PACKAGED_DEFAULT, levels, namesToRemove);
	}

	@Override
	default void clearLogLevels() {
		clearProfileLogLevels(LogLevelProfileNames.PACKAGED_DEFAULT);
	}
}
