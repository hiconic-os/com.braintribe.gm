package com.braintribe.logging.level.persistence;

import java.util.Map;
import java.util.Set;

public interface LogLevelPersistence {
	Map<String, String> getLogLevels();
	void updateLogLevels(Map<String, String> levels, Set<String> namesToRemove);
	void clearLogLevels();
}
