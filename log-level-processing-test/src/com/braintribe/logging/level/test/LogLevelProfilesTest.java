package com.braintribe.logging.level.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.braintribe.gm.model.logging.level.api.LogLevelProfileOrderMutation;
import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.logging.level.LogLevelFramework;
import com.braintribe.logging.level.LogLevelManager;
import com.braintribe.logging.level.LogLevelRuntimeUpdater;
import com.braintribe.logging.level.LogLevelUpdateDispatcher;
import com.braintribe.logging.level.persistence.FilesystemLogLevelProfilePersistence;
import com.braintribe.logging.level.persistence.LogLevelProfilePersistence;

public class LogLevelProfilesTest {
	@Test
	public void resolvesProfilesInOrderAndResetsToPackagedOrder() {
		MemoryProfiles packaged = profiles(
				profile("packaged-default", levels("a", "INFO", "b", "WARN")),
				profile("platform-debug", levels("a", "DEBUG")));
		packaged.cascade.add("packaged-default");

		MemoryProfiles runtime = profiles(profile("incident", levels("b", "TRACE")));
		LogLevelManager manager = new LogLevelManager(packaged, runtime, new MemoryFramework());

		assertThat(manager.getEffectiveProfileOrder()).containsExactly("packaged-default");
		assertThat(manager.getEffectiveLogLevels()).containsExactlyEntriesOf(levels("a", "INFO", "b", "WARN"));

		assertThat(manager.mutateRuntimeProfileOrder(LogLevelProfileOrderMutation.APPEND, "incident", null).isSatisfied()).isTrue();
		assertThat(manager.getEffectiveProfileOrder()).containsExactly("packaged-default", "incident");
		assertThat(manager.getEffectiveLogLevels()).containsExactlyEntriesOf(levels("a", "INFO", "b", "TRACE"));

		assertThat(manager.mutateRuntimeProfileOrder(LogLevelProfileOrderMutation.RESET, null, null).isSatisfied()).isTrue();
		assertThat(manager.isRuntimeProfileOrderActive()).isFalse();
		assertThat(manager.getEffectiveLogLevels()).containsExactlyEntriesOf(levels("a", "INFO", "b", "WARN"));
	}

	@Test
	public void dispatchesEverySatisfiedProfileMutation() {
		MemoryProfiles packaged = profiles(profile("packaged-default", levels("a", "INFO")));
		packaged.cascade.add("packaged-default");
		MemoryProfiles runtime = profiles();
		LogLevelManager manager = new LogLevelManager(packaged, runtime, new MemoryFramework());
		CountingDispatcher dispatcher = new CountingDispatcher();
		dispatcher.setLogLevelManager(manager);
		LogLevelRuntimeUpdater updater = new LogLevelRuntimeUpdater();
		updater.setLogLevelManager(manager);
		updater.setUpdateDispatcher(dispatcher);

		assertThat(updater.updateRuntimeProfile("incident", levels("a", "DEBUG"), Collections.<String>emptySet()).isSatisfied()).isTrue();
		assertThat(dispatcher.count).isEqualTo(1);
		assertThat(manager.getEffectiveProfileOrder()).containsExactly("packaged-default", "incident");
		assertThat(manager.getEffectiveLogLevels()).containsEntry("a", "DEBUG");

		assertThat(updater.mutateProfileOrder(LogLevelProfileOrderMutation.REMOVE, "incident", null).isSatisfied()).isTrue();
		assertThat(dispatcher.count).isEqualTo(2);
		assertThat(manager.getEffectiveLogLevels()).containsEntry("a", "INFO");
	}

	@Test
	public void filesystemPersistenceSurvivesReloadAndReset() throws Exception {
		File folder = Files.createTempDirectory("log-level-profiles-").toFile();
		File profilesFile = new File(folder, "runtime-log-levels.ini");
		File orderFile = new File(folder, "runtime-profile-order.txt");
		FilesystemLogLevelProfilePersistence persistence = new FilesystemLogLevelProfilePersistence(profilesFile, orderFile);

		persistence.updateProfileLogLevels("incident-debug", levels("com.example.Service", "DEBUG", "name:with=special", "TRACE"),
				Collections.<String>emptySet());
		persistence.updateProfileCascade(java.util.Arrays.asList("packaged-default", "incident-debug"));

		FilesystemLogLevelProfilePersistence reloaded = new FilesystemLogLevelProfilePersistence(profilesFile, orderFile);
		assertThat(reloaded.getProfiles().get("incident-debug"))
				.containsEntry("com.example.Service", "DEBUG")
				.containsEntry("name:with=special", "TRACE");
		assertThat(reloaded.getProfileCascade()).containsExactly("packaged-default", "incident-debug");
		reloaded.updateProfileCascade(Collections.emptyList());
		assertThat(new FilesystemLogLevelProfilePersistence(profilesFile, orderFile).hasProfileCascade()).isTrue();
		assertThat(reloaded.getProfileCascade()).isEmpty();

		reloaded.clearProfileCascade();
		assertThat(reloaded.hasProfileCascade()).isFalse();
		assertThat(reloaded.getProfileCascade()).isEmpty();
		assertThat(reloaded.getProfiles()).containsKey("incident-debug");
	}

	@Test
	public void removingLastPackagedProfileKeepsAnExplicitlyEmptyOrder() {
		MemoryProfiles packaged = profiles(profile("packaged-default", levels("a", "INFO")));
		packaged.cascade.add("packaged-default");
		MemoryProfiles runtime = profiles();
		LogLevelManager manager = new LogLevelManager(packaged, runtime, new MemoryFramework());

		assertThat(manager.replaceRuntimeProfileOrder(Collections.emptyList()).isSatisfied()).isTrue();
		assertThat(manager.isRuntimeProfileOrderActive()).isTrue();
		assertThat(manager.getEffectiveProfileOrder()).isEmpty();
		assertThat(manager.getEffectiveLogLevels()).isEmpty();

		assertThat(manager.mutateRuntimeProfileOrder(LogLevelProfileOrderMutation.APPEND, "packaged-default", null).isSatisfied()).isTrue();
		assertThat(manager.getEffectiveProfileOrder()).containsExactly("packaged-default");
		assertThat(manager.mutateRuntimeProfileOrder(LogLevelProfileOrderMutation.REMOVE, "packaged-default", null).isSatisfied()).isTrue();
		assertThat(manager.getEffectiveProfileOrder()).isEmpty();

		assertThat(manager.mutateRuntimeProfileOrder(LogLevelProfileOrderMutation.RESET, null, null).isSatisfied()).isTrue();
		assertThat(manager.isRuntimeProfileOrderActive()).isFalse();
		assertThat(manager.getEffectiveProfileOrder()).containsExactly("packaged-default");
	}

	private static MemoryProfiles profiles(Map.Entry<String, Map<String, String>>... profiles) {
		MemoryProfiles result = new MemoryProfiles();
		for (Map.Entry<String, Map<String, String>> profile : profiles)
			result.profiles.put(profile.getKey(), new LinkedHashMap<>(profile.getValue()));
		return result;
	}

	private static Map.Entry<String, Map<String, String>> profile(String name, Map<String, String> levels) {
		return new java.util.AbstractMap.SimpleImmutableEntry<>(name, levels);
	}

	private static Map<String, String> levels(String... entries) {
		Map<String, String> result = new LinkedHashMap<>();
		for (int i = 0; i < entries.length; i += 2)
			result.put(entries[i], entries[i + 1]);
		return result;
	}

	private static class MemoryProfiles implements LogLevelProfilePersistence {
		final Map<String, Map<String, String>> profiles = new LinkedHashMap<>();
		final List<String> cascade = new ArrayList<>();
		boolean cascadeActive;

		@Override public Map<String, Map<String, String>> getProfiles() { return profiles; }
		@Override public List<String> getProfileCascade() { return new ArrayList<>(cascade); }
		@Override public boolean hasProfileCascade() { return cascadeActive || !cascade.isEmpty(); }
		@Override public void updateProfileLogLevels(String profile, Map<String, String> levels, Set<String> namesToRemove) {
			Map<String, String> values = profiles.computeIfAbsent(profile, key -> new LinkedHashMap<>());
			if (namesToRemove != null) namesToRemove.forEach(values::remove);
			if (levels != null) values.putAll(levels);
		}
		@Override public void clearProfileLogLevels(String profile) { profiles.remove(profile); }
		@Override public void updateProfileCascade(List<String> profiles) { cascade.clear(); cascade.addAll(profiles); cascadeActive = true; }
		@Override public void clearProfileCascade() { cascade.clear(); cascadeActive = false; }
	}

	private static class MemoryFramework implements LogLevelFramework {
		final Map<String, String> configured = new LinkedHashMap<>();
		@Override public Map<String, String> getConfiguredLogLevels() { return configured; }
		@Override public String getEffectiveLogLevel(String loggerName) { return configured.get(loggerName); }
		@Override public Set<String> getKnownLoggerNames() { return new LinkedHashSet<>(configured.keySet()); }
		@Override public void applyLogLevels(Map<String, String> levels) { configured.putAll(levels); }
		@Override public void clearLogLevels(Set<String> loggerNames) { loggerNames.forEach(configured::remove); }
	}

	private static class CountingDispatcher implements LogLevelUpdateDispatcher {
		int count;
		@Override public Maybe<Void> dispatchUpdate() { count++; return Maybe.complete(null); }
		@Override public void setLogLevelManager(LogLevelManager logLevelManager) { }
	}
}
