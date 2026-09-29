package com.braintribe.gm.config.yaml.index;

import java.net.URL;

import com.braintribe.utils.lcd.NullSafe;

/**
 * @author peter.gazdik
 */
public class ClasspathEntry {

	public final String path;
	public final URL url;
	/** The artifactId of the artifact the resource belongs to, or an empty string if it is not known. */
	public final String artifactId;

	public ClasspathEntry(String path, URL url) {
		this(path, url, "");
	}

	public ClasspathEntry(String path, URL url, String artifactId) {
		this.path = NullSafe.nonNull(path, "path");
		this.url = NullSafe.nonNull(url, "url");
		this.artifactId = NullSafe.nonNull(artifactId, "artifactId");
	}

}
