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
package com.braintribe.transport.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

import com.braintribe.utils.stream.CountingInputStream;
import com.braintribe.utils.stream.CountingOutputStream;

public class CountingHttpEntity implements HttpEntity {

	private HttpEntity delegate;
	
	private CountingInputStream countingInputStream;
	private long inputStreamCount = 0;
	private long outputStreamCount = 0;
	
	public CountingHttpEntity(HttpEntity delegate) {
		this.delegate = delegate;
	}
	
	public long getCount() {
		return inputStreamCount+outputStreamCount;
	}

	@Override
	public boolean isRepeatable() {
		return delegate.isRepeatable();
	}

	@Override
	public boolean isChunked() {
		return delegate.isChunked();
	}

	@Override
	public long getContentLength() {
		return delegate.getContentLength();
	}

	@Override
	public Header getContentType() {
		return delegate.getContentType();
	}

	@Override
	public Header getContentEncoding() {
		return delegate.getContentEncoding();
	}

	@Override
	public InputStream getContent() throws IOException, UnsupportedOperationException {
		InputStream delegateInputStream = delegate.getContent();
		if (delegateInputStream == null) {
			return null;
		}
		// Reset
		inputStreamCount = 0;
		countingInputStream = new CountingInputStream(delegateInputStream, false);
		return countingInputStream;
	}

	@Override
	public void writeTo(OutputStream outstream) throws IOException {
		CountingOutputStream countingOutputStream = new CountingOutputStream(outstream);
		try {
			delegate.writeTo(countingOutputStream);
		} finally {
			outputStreamCount = countingOutputStream.getCount();
		}
	}

	@Override
	public boolean isStreaming() {
		return delegate.isStreaming();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void consumeContent() throws IOException {
		delegate.consumeContent();
	}
	
}
