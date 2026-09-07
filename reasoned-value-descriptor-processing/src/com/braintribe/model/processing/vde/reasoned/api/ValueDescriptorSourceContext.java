// ============================================================================
// Licensed under the Apache License, Version 2.0
// ============================================================================
package com.braintribe.model.processing.vde.reasoned.api;

/**
 * Logical origin of a descriptor-bearing document. Experts may use it to complete relative references without
 * coupling themselves to a concrete marshaller or configuration loader.
 */
public final class ValueDescriptorSourceContext {

	private final String artifact;
	private final String path;

	public ValueDescriptorSourceContext(String artifact, String path) {
		this.artifact = artifact;
		this.path = path;
	}

	public String artifact() {
		return artifact;
	}

	public String path() {
		return path;
	}
}
