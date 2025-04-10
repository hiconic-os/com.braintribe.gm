// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ============================================================================
package com.braintribe.model.resource;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import com.braintribe.model.generic.StandardStringIdentifiable;
import com.braintribe.model.generic.annotation.ForwardDeclaration;
import com.braintribe.model.generic.annotation.SelectiveInformation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.session.GmSession;
import com.braintribe.model.generic.session.InputStreamProvider;
import com.braintribe.model.generic.session.exception.GmSessionRuntimeException;
import com.braintribe.model.resource.api.HasResourceReadAccess;
import com.braintribe.model.resource.api.ResourceReadAccess;
import com.braintribe.model.resource.source.ResourceSource;
import com.braintribe.model.resource.source.StreamableSource;
import com.braintribe.model.resource.source.TransientSource;
import com.braintribe.model.resource.specification.ResourceSpecification;

/**
 * Resource represents binary data.
 * <p>
 * It is one of the most fundamental entities of our modeling framework (GM).
 * 
 * <h2>Payload</h2>
 * 
 * The actual payload (i.e. binary data) is referenced by an indirection via the property {@link #resourceSource}, which can be backed e.g. by a file
 * on the file system, by a blob in a DB, or say by a String in memory.
 * <p>
 * The data can be read directly via {@link #openStream()} or written to an output stream via {@link #writeToStream(OutputStream)}. Both these methods
 * delegate to the referenced {@link ResourceSource}.
 * 
 * <h2>Meta data</h2>
 * 
 * Properties other than {@link #resourceSource} describe metadata about the actual content, e.g.: <code>mimeType</code>, <code>md5</code> and others.
 * <p>
 * Custom metadata for certain types of resources can be configured with a {@link #getSpecification() resource specification}.
 * 
 * <h2>Creating a resource</h2>
 * 
 * See {@link #createTransient(InputStreamProvider)}
 * 
 */
@ForwardDeclaration("com.braintribe.gm:resource-model")
@SelectiveInformation("${name}")
public interface Resource extends StandardStringIdentifiable {

	final EntityType<Resource> T = EntityTypes.T(Resource.class);

	final String mimeType = "mimeType";
	final String md5 = "md5";
	final String fileSize = "fileSize";
	final String tags = "tags";
	final String resourceSource = "resourceSource";
	final String name = "name";
	final String created = "created";
	final String creator = "creator";
	final String specification = "specification";

	String getMimeType();
	void setMimeType(String mimeType);

	String getMd5();
	void setMd5(String md5);

	Long getFileSize();
	void setFileSize(Long fileSize);

	Set<String> getTags();
	void setTags(Set<String> tags);

	ResourceSource getResourceSource();
	void setResourceSource(ResourceSource resourceSource);

	String getName();
	void setName(String name);

	Date getCreated();
	void setCreated(Date created);

	void setCreator(String creator);
	String getCreator();

	/**
	 * Additional metadata specific for concrete type of resources.
	 * <p>
	 * This could be dimensions in case this resource denotes an image or number of pages if it is a PDF (there are
	 * <code>RasterImageSpecification</code>, <code>VectorImageSpecification</code> and <code>PdfSpecification</code>).
	 * <p>
	 * 
	 * 
	 * It is intended for this be extended, i.e. anyone can derive their own {@link ResourceSpecification} for their
	 */
	ResourceSpecification getSpecification();
	void setSpecification(ResourceSpecification specification);

	default boolean isTransient() {
		return getResourceSource() instanceof TransientSource;
	}

	default boolean isStreamable() {
		return getResourceSource() instanceof StreamableSource;
	}

	default InputStream openStream() {
		ResourceSource resSrc = getResourceSource();

		if (resSrc instanceof StreamableSource) {
			StreamableSource transientSource = (StreamableSource) resSrc;
			return transientSource.openStream();
		}

		GmSession session = session();
		if (!(session instanceof HasResourceReadAccess))
			throw new GmSessionRuntimeException("Cannot open resource stream as entity is not attached to a session which supports streaming.");

		ResourceReadAccess resources = ((HasResourceReadAccess) session).resources();

		try {
			return resources.openStream(this);
		} catch (IOException e) {
			throw new RuntimeException("Error while opening stream" + (e.getMessage() != null ? ": " + e.getMessage() : ""), e);
		}
	}

	default void writeToStream(OutputStream outputStream) {
		ResourceSource resSrc = getResourceSource();
		if (resSrc instanceof StreamableSource) {
			StreamableSource transientSource = (StreamableSource) getResourceSource();
			transientSource.writeToStream(outputStream);

		} else {
			GmSession session = session();

			if (!(session instanceof HasResourceReadAccess)) {
				throw new GmSessionRuntimeException(
						"Cannot write to resource stream as entity is not attached to a session which supports streaming.");
			}

			ResourceReadAccess resources = ((HasResourceReadAccess) session).resources();
			try {
				resources.writeToStream(this, outputStream);
			} catch (IOException e) {
				throw new RuntimeException("Error while writing to stream" + (e.getMessage() != null ? ": " + e.getMessage() : ""), e);
			}
		}
	}

	// ###############################################
	// #. . . . Creating Transient Resource . . . . ##
	// ###############################################

	/**
	 * Creates a new {@link Resource} backed by a new {@link TransientSource} for given {@link InputStreamProvider}.
	 * 
	 * @param inputStreamProvider
	 *            {@link InputStreamProvider} to be used for the new {@link TransientSource}, which must be restreameble: Any time the
	 *            {@link InputStreamProvider#openInputStream()} method is called, a new {@link InputStream} for the exact same binary data must be
	 *            returned
	 */
	static Resource createTransient(InputStreamProvider inputStreamProvider) {
		Resource resource = Resource.T.create();
		resource.assignTransientSource(inputStreamProvider);
		return resource;
	}

	/**
	 * Creates a new {@link TransientSource} with given {@link InputStreamProvider} and assigns it to this instance as its {@link #getResourceSource()
	 * resource source}.
	 * 
	 * @param inputStreamProvider
	 *            {@link InputStreamProvider} to be used for the new {@link TransientSource}, which must be re-streameble, i.e. any time the
	 *            {@link InputStreamProvider#openInputStream()} method is called, a new {@link InputStream} for the exact same binary data must be
	 *            returned
	 */
	default void assignTransientSource(InputStreamProvider inputStreamProvider) {
		requireNonNull(inputStreamProvider, "Cannot create transient resource with null inputStreamProvider.");

		TransientSource transientSource = TransientSource.T.create();
		transientSource.setGlobalId(UUID.randomUUID().toString());
		transientSource.setInputStreamProvider(inputStreamProvider);
		transientSource.setOwner(this);
		setResourceSource(transientSource);
	}

}
