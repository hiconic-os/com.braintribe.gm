package com.braintribe.logging.level.persistence;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** File fallback for local installations without the JDBC log-level store. */
public class FilesystemLogLevelProfilePersistence implements LogLevelProfilePersistence {
	private final File profilesFile;
	private final File cascadeFile;

	public FilesystemLogLevelProfilePersistence(File profilesFile, File cascadeFile) {
		this.profilesFile = profilesFile;
		this.cascadeFile = cascadeFile;
	}

	@Override
	public synchronized Map<String, Map<String, String>> getProfiles() {
		if (!profilesFile.isFile())
			return new LinkedHashMap<>();
		try (InputStreamReader reader = new InputStreamReader(Files.newInputStream(profilesFile.toPath()), StandardCharsets.UTF_8)) {
			return LogLevelIniCodec.read(reader);
		} catch (IOException e) {
			throw new RuntimeException("Could not read runtime log-level profiles from " + profilesFile, e);
		}
	}

	@Override
	public synchronized List<String> getProfileCascade() {
		if (!cascadeFile.isFile())
			return new ArrayList<>();
		try {
			List<String> result = new ArrayList<>();
			for (String line : Files.readAllLines(cascadeFile.toPath(), StandardCharsets.UTF_8)) {
				String value = line.trim();
				if (!value.isEmpty() && !value.startsWith("#"))
					result.add(value);
			}
			return result;
		} catch (IOException e) {
			throw new RuntimeException("Could not read runtime log-level profile cascade from " + cascadeFile, e);
		}
	}

	@Override
	public synchronized boolean hasProfileCascade() {
		return cascadeFile.isFile();
	}

	@Override
	public synchronized void updateProfileLogLevels(String profile, Map<String, String> levels, Set<String> namesToRemove) {
		Map<String, Map<String, String>> profiles = getProfiles();
		Map<String, String> profileLevels = profiles.computeIfAbsent(requireProfile(profile), p -> new LinkedHashMap<>());
		if (namesToRemove != null)
			for (String name : namesToRemove)
				profileLevels.remove(name);
		if (levels != null)
			profileLevels.putAll(levels);
		if (profileLevels.isEmpty())
			profiles.remove(profile);
		writeProfiles(profiles);
	}

	@Override
	public synchronized void clearProfileLogLevels(String profile) {
		Map<String, Map<String, String>> profiles = getProfiles();
		profiles.remove(requireProfile(profile));
		writeProfiles(profiles);
	}

	@Override
	public synchronized void updateProfileCascade(List<String> profiles) {
		List<String> cascade = profiles == null ? Collections.<String>emptyList() : profiles;
		Set<String> unique = new LinkedHashSet<>();
		for (String profile : cascade) {
			String value = requireProfile(profile);
			if (!unique.add(value))
				throw new IllegalArgumentException("Duplicate profile in log-level cascade: " + value);
		}
		write(cascadeFile, writer -> {
			for (String profile : cascade) {
				writer.write(profile.trim());
				writer.newLine();
			}
		});
	}

	@Override
	public synchronized void clearProfileCascade() {
		try {
			Files.deleteIfExists(cascadeFile.toPath());
		} catch (IOException e) {
			throw new RuntimeException("Could not clear runtime log-level profile cascade", e);
		}
	}

	private void writeProfiles(Map<String, Map<String, String>> profiles) {
		write(profilesFile, writer -> {
			for (Map.Entry<String, Map<String, String>> profile : profiles.entrySet()) {
				writer.write('[' + profile.getKey() + ']');
				writer.newLine();
				for (Map.Entry<String, String> level : profile.getValue().entrySet()) {
					writer.write(escape(level.getKey(), true));
					writer.write('=');
					writer.write(escape(level.getValue(), false));
					writer.newLine();
				}
				writer.newLine();
			}
		});
	}

	private void write(File target, WriterAction action) {
		try {
			File parent = target.getAbsoluteFile().getParentFile();
			if (parent != null)
				Files.createDirectories(parent.toPath());
			File temporary = new File(parent, target.getName() + ".tmp");
			try (BufferedWriter writer = Files.newBufferedWriter(temporary.toPath(), StandardCharsets.UTF_8)) {
				action.write(writer);
			}
			try {
				Files.move(temporary.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
			} catch (IOException unsupportedAtomicMove) {
				Files.move(temporary.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (IOException e) {
			throw new RuntimeException("Could not write runtime log-level profiles to " + target, e);
		}
	}

	private static String requireProfile(String profile) {
		String value = profile == null ? "" : profile.trim();
		if (value.isEmpty())
			throw new IllegalArgumentException("Log-level profile must not be empty");
		return value;
	}

	private static String escape(String value, boolean key) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			switch (c) {
			case '\\': result.append("\\\\"); break;
			case '\n': result.append("\\n"); break;
			case '\r': result.append("\\r"); break;
			case '\t': result.append("\\t"); break;
			case '=':
			case ':':
			case '#':
			case '!':
				if (key)
					result.append('\\');
				result.append(c);
				break;
			default: result.append(c);
			}
		}
		return result.toString();
	}

	@FunctionalInterface
	private interface WriterAction {
		void write(BufferedWriter writer) throws IOException;
	}
}
