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
package com.braintribe.model.processing.test.jar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 
 */
public class SourceJarReader {

	public static Map<String, String> readSources(ZipInputStream zis) throws IOException {
		Map<String, String> result = new HashMap<String, String>();

		ZipEntry ze;
		while ((ze = nextEntry(zis)) != null) {
			if (isJavaFile(ze)) {
				String name = ze.getName();
				String className = name.substring(0, name.length() - 5).replace("/", ".");

				StringBuilder sb = new StringBuilder();
				BufferedReader b = new BufferedReader(new InputStreamReader(zis));

				String s;
				while ((s = b.readLine()) != null) {
					sb.append(s);
					sb.append("\n");
				}

				result.put(className, sb.toString());
			}
		}

		return result;
	}

	private static boolean isJavaFile(ZipEntry ze) {
		return !ze.isDirectory() && ze.getName().endsWith(".java");
	}

	private static ZipEntry nextEntry(ZipInputStream zis) {
		try {
			return zis.getNextEntry();

		} catch (IOException e) {
			throw new RuntimeException("Some problem: " + e.getMessage(), e);
		}
	}
}
