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
package com.braintribe.utils.stream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.braintribe.logging.Logger;
import com.braintribe.model.generic.session.InputStreamProvider;
import com.braintribe.utils.IOTools;
import com.braintribe.utils.StringTools;
import com.braintribe.utils.date.NanoClock;
import com.braintribe.utils.stream.api.StreamPipe;
import com.braintribe.utils.stream.api.StreamPipeFactory;

public class PipeBackedZippingInputStreamProvider implements InputStreamProvider {

	private static Logger logger = Logger.getLogger(PipeBackedZippingInputStreamProvider.class);

	private Map<String, File> files;
	private boolean deleteSourceFilesAfterZipping;
	private String folderName = null;
	private StreamPipe pipe;

	public PipeBackedZippingInputStreamProvider(StreamPipeFactory streamPipeFactory, String name, Collection<File> files,
			boolean deleteSourceFilesAfterZipping) throws IOException {
		this.deleteSourceFilesAfterZipping = deleteSourceFilesAfterZipping;
		this.files = new LinkedHashMap<>();
		for (File f : files) {
			this.files.put(f.getName(), f);
		}
		this.pipe = streamPipeFactory.newPipe(name);
		this.doZip();
	}
	public PipeBackedZippingInputStreamProvider(StreamPipeFactory streamPipeFactory, String name, Map<String, File> files,
			boolean deleteSourceFilesAfterZipping) throws IOException {
		this.deleteSourceFilesAfterZipping = deleteSourceFilesAfterZipping;
		this.files = files;
		this.pipe = streamPipeFactory.newPipe(name);
		this.doZip();
	}

	private void doZip() throws IOException {
		Instant start = NanoClock.INSTANCE.instant();
		try {
			try (ZipOutputStream out = new ZipOutputStream(pipe.openOutputStream())) {

				for (Map.Entry<String, File> entry : files.entrySet()) {
					String name = entry.getKey();
					File f = entry.getValue();

					try (FileInputStream in = new FileInputStream(f)) {
						String entryName = folderName != null ? folderName + "/" + name : name;
						out.putNextEntry(new ZipEntry(entryName));
						IOTools.transferBytes(in, out, IOTools.BUFFER_SUPPLIER_64K);
					} finally {
						out.closeEntry();
					}
				}

				out.finish();
			}
			logger.debug(() -> "Zipping " + this.files.size() + " files took: " + StringTools.prettyPrintDuration(start, true, ChronoUnit.MILLIS));
		} finally {
			if (deleteSourceFilesAfterZipping) {
				for (File f : files.values()) {
					try {
						f.delete();
					} catch (Exception e) {
						logger.debug("Could not delete file " + f.getAbsolutePath(), e);
					}
				}
				logger.debug(() -> "Zipping and deleting " + this.files.size() + " files took: "
						+ StringTools.prettyPrintDuration(start, true, ChronoUnit.MILLIS));
			}
		}
	}

	@Override
	public InputStream openInputStream() throws IOException {
		return new AutoCloseInputStream(pipe.openInputStream());
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

}
