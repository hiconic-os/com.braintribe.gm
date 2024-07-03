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
package com.braintribe.model.io.metamodel.render;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;

/**
 * 
 */
public class SerializingUtils {

	public static void copySourceToDestination(File source, File destination) {
		destination = getFileInExistingFolder(destination);
		try {
			InputStream in = new FileInputStream(source);
			try {
				OutputStream out = new FileOutputStream(destination);

				try {
					copyInToOut(in, out);
				} finally {
					out.close();
				}
			} finally {
				in.close();
			}

		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private static File getFileInExistingFolder(File file) {
		File parentFile = file.getParentFile();
		if (parentFile.isDirectory() || parentFile.mkdirs()) {
			return file;
		}

		throw new IllegalArgumentException("Failed to create folders to store file: " + file.getAbsolutePath());
	}

	private static void copyInToOut(InputStream in, OutputStream out) throws IOException {
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
	}

}
