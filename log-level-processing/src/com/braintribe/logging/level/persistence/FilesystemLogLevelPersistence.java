package com.braintribe.logging.level.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;

public class FilesystemLogLevelPersistence implements LogLevelPersistence {
	private File logLevelFile;
	private Function<String, String> propertyLookup;

	public FilesystemLogLevelPersistence(File logLevelFile) {
		super();
		this.logLevelFile = logLevelFile;
	}

	public FilesystemLogLevelPersistence(File logLevelFile, Function<String, String> propertyLookup) {
		this(logLevelFile);
		this.propertyLookup = Objects.requireNonNull(propertyLookup, "propertyLookup");
	}

	@Override
	public synchronized Map<String, String> getLogLevels() {
		Properties properties = loadProperties();
		Map<String, String> levels = new LinkedHashMap<>();

		for (String name: properties.stringPropertyNames()) {
			levels.put(name, LogLevelValueResolver.resolveValue(properties.getProperty(name), propertyLookup));
		}

		return levels;
	}

	public void setPropertyLookup(Function<String, String> propertyLookup) {
		this.propertyLookup = propertyLookup;
	}

	@Override
	public synchronized void updateLogLevels(Map<String, String> levels, Set<String> namesToRemove) {
		boolean hasLevels = levels != null && levels.isEmpty() == false;
		boolean hasNamesToRemove = namesToRemove != null && namesToRemove.isEmpty() == false;

		if (hasLevels == false && hasNamesToRemove == false) {
			return;
		}

		Properties properties = loadProperties();

		if (hasNamesToRemove) {
			for (String name: namesToRemove) {
				properties.remove(name);
			}
		}

		if (hasLevels) {
			for (Map.Entry<String, String> entry: levels.entrySet()) {
				properties.setProperty(entry.getKey(), entry.getValue());
			}
		}

		storeProperties(properties);
	}

	@Override
	public synchronized void clearLogLevels() {
		storeProperties(new Properties());
	}

	private Properties loadProperties() {
		Properties properties = new Properties();

		if (logLevelFile == null || logLevelFile.exists() == false) {
			return properties;
		}

		try (Reader reader = new InputStreamReader(new FileInputStream(logLevelFile), StandardCharsets.UTF_8)) {
			properties.load(reader);
		} catch (IOException e) {
			throw new RuntimeException("Could not read log levels from " + logLevelFile.getAbsolutePath(), e);
		}

		return properties;
	}

	private void storeProperties(Properties properties) {
		if (logLevelFile == null) {
			return;
		}

		File parent = logLevelFile.getParentFile();
		if (parent != null && parent.exists() == false && parent.mkdirs() == false) {
			throw new RuntimeException("Could not create directory " + parent.getAbsolutePath());
		}

		try (Writer writer = new OutputStreamWriter(new FileOutputStream(logLevelFile), StandardCharsets.UTF_8)) {
			properties.store(writer, null);
		} catch (IOException e) {
			throw new RuntimeException("Could not write log levels to " + logLevelFile.getAbsolutePath(), e);
		}
	}

}
