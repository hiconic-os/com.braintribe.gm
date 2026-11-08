package com.braintribe.logging.level.persistence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/** Strict INI sections whose bodies use ordinary Java properties syntax. */
public final class LogLevelIniCodec {
	private LogLevelIniCodec() {
	}

	public static Map<String, Map<String, String>> read(Reader input) throws IOException {
		Map<String, Map<String, String>> profiles = new LinkedHashMap<>();
		try (BufferedReader reader = new BufferedReader(input)) {
			String profile = null;
			StringBuilder body = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				String trimmed = line.trim();
				if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
					flush(profiles, profile, body);
					profile = requireProfile(trimmed.substring(1, trimmed.length() - 1));
					body.setLength(0);
				} else {
					if (profile == null && !trimmed.isEmpty() && !trimmed.startsWith("#") && !trimmed.startsWith(";"))
						throw new IllegalArgumentException("Log-level INI entries must be placed inside a profile section");
					body.append(line).append('\n');
				}
			}
			flush(profiles, profile, body);
		}
		return profiles;
	}

	private static void flush(Map<String, Map<String, String>> profiles, String profile, StringBuilder body) throws IOException {
		if (profile == null)
			return;
		Properties properties = new Properties();
		properties.load(new StringReader(body.toString()));
		Map<String, String> levels = profiles.computeIfAbsent(profile, p -> new LinkedHashMap<>());
		for (String name : properties.stringPropertyNames())
			levels.put(name, properties.getProperty(name));
	}

	private static String requireProfile(String value) {
		String profile = value == null ? "" : value.trim();
		if (profile.isEmpty())
			throw new IllegalArgumentException("Log-level INI profile name must not be empty");
		if (profile.indexOf('/') >= 0)
			throw new IllegalArgumentException("Log-level INI profile name must not contain '/': " + profile);
		return profile;
	}
}
