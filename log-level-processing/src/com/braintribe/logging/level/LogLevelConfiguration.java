package com.braintribe.logging.level;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class LogLevelConfiguration {
	public static final String SOURCE_BASE = "base";
	public static final String SOURCE_DEPLOYMENT = "deployment";
	public static final String SOURCE_PERSISTENCE = "persistence";
	public static final String LEVEL_TRACE = "TRACE";
	public static final String LEVEL_DEBUG = "DEBUG";
	public static final String LEVEL_INFO = "INFO";
	public static final String LEVEL_WARN = "WARN";
	public static final String LEVEL_ERROR = "ERROR";
	public static final String LEVEL_FATAL = "FATAL";

	public Map<String, String> resolveEffectiveLogLevels(Map<String, String> deployedLevels, Map<String, String> persistentLevels) {
		Map<String, String> effectiveLevels = new LinkedHashMap<>();

		if (deployedLevels != null) {
			putNormalized(effectiveLevels, deployedLevels);
		}

		if (persistentLevels != null) {
			putNormalized(effectiveLevels, persistentLevels);
		}

		return effectiveLevels;
	}

	public Map<String, LogLevelEntry> describeLogLevels(Map<String, String> deployedLevels, Map<String, String> persistentLevels,
			Map<String, String> frameworkLevels) {
		Map<String, LogLevelEntry> entries = new TreeMap<>(new StructuredPackageComparator());

		addNames(entries, deployedLevels == null ? null : deployedLevels.keySet());
		addNames(entries, persistentLevels == null ? null : persistentLevels.keySet());

		Map<String, String> effectiveLevels = resolveEffectiveLogLevels(deployedLevels, persistentLevels);

		for (LogLevelEntry entry: entries.values()) {
			entry.deployedLevel = deployedLevels == null ? null : deployedLevels.get(entry.name);
			entry.persistentLevel = persistentLevels == null ? null : persistentLevels.get(entry.name);
			entry.frameworkLevel = frameworkLevels == null ? null : frameworkLevels.get(entry.name);
			entry.effectiveLevel = effectiveLevels.get(entry.name);

			if (entry.persistentLevel != null) {
				entry.source = SOURCE_PERSISTENCE;
			} else if (entry.deployedLevel != null) {
				entry.source = SOURCE_DEPLOYMENT;
			} else {
				entry.source = SOURCE_BASE;
			}
		}

		return entries;
	}

	public void applyEffectiveLogLevels(LogLevelFramework framework, Map<String, String> deployedLevels, Map<String, String> persistentLevels) {
		Map<String, String> effectiveLevels = resolveEffectiveLogLevels(deployedLevels, persistentLevels);
		Set<String> configuredLevels = new LinkedHashSet<>(framework.getConfiguredLogLevels().keySet());

		framework.clearLogLevels(configuredLevels);
		framework.applyLogLevels(effectiveLevels);
	}

	private void addNames(Map<String, LogLevelEntry> entries, Iterable<String> names) {
		if (names == null) {
			return;
		}

		for (String name: names) {
			if (name != null) {
				entries.put(name, new LogLevelEntry(name));
			}
		}
	}

	private void putNormalized(Map<String, String> target, Map<String, String> source) {
		for (Map.Entry<String, String> entry: source.entrySet()) {
			String level = normalizeLogLevel(entry.getValue());
			if (level != null) {
				target.put(entry.getKey(), level);
			}
		}
	}

	public static boolean isSupportedLogLevel(String level) {
		return normalizeLogLevel(level) != null;
	}

	public static String normalizeLogLevel(String level) {
		if (level == null) {
			return null;
		}

		String normalized = level.trim().toUpperCase(Locale.ROOT);
		switch (normalized) {
			case LEVEL_TRACE:
			case LEVEL_DEBUG:
			case LEVEL_INFO:
			case LEVEL_WARN:
			case LEVEL_ERROR:
			case LEVEL_FATAL:
				return normalized;
			default:
				return null;
		}
	}

	public static class LogLevelEntry {
		public final String name;
		public String effectiveLevel;
		public String persistentLevel;
		public String deployedLevel;
		public String frameworkLevel;
		public String source;

		public LogLevelEntry(String name) {
			this.name = name;
		}
	}
}
