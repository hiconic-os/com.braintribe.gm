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
package com.braintribe.model.processing.webrpc.server.multipart;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.function.Supplier;

import com.braintribe.logging.Logger;
import com.braintribe.model.generic.session.InputStreamProvider;

public class TemporaryFileInputStreamSupplier implements Supplier<InputStream>, InputStreamProvider, Closeable {

	private static final Logger log = Logger.getLogger(TemporaryFileInputStreamSupplier.class);
	private Path path;

	public TemporaryFileInputStreamSupplier(File file) {
		this(Objects.requireNonNull(file, "file must not be null").toPath());
	}

	public TemporaryFileInputStreamSupplier(Path path) {
		this.path = Objects.requireNonNull(path, "path must not be null");
	}

	@Override
	public InputStream openInputStream() {
		return get();
	}

	@Override
	public InputStream get() {
		try {
			return Files.newInputStream(path, StandardOpenOption.READ);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public void close() throws IOException {
		if (path != null) {
			try {
				Files.deleteIfExists(path);
				log.info("Deleted temp file " + path);
			} catch (Throwable t) {
				log.error("Failed to delete temmporary file at \"" + path + "\" due to: " + t, t);
			}
		}
	}

}
