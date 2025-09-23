// ============================================================================
package com.braintribe.model.resource.api;

import java.io.File;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Provides methods for obtaining different representations of a resource reference, including ways of obtaining the
 * resource contents.
 */
public interface ResourceHandle {

	/**
	 * Returns the resolved path as {@link URL}.
	 * 
	 * @throws UncheckedIOException
	 *             If the resolved path cannot be represented as {@link URL}.
	 */
	URL asUrl() throws UncheckedIOException;

	/**
	 * Returns the resolved path as {@link Path}.
	 * 
	 * @throws UncheckedIOException
	 *             If the resolved path cannot be represented as {@link Path}.
	 */
	Path asPath() throws UncheckedIOException;

	/**
	 * Returns the resolved path as {@link File}.
	 * 
	 * @throws UncheckedIOException
	 *             If the resolved path cannot be represented as {@link File}.
	 */
	File asFile() throws UncheckedIOException;

	/**
	 * Returns the resource contents as String.
	 * 
	 * @param encoding
	 *            The encoding used for reading the resource contents.
	 * @throws UncheckedIOException
	 *             In case of IOException(s) while reading the resource contents.
	 */
	String asString(String encoding) throws UncheckedIOException;

	/**
	 * Returns an {@link InputStream} for reading the resource contents.
	 * 
	 * @throws UncheckedIOException
	 *             In case of IOException(s) while obtaining the {@link InputStream}.
	 */
	InputStream asStream() throws UncheckedIOException;

	/**
	 * Returns a {@link Properties} instance as loaded from the resource.
	 * <p>
	 * The resource compatibility for this operation is described in the {@link Properties#load(InputStream)} method
	 * documentation.
	 * 
	 * @throws UncheckedIOException
	 *             In case of IOException(s) while reading the resource or loading it as a {@link Properties} object.
	 */
	Properties asProperties() throws UncheckedIOException;

}
