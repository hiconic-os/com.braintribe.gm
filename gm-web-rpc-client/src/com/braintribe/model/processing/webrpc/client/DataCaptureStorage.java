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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Consumer;

import com.braintribe.logging.Logger;
import com.braintribe.model.generic.session.InputStreamProvider;
import com.braintribe.utils.stream.api.StreamPipe;
import com.braintribe.utils.stream.api.StreamPipeFactory;

public class DataCaptureStorage implements AutoCloseable {
	private final static Logger logger = Logger.getLogger(DataCaptureStorage.class);
	
	private Consumer<InputStreamProvider> receiver;
	private StreamPipe pipe;
	private StreamPipeFactory streamPipeFactory;

	private boolean notifyReceiver;

	private String callId;

	public DataCaptureStorage(String callId, StreamPipeFactory streamPipeFactory, Consumer<InputStreamProvider> receiver) {
		this.callId = callId;
		this.streamPipeFactory = streamPipeFactory;
		this.receiver = receiver;
	}
	
	public void activateNotification() {
		this.notifyReceiver = true;
	}
	
	public InputStream wrap(InputStream in) {
		if (receiver == null)
			return in;
		
		pipe = streamPipeFactory.newPipe("multipart-capture-" + callId); 
		
		return new CapturingInputStream(in, pipe.openOutputStream());
	}
	
	@Override
	public void close() {
		if (receiver != null && notifyReceiver) {
			receiver.accept(pipe::openInputStream);
		}
	}
}

class CapturingInputStream extends InputStream {
	private InputStream delegate;
	private OutputStream out;
	
	public CapturingInputStream(InputStream delegate, OutputStream out) {
		super();
		this.delegate = delegate;
		this.out = out;
	}

	@Override
	public int read() throws IOException {
		int res = delegate.read();
		
		if (res != -1)
			out.write(res);
		else
			out.close();
			
		return res;
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int res = delegate.read(b, off, len);
		
		if (res != -1)
			out.write(b, off, res);
		else
			out.close();
		
		return res;
	}
}