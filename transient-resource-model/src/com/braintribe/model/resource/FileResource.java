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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface FileResource extends Resource {

	EntityType<FileResource> T = EntityTypes.T(FileResource.class);

	String getPath();
	void setPath(String path);

	@Override
	default InputStream openStream() {
		String path = getPath();

		if (path == null || path.isEmpty()) {
			throw new IllegalStateException("path is not set");
		}

		Path p = Paths.get(path);

		InputStream stream;
		try {
			stream = Files.newInputStream(p);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}

		return stream;
	}
	
	@Override
	default void writeToStream(OutputStream outputStream) {
		byte buffer[] = new byte[64*1024];
		try (InputStream in = openStream()) {
			int read;

			while ((read = in.read(buffer)) != -1) {
				outputStream.write(buffer, 0, read);
			}

		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

}
