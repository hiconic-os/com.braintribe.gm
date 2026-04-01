package com.braintribe.gm.config.yaml.index;

import java.net.URL;

import com.braintribe.utils.lcd.NullSafe;

/**
 * @author peter.gazdik
 */
public class ClasspathEntry {

	public final String path;
	public final URL url;

	public ClasspathEntry(String path, URL url) {
		this.path = NullSafe.nonNull(path, "path");
		this.url = NullSafe.nonNull(url, "url");
	}

}
