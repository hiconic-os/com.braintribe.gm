// ============================================================================
// Licensed under the Apache License, Version 2.0
// ============================================================================
package com.braintribe.model.bvd.resource;

import com.braintribe.model.generic.annotation.Initializer;
import com.braintribe.model.generic.annotation.meta.Mandatory;
import com.braintribe.model.generic.annotation.meta.PositionalArguments;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.value.type.StringDescriptor;
import com.braintribe.model.resource.Resource;

/**
 * The text content of a {@link Resource}, written as <code>${resourceText(packagedResource('./notes.txt'))}</code>.
 * <p>
 * The Resource says where the data comes from, this descriptor only decodes it. Any expression that yields a Resource can therefore be used, and a
 * new kind of source needs no new text descriptor.
 * <p>
 * {@link PackagedResourceText} is the short form for the common case of a file in a classpath artifact.
 */
@PositionalArguments({ "resource", "encoding" })
public interface ResourceText extends StringDescriptor {

	EntityType<ResourceText> T = EntityTypes.T(ResourceText.class);

	String resource = "resource";
	String encoding = "encoding";

	/** The Resource to read. Usually the result of a nested expression, for example {@link PackagedResource}. */
	@Mandatory
	Resource getResource();
	void setResource(Resource resource);

	@Initializer("'UTF-8'")
	String getEncoding();
	void setEncoding(String encoding);
}
