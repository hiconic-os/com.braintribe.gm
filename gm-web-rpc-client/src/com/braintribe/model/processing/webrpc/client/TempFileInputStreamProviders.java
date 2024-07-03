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
package com.braintribe.model.processing.webrpc.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.braintribe.model.generic.session.DuplexStreamProvider;
import com.braintribe.utils.FileTools;
import com.braintribe.utils.stream.BasicDelegateInputStream;
import com.braintribe.utils.stream.BasicDelegateOutputStream;

public class TempFileInputStreamProviders {

    public static DuplexStreamProvider create(String name) throws IOException {
    	
    	File tempFile = File.createTempFile("TempFileInputStreamProvider", name);
        TempFileStreamProvider provider = new TempFileStreamProvider(tempFile);
        FileTools.deleteFileWhenOrphaned(tempFile);
        return provider;
    }
    
    private static class TempFileStreamProvider implements DuplexStreamProvider {
    	private File file;
    	
    	public TempFileStreamProvider(File file) {
			super();
			this.file = file;
		}

		@Override
    	public InputStream openInputStream() throws IOException {
			// us an anonymous override in order to keep a back reference to the provider
    		return new BasicDelegateInputStream(new FileInputStream(file)) {
    			// Need to hold a reference to the file so that it won't get deleted while the stream is still in use (see FileTools.deleteFileWhenOrphaned)
    			@SuppressWarnings("unused")
				private File streamFile = file;
    		};
    	}
		
		@Override
		public OutputStream openOutputStream() throws IOException {
			// us an anonymous override in order to keep a back reference to the provider
			return new BasicDelegateOutputStream(new FileOutputStream(file)) {
    			// Need to hold a reference to the file so that it won't get deleted while the stream is still in use (see FileTools.deleteFileWhenOrphaned)
    			@SuppressWarnings("unused")
    			private File streamFile = file;
			};
		}
    }
}
