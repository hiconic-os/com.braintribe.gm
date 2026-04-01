package com.braintribe.gm.config.yaml;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.braintribe.gm.config.yaml.index.ClasspathEntry;
import com.braintribe.logging.Logger;

/**
 * @author peter.gazdik
 */
/* package */ class ConfigurationEntrySorter {

	private static final Logger log = Logger.getLogger(ConfigurationEntrySorter.ComparableEntry.class);

	/**
	 * Sorts {@link ClasspathEntry}s for URLS matching "jar:file:/path/to/jar!/some/conf/my-entity....yaml".
	 * <p>
	 * {@link ModeledYamlConfiguration}
	 */
	public static List<ClasspathEntry> sortClasspathEntries(List<ClasspathEntry> cpEntries) {
		List<ComparableEntry<ClasspathEntry>> comparableEntries = cpEntries.stream() //
				.map(ConfigurationEntrySorter::cpEntryToComparable) //
				.filter(e -> e != null) //
				.collect(Collectors.toList());

		comparableEntries.sort(Comparator.naturalOrder());

		return comparableEntries.stream() //
				.map(e -> e.entry) //
				.collect(Collectors.toList());
	}

	private static ComparableEntry<ClasspathEntry> cpEntryToComparable(ClasspathEntry cpEntry) {
		// e.g. "jar:file:/C:/maven-repo/res-on-cp/1.0/res-on-cp-1.0.jar!/config/my-config~use-case.disambig-8.yaml"
		String fullPath = cpEntry.url.toString();
		if (!fullPath.startsWith("jar:file:"))
			throw new IllegalStateException("Only classpath entries for jar files are supported for config loading, but found: " + fullPath);

		int i = fullPath.indexOf(".jar!/");
		if (i < 0)
			throw new IllegalStateException("Invalid classpath entry url: " + fullPath + ". Expected format: jar:file:/path/to/jar!/path/inside/jar");

		// e.g. jar:file:/C:/maven-repo/res-on-cp/1.0/res-on-cp-1.0
		String jarPath = fullPath.substring(0, i);
		i = jarPath.lastIndexOf("/");
		if (i < 0)
			throw new IllegalStateException("Invalid classpath entry url: " + fullPath + ". Expected format: jar:file:/path/to/jar!/path/inside/jar");

		// e.g. res-on-cp-1.0
		String artifactWithVersion = jarPath.substring(i + 1);
		i = artifactWithVersion.lastIndexOf("-");

		// e.g. res-on-cp
		String artifactId = i > 0 ? artifactWithVersion.substring(0, i) : artifactWithVersion;

		// e.g. config/my-config~use-case.disambig-8.yaml
		String path = cpEntry.path;
		if (!path.endsWith("yaml")) {
			log.warn("Only classpath entries for yaml files are supported for config loading, but found: " + fullPath);
			return null;
		}

		i = path.lastIndexOf("/");
		// e.g. my-config~use-case.disambig-8.yaml
		String fileName = i >= 0 ? path.substring(i + 1) : path;

		ComparableEntry<ClasspathEntry> result = new ComparableEntry<ClasspathEntry>(cpEntry, artifactId);
		fillDisambiguationAndPriority(result, fileName, "classpath entry file name [" + fullPath + "]");

		return result;
	}

	public static List<File> sortFiles(List<File> files) {
		List<ComparableEntry<File>> comparableEntries = files.stream() //
				.map(ConfigurationEntrySorter::fileToComparable) //
				.filter(e -> e != null) //
				.collect(Collectors.toList());

		comparableEntries.sort(Comparator.naturalOrder());

		return comparableEntries.stream() //
				.map(e -> e.entry) //
				.collect(Collectors.toList());
	}

	private static ComparableEntry<File> fileToComparable(File file) {
		ComparableEntry<File> result = new ComparableEntry<File>(file, "");
		fillDisambiguationAndPriority(result, file.getName(), "file [" + file.getPath() + "]");

		return result;
	}

	private static void fillDisambiguationAndPriority(ComparableEntry<?> entry, String fileName, String fileSource) {
		// e.g. my-config~use-case.disambig-8
		String fileNameNoExt = fileName.substring(0, fileName.length() - 5/* .yaml */);

		int i = fileNameNoExt.lastIndexOf(".");
		if (i <= 0)
			return;

		// Can be a priority, disambiguuator or both, e.g. "disambiguator-8", "disambiguator", "8"
		String disambiguatorPart = fileNameNoExt.substring(i + 1);

		i = disambiguatorPart.lastIndexOf("-");
		if (i > 0) {
			// e.g. disambiguator-8
			entry.disambiguator = disambiguatorPart.substring(0, i);
			String priorityPart = disambiguatorPart.substring(i + 1);

			entry.priority = parsePriority(priorityPart, fileSource);

		} else {
			// if numbers only, it is priority, otherwise it is disambiguator
			if (disambiguatorPart.matches("\\d+"))
				entry.priority = parsePriority(disambiguatorPart, fileSource);
			else
				entry.disambiguator = disambiguatorPart;
		}
	}

	private static int parsePriority(String priorityPart, String fileSource) {
		try {
			return Integer.parseInt(priorityPart);
		} catch (NumberFormatException e) {
			log.warn("Invalid priority in " + fileSource + ". Expected format: (disambig-)?<priority>.yaml, but found: " + priorityPart);
			return -1;
		}
	}

	static class ComparableEntry<E> implements Comparable<ComparableEntry<E>> {
		private final E entry;
		private int priority = -1;
		private String disambiguator = "";
		private final String artifactId;

		public ComparableEntry(E entry, String artifactId) {
			this.entry = entry;
			this.artifactId = artifactId;
		}

		@Override
		public int compareTo(ComparableEntry<E> o) {
			int result = Integer.compare(priority, o.priority);
			if (result != 0)
				return result;

			result = disambiguator.compareTo(o.disambiguator);
			if (result != 0)
				return result;

			return artifactId.compareTo(o.artifactId);
		}
	}

}
