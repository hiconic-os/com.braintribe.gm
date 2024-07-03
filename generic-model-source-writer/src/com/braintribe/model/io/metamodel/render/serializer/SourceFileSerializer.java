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
package com.braintribe.model.io.metamodel.render.serializer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import com.braintribe.logging.Logger;
import com.braintribe.model.io.metamodel.MetaModelSourceDescriptor;
import com.braintribe.utils.IOTools;

/**
 * Stores given source-files represented by {@link MetaModelSourceDescriptor} inside the {@link #outputDirectory} passed
 * through constructor.
 */
public class SourceFileSerializer implements SourceSerializer {
	private static final Logger log = Logger.getLogger(SourceFileSerializer.class);

	private final Map<String, String> sourceMap = new HashMap<String, String>();
	private final File outputDirectory;


	@Override
	public Map<String, String> getSourceMap() {
		return sourceMap;
	}


	public SourceFileSerializer(File outputDirectory) {
		this.outputDirectory = outputDirectory;
		
		if (outputDirectory.exists()) {
			if (!outputDirectory.isDirectory()) {
				String msg = "Source writing initialization failed as '" + outputDirectory.getAbsolutePath() + "' is not a valid directory.";
				log.error( msg, null);
				throw new RuntimeException( msg);
			}
		}

		
	}


	@Override
	public void writeSourceFile(MetaModelSourceDescriptor sourceDescriptor) {		
		sourceMap.put( sourceDescriptor.sourceRelativePath, sourceDescriptor.sourceCode);
		writeSourceHelper(sourceDescriptor);
	}

	private void writeSourceHelper(MetaModelSourceDescriptor sourceDescriptor) {
		File outputFile = getFileInExistingFolder(sourceDescriptor.sourceRelativePath);

		Writer writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8"));

			writer.write(sourceDescriptor.sourceCode);

		} catch (IOException e) {
			throw new UncheckedIOException(e);
		} finally {
			IOTools.closeCloseable(writer, log);
		}
	}

	private File getFileInExistingFolder(String relativeSourcePath) {
		File result = new File(outputDirectory, relativeSourcePath);

		File parentFile = result.getParentFile();
		if (parentFile.isDirectory() || parentFile.mkdirs()) {
			return result;
		}
		String msg = "Failed to create folders to store file [ " + result.getAbsolutePath() + "]";
		log.error( msg, null);
		throw new RuntimeException( msg);
	}

}
