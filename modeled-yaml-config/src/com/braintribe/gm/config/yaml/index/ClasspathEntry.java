package com.braintribe.gm.config.yaml.index;

import java.net.URL;

import com.braintribe.utils.lcd.NullSafe;

/**
 * @author peter.gazdik
 */
public class ClasspathEntry {

	public final String path;
	public final URL url;
	/** The groupId of the artifact the resource belongs to, or an empty string if it is not known. */
	public final String groupId;
	/** The artifactId of the artifact the resource belongs to, or an empty string if it is not known. */
	public final String artifactId;

	public ClasspathEntry(String path, URL url) {
		this(path, url, "");
	}

	public ClasspathEntry(String path, URL url, String artifactId) {
		this(path, url, "", artifactId);
	}

	public ClasspathEntry(String path, URL url, String groupId, String artifactId) {
		this.path = NullSafe.nonNull(path, "path");
		this.url = NullSafe.nonNull(url, "url");
		this.groupId = NullSafe.nonNull(groupId, "groupId");
		this.artifactId = NullSafe.nonNull(artifactId, "artifactId");
	}

}
