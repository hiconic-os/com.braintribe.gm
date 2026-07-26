package com.braintribe.gm.config.yaml.index;

import static com.braintribe.utils.lcd.CollectionTools2.newList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import com.braintribe.logging.Logger;
import com.braintribe.utils.lcd.Lazy;

/**
 * @author peter.gazdik
 */
public class ClasspathIndex {

	// URL EXAMPLE - Project in IDE on classpath:
	// toString: file:/C:/git-dir/res-in-ws/classes/META-INF/classpath-index.txt
	// getPath : /C:/git-dir/res-in-ws/classes/META-INF/classpath-index.txt
	//
	// URL EXAMPLE - JAR on classpath:
	// toString: jar:file:/C:/maven-repo/res-on-cp/1.0/res-on-cp-1.0.jar!/META-INF/classpath-index.txt
	// getPath : file:/C:/maven-repo/res-on-cp/1.0/res-on-cp-1.0.jar!/META-INF/classpath-index.txt
	private static final String INDEX_FILE_NAME = "META-INF/classpath-index.txt";
	private static final String ORIGIN_FILE_NAME = "META-INF/classpath-origin.properties";

	private static final Logger log = Logger.getLogger(ClasspathIndex.class);

	private final ClassLoader classLoader;
	private final Path filesystemRoot;

	private final Lazy<List<ClasspathEntry>> lazyIndex = new Lazy<List<ClasspathEntry>>(this::loadIndex);

	public ClasspathIndex() {
		this(ClasspathIndex.class.getClassLoader(), null);
	}

	public ClasspathIndex(ClassLoader classLoader) {
		this(classLoader, null);
	}

	/** Creates an index backed exclusively by an assembled filesystem mirror. */
	public ClasspathIndex(Path filesystemRoot) {
		this(null, requireFilesystemRoot(filesystemRoot));
	}

	private ClasspathIndex(ClassLoader classLoader, Path filesystemRoot) {
		this.classLoader = classLoader;
		this.filesystemRoot = filesystemRoot;
	}

	public List<ClasspathEntry> all() {
		return Collections.unmodifiableList(lazyIndex.get());
	}

	public List<ClasspathEntry> forPrefix(String prefix) {
		List<ClasspathEntry> all = lazyIndex.get();

		int l = findBoundary(all, prefix, true);
		if (l == -1)
			return Collections.emptyList();

		int h = findBoundary(all, prefix, false);

		return Collections.unmodifiableList(all.subList(l, h + 1));
	}

	private int findBoundary(List<ClasspathEntry> all, String prefix, boolean low) {
		int l = 0;
		int h = all.size() - 1;

		int resultCandidate = -1;

		while (l <= h) {
			int m = (l + h) / 2;
			ClasspathEntry e = all.get(m);

			if (e.path.startsWith(prefix)) {
				resultCandidate = m;
				if (low)
					h = m - 1;
				else
					l = m + 1;
				continue;
			}

			int cmp = e.path.compareTo(prefix);
			if (cmp <= 0) // prefix >= e.path
				l = m + 1;
			else
				h = m - 1;
		}

		return resultCandidate;
	}

	// #################################################
	// ## . . . . . . . Loading Entries . . . . . . . ##
	// #################################################

	private List<ClasspathEntry> loadIndex() {
		List<ClasspathEntry> entries = newList();

		if (filesystemRoot != null)
			loadFilesystemIndex(entries);
		else
			loadClasspathIndex(entries);

		entries.sort((e1, e2) -> e1.path.compareTo(e2.path));

		return entries;
	}

	private void loadClasspathIndex(List<ClasspathEntry> entries) {
		try {
			Enumeration<URL> resources = classLoader.getResources(INDEX_FILE_NAME);
			while (resources.hasMoreElements())
				addEntriesFromIndexFile(resources.nextElement(), entries);

		} catch (IOException e) {
			throw new UncheckedIOException("Error while getting Resources: " + INDEX_FILE_NAME, e);
		}
	}

	private void addEntriesFromIndexFile(URL indexFileUrl, List<ClasspathEntry> entries) {
		String jarUrlPath = artifactPrefix(indexFileUrl);
		if (jarUrlPath == null) {
			log.warn("URL for indexFile does not end with [" + INDEX_FILE_NAME + "]: " + indexFileUrl);
			return;
		}

		try (var reader = new BufferedReader(new InputStreamReader(indexFileUrl.openStream(), StandardCharsets.UTF_8))) {
			String line;
			int lineNum = -1;
			while ((line = reader.readLine()) != null) {
				lineNum++;

				line = line.trim();
				if (line.isEmpty() || line.startsWith("#"))
					continue;

				// From now on we consider line to be a path within given jar / artifact
				Enumeration<URL> files = classLoader.getResources(line);
				if (!files.hasMoreElements()) {
					log.warn("File [" + line + "] referenced on line # " + lineNum + " not found. Index file: " + indexFileUrl);
					continue;
				}

				boolean found = false;
				while (files.hasMoreElements()) {
					URL fileUrl = files.nextElement();
					if (fileUrl.getPath().startsWith(jarUrlPath)) {
						found = true;
						entries.add(new ClasspathEntry(line, fileUrl, artifactOrigin(indexFileUrl)));
						break;
					}
				}

				if (!found)
					log.warn("File [" + line + "] referenced on line # " + lineNum + " not found in that same artifact. Index file: " + indexFileUrl);
			}

		} catch (IOException e) {
			throw new UncheckedIOException("Error while reading index file: " + indexFileUrl, e);
		}
	}

	private void loadFilesystemIndex(List<ClasspathEntry> entries) {
		if (!Files.isDirectory(filesystemRoot))
			throw new IllegalStateException("Classpath resource mirror does not exist: " + filesystemRoot);

		try (var children = Files.list(filesystemRoot)) {
			for (Path artifactRoot : children.filter(Files::isDirectory).sorted().toList()) {
				Path index = artifactRoot.resolve(INDEX_FILE_NAME);
				if (Files.isRegularFile(index))
					addEntriesFromFilesystemIndex(artifactRoot, index, entries);
			}
		} catch (IOException e) {
			throw new UncheckedIOException("Error while inspecting classpath resource mirror: " + filesystemRoot, e);
		}
	}

	private void addEntriesFromFilesystemIndex(Path artifactRoot, Path index, List<ClasspathEntry> entries) {
		String origin = filesystemOrigin(artifactRoot);
		try (var reader = Files.newBufferedReader(index, StandardCharsets.UTF_8)) {
			String line;
			int lineNum = -1;
			while ((line = reader.readLine()) != null) {
				lineNum++;
				line = line.trim();
				if (line.isEmpty() || line.startsWith("#"))
					continue;

				Path resource = artifactRoot.resolve(line).normalize();
				if (!resource.startsWith(artifactRoot.normalize()) || !Files.isRegularFile(resource)) {
					log.warn("File [" + line + "] referenced on line # " + lineNum
							+ " not found in filesystem artifact mirror: " + index);
					continue;
				}
				entries.add(new ClasspathEntry(line, resource.toUri().toURL(), origin));
			}
		} catch (IOException e) {
			throw new UncheckedIOException("Error while reading filesystem classpath index: " + index, e);
		}
	}

	private String filesystemOrigin(Path artifactRoot) {
		Path originFile = artifactRoot.resolve(ORIGIN_FILE_NAME);
		if (Files.isRegularFile(originFile)) {
			Properties properties = new Properties();
			try (var in = Files.newInputStream(originFile)) {
				properties.load(in);
				String artifactId = properties.getProperty("artifactId");
				if (artifactId != null && !artifactId.isBlank())
					return artifactId;
			} catch (IOException e) {
				throw new UncheckedIOException("Error while reading classpath resource origin: " + originFile, e);
			}
		}
		return artifactRoot.getFileName().toString();
	}

	private String artifactOrigin(URL indexFileUrl) {
		String value = indexFileUrl.toString();
		int jarEnd = value.indexOf(".jar!/");
		if (jarEnd >= 0) {
			int slash = value.lastIndexOf('/', jarEnd);
			String artifactWithVersion = value.substring(slash + 1, jarEnd);
			int versionSeparator = artifactWithVersion.lastIndexOf('-');
			return versionSeparator > 0 ? artifactWithVersion.substring(0, versionSeparator) : artifactWithVersion;
		}
		return "";
	}

	private String artifactPrefix(URL indexFileUrl) {
		String path = indexFileUrl.getPath();
		if (!path.endsWith(INDEX_FILE_NAME))
			return null;
		return path.substring(0, path.length() - INDEX_FILE_NAME.length());
	}

	private static Path requireFilesystemRoot(Path path) {
		if (path == null)
			throw new NullPointerException("filesystemRoot must not be null");
		return path;
	}
}
