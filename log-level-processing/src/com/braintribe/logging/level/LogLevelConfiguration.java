package com.braintribe.logging.level;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class LogLevelConfiguration {
	public static final String SOURCE_BASE = "base";
	public static final String SOURCE_DEPLOYMENT = "deployment";
	public static final String SOURCE_PERSISTENCE = "persistence";

	public Map<String, String> resolveEffectiveLogLevels(Map<String, String> deployedLevels, Map<String, String> persistentLevels) {
		Map<String, String> effectiveLevels = new LinkedHashMap<>();

		if (deployedLevels != null) {
			effectiveLevels.putAll(deployedLevels);
		}

		if (persistentLevels != null) {
			effectiveLevels.putAll(persistentLevels);
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
