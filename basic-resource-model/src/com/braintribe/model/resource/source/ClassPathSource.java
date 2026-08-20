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
package com.braintribe.model.resource.source;

import java.io.FileNotFoundException;
import java.io.InputStream;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.session.InputStreamProvider;
import com.braintribe.model.resource.Resource;

/**
 * A {@link ResourceSource} that represents data on the classpath, given by its {@link #getPath() path}.
 * <p>
 * The data is read with the {@link ClassLoader} of this instance, thus a {@link Resource} with such a source can be streamed directly with
 * {@link Resource#openStream()}.
 */
public interface ClassPathSource extends ResourceSource, StreamableSource {

	EntityType<ClassPathSource> T = EntityTypes.T(ClassPathSource.class);

	String path = "path";

	/** 
	 * Path of the data on the classpath.
	 * <p>
	 * Leading slash is ignored, 'my/data.png' and '/my/data.png' mean the same.
	 */
	String getPath();
	void setPath(String path);

	@Override
	default InputStreamProvider inputStreamProvider() {
		return this::openClassPathStream;
	}

	default InputStream openClassPathStream() throws FileNotFoundException {
		String configuredPath = getPath();
		if (configuredPath == null)
			throw new FileNotFoundException("No path is set on this " + T.getShortName() + ".");

		String normalizedPath = configuredPath.startsWith("/") ? configuredPath.substring(1) : configuredPath;

		ClassLoader classLoader = getClass().getClassLoader();

		InputStream result = classLoader.getResourceAsStream(normalizedPath);
		if (result == null)
			throw new FileNotFoundException("There is no such data on the classpath: " + normalizedPath + ". ClassLoader: " + classLoader);

		return result;
	}

}
