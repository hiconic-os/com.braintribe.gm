package com.braintribe.logging.level.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.junit.Test;

import com.braintribe.logging.level.JulLogLevelFramework;

public class JulLogLevelFrameworkLifecycleTest {

	@Test
	public void retainsConfiguredParentLevelForLoggersCreatedLater() throws Exception {
		String parentName = "test.log.level." + UUID.randomUUID();
		String childName = parentName + ".Processor";
		JulLogLevelFramework framework = new JulLogLevelFramework();

		try {
			framework.applyLogLevels(Collections.singletonMap(parentName, JulLogLevelFramework.LEVEL_DEBUG));

			WeakReference<Logger> parentReference = configuredLoggerReference(parentName);
			awaitCollection(parentReference);
			assertThat(parentReference.get()).as("an explicitly configured logger must remain alive").isNotNull();

			Logger child = Logger.getLogger(childName);
			assertThat(child.isLoggable(Level.FINE))
					.as("a logger created later must inherit the configured parent level")
					.isTrue();

			Map<String, String> parentAndChildLevels = new LinkedHashMap<>();
			parentAndChildLevels.put(parentName, JulLogLevelFramework.LEVEL_DEBUG);
			parentAndChildLevels.put(childName, JulLogLevelFramework.LEVEL_DEBUG);
			reapply(framework, parentAndChildLevels, parentName, childName);
			assertThat(child.isLoggable(Level.FINE)).isTrue();

			reapply(framework, Collections.singletonMap(parentName, JulLogLevelFramework.LEVEL_DEBUG), parentName, childName);
			assertThat(child.getLevel()).isNull();
			assertThat(child.isLoggable(Level.FINE))
					.as("the existing child now keeps the recreated parent hierarchy alive")
					.isTrue();
		} finally {
			java.util.Set<String> loggerNames = new LinkedHashSet<>();
			loggerNames.add(parentName);
			loggerNames.add(childName);
			framework.clearLogLevels(loggerNames);
		}
	}

	private static void reapply(JulLogLevelFramework framework, Map<String, String> levels, String... loggerNames) {
		Set<String> namesToClear = new LinkedHashSet<>();
		Collections.addAll(namesToClear, loggerNames);
		framework.clearLogLevels(namesToClear);
		framework.applyLogLevels(levels);
	}

	private static WeakReference<Logger> configuredLoggerReference(String name) {
		Logger logger = LogManager.getLogManager().getLogger(name);
		assertThat(logger).isNotNull();
		return new WeakReference<>(logger);
	}

	private static void awaitCollection(WeakReference<?> reference) throws InterruptedException {
		for (int attempt = 0; attempt < 100 && reference.get() != null; attempt++) {
			System.gc();
			System.runFinalization();
			Thread.sleep(10);
		}
	}
}
