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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.braintribe.logging.Logger;
import com.braintribe.model.io.metamodel.MetaModelSourceDescriptor;

public class SourceStreamSerializer implements SourceSerializer {
	private static Logger log = Logger.getLogger(SourceStreamSerializer.class);
	private final ZipOutputStream outputStream;
	private final Map<String,String> sourceMap = new HashMap<String, String>();
	
	public SourceStreamSerializer( ZipOutputStream outputStream) {
		this.outputStream = outputStream;
	}

	@Override
	public Map<String, String> getSourceMap() {
		return sourceMap;
	}



	@Override
	public void writeSourceFile(MetaModelSourceDescriptor sourceDescriptor) {
		// add to map
		sourceMap.put( sourceDescriptor.sourceRelativePath, sourceDescriptor.sourceCode);
		try {
			// create zip entry with the relative path as name
			ZipEntry entry = new ZipEntry( sourceDescriptor.sourceRelativePath);
			outputStream.putNextEntry(entry);
			// write the source code 
			byte [] bytes = sourceDescriptor.sourceCode.getBytes("UTF-8");
			outputStream.write( bytes, 0, bytes.length);
			outputStream.closeEntry();
		} catch (IOException e) {
			String msg="cannot write zip entry for source [" + sourceDescriptor.sourceRelativePath + "]";
			log.error( msg, e);
			throw new UncheckedIOException(msg, e);
		}

	}

}
