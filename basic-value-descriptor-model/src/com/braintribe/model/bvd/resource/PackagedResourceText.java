// ============================================================================
// Licensed under the Apache License, Version 2.0
// ============================================================================
package com.braintribe.model.bvd.resource;

import com.braintribe.model.generic.annotation.Initializer;
import com.braintribe.model.generic.annotation.meta.PositionalArguments;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.value.type.StringDescriptor;

/**
 * The text content of a file packaged in a classpath artifact, written as <code>${packagedResourceText('./notes.txt')}</code>.
 * <p>
 * This is the short form of <code>${resourceText(packagedResource('./notes.txt'))}</code>, for the common case. Use {@link ResourceText} when the
 * data comes from anywhere else.
 * <p>
 * The path follows the same rules as {@link PackagedSource}.
 */
@PositionalArguments({ "path", "artifact", "encoding" })
public interface PackagedResourceText extends StringDescriptor {

	EntityType<PackagedResourceText> T = EntityTypes.T(PackagedResourceText.class);

	String path = "path";
	String artifact = "artifact";
	String encoding = "encoding";

	/** {@code ./} and {@code ../} are relative to the document that holds this expression, every other path is artifact root relative. */
	String getPath();
	void setPath(String path);

	/** Optional in an expression, and completed from the artifact that owns the document. */
	String getArtifact();
	void setArtifact(String artifact);

	@Initializer("'UTF-8'")
	String getEncoding();
	void setEncoding(String encoding);
}
