// ============================================================================
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
// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
// ============================================================================
package com.braintribe.utils.stream;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.braintribe.logging.Logger;
import com.braintribe.model.generic.session.InputStreamProvider;
import com.braintribe.utils.FileTools;
import com.braintribe.utils.IOTools;

public class ZippingInputStreamProvider implements InputStreamProvider {

	private static Logger logger = Logger.getLogger(ZippingInputStreamProvider.class);
	
	private Map<String,File> files;
	private String name;
	private boolean deleteSourceFilesAfterZipping;
	private int bufferSize = 0xffff;
	private String folderName = null;

	public ZippingInputStreamProvider(String name, Collection<File> files, boolean deleteSourceFilesAfterZipping) {
		this.name = name;
		this.deleteSourceFilesAfterZipping = deleteSourceFilesAfterZipping;
		this.files = new LinkedHashMap<>();
		for (File f : files) {
			this.files.put(f.getName(), f);
		}
	}
	public ZippingInputStreamProvider(String name, Map<String,File> files, boolean deleteSourceFilesAfterZipping) {
		this.name = name;
		this.deleteSourceFilesAfterZipping = deleteSourceFilesAfterZipping;
		this.files = files;
	}


	@Override
	public InputStream openInputStream() throws IOException {

		try {
			File tempFile = File.createTempFile(name, ".zip");
			FileTools.deleteFileWhenOrphaned(tempFile);

			try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(tempFile))) {

				for (Map.Entry<String,File> entry : files.entrySet()) {
					String name = entry.getKey();
					File f = entry.getValue();

					try (FileInputStream in = new FileInputStream(f)) {
						String entryName = folderName != null ? folderName+"/"+name : name;
						out.putNextEntry(new ZipEntry(entryName));
						IOTools.pump(in, out, 0xffff);
					} finally {
						out.closeEntry();
					}
				}

				out.finish();
			}

			DeleteOnCloseFileInputStream docInputStream = new DeleteOnCloseFileInputStream(tempFile); 
			if (bufferSize > 0) {
				return new BufferedInputStream(docInputStream, bufferSize);
			} else {
				return docInputStream;
			}
		} finally {
			if (deleteSourceFilesAfterZipping) {
				for (File f : files.values()) {
					try {
						f.delete();
					} catch(Exception e) {
						logger.debug("Could not delete file "+f.getAbsolutePath(), e);
					}
				}
			}
		}
	}
	
	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

}
