package com.braintribe.logging.level;

import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class JulLogLevelFramework implements LogLevelFramework {
	public static final String LEVEL_TRACE = "TRACE";
	public static final String LEVEL_DEBUG = "DEBUG";
	public static final String LEVEL_INFO = "INFO";
	public static final String LEVEL_WARN = "WARN";
	public static final String LEVEL_ERROR = "ERROR";
	public static final String LEVEL_FATAL = "FATAL";

	@Override
	public Map<String, String> getConfiguredLogLevels() {
		Map<String, String> levels = new LinkedHashMap<>();
		LogManager logManager = LogManager.getLogManager();

		Enumeration<String> loggerNames = logManager.getLoggerNames();
		while (loggerNames.hasMoreElements()) {
			String name = loggerNames.nextElement();
			Logger logger = logManager.getLogger(name);

			if (logger != null && logger.getLevel() != null) {
				levels.put(name, fromJulLevel(logger.getLevel()));
			}
		}

		return levels;
	}

	@Override
	public Set<String> getKnownLoggerNames() {
		Set<String> loggerNames = new TreeSet<>(new StructuredPackageComparator());
		LogManager logManager = LogManager.getLogManager();

		Enumeration<String> names = logManager.getLoggerNames();
		while (names.hasMoreElements()) {
			loggerNames.add(names.nextElement());
		}

		for (Package pkg: Package.getPackages()) {
			loggerNames.add(pkg.getName());
		}

		return loggerNames;
	}

	@Override
	public void applyLogLevels(Map<String, String> levels) {
		if (levels == null || levels.isEmpty()) {
			return;
		}

		for (Map.Entry<String, String> entry: levels.entrySet()) {
			Logger.getLogger(entry.getKey()).setLevel(toJulLevel(entry.getValue()));
		}
	}

	@Override
	public void clearLogLevels(Set<String> loggerNames) {
		if (loggerNames == null || loggerNames.isEmpty()) {
			return;
		}

		for (String loggerName: loggerNames) {
			Logger.getLogger(loggerName).setLevel(null);
		}
	}

	public static Level toJulLevel(String levelName) {
		if (levelName == null) {
			return null;
		}

		switch (levelName.trim().toUpperCase(Locale.ROOT)) {
			case LEVEL_TRACE:
				return Level.FINEST;
			case LEVEL_DEBUG:
				return Level.FINE;
			case LEVEL_INFO:
				return Level.INFO;
			case LEVEL_WARN:
				return Level.WARNING;
			case LEVEL_ERROR:
			case LEVEL_FATAL:
				return Level.SEVERE;
			default:
				throw new IllegalArgumentException("Unsupported log level: " + levelName);
		}
	}

	public static String fromJulLevel(Level julLevel) {
		if (julLevel == null) {
			return null;
		}

		int value = julLevel.intValue();

		if (value <= Level.FINER.intValue()) {
			return LEVEL_TRACE;
		}
		if (value <= Level.CONFIG.intValue()) {
			return LEVEL_DEBUG;
		}
		if (value <= Level.INFO.intValue()) {
			return LEVEL_INFO;
		}
		if (value <= Level.WARNING.intValue()) {
			return LEVEL_WARN;
		}
		return LEVEL_ERROR;
	}
}
