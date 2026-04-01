package com.braintribe.gm.config.yaml.index;

import static com.braintribe.utils.lcd.CollectionTools2.newList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

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

	private static final Logger log = Logger.getLogger(ClasspathIndex.class);

	private final ClassLoader classLoader;

	private final Lazy<List<ClasspathEntry>> lazyIndex = new Lazy<List<ClasspathEntry>>(this::loadIndex);

	public ClasspathIndex() {
		this.classLoader = getClass().getClassLoader();
	}

	public ClasspathIndex(ClassLoader classLoader) {
		this.classLoader = classLoader;
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

		try {
			Enumeration<URL> resources = classLoader.getResources(INDEX_FILE_NAME);
			while (resources.hasMoreElements())
				addEntriesFromIndexFile(resources.nextElement(), entries);

		} catch (IOException e) {
			throw new UncheckedIOException("Error while getting Resources: " + INDEX_FILE_NAME, e);
		}

		entries.sort((e1, e2) -> e1.path.compareTo(e2.path));

		return entries;
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
						entries.add(new ClasspathEntry(line, fileUrl));
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

	private String artifactPrefix(URL indexFileUrl) {
		String path = indexFileUrl.getPath();
		if (!path.endsWith(INDEX_FILE_NAME))
			return null;
		return path.substring(0, path.length() - INDEX_FILE_NAME.length());
	}
}
