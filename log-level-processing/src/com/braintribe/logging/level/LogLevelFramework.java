package com.braintribe.logging.level;

import java.util.Map;
import java.util.Set;

public interface LogLevelFramework {
	Map<String, String> getConfiguredLogLevels();
	Set<String> getKnownLoggerNames();
	void applyLogLevels(Map<String, String> levels);
	void clearLogLevels(Set<String> loggerNames);
}
