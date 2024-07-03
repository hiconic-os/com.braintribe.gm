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

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;

import com.braintribe.logging.Logger;

public class ResponseEntityInputStream extends InputStream {

	private static final Logger logger = Logger.getLogger(ResponseEntityInputStream.class);

	private final HttpResponse response;
	private final InputStream content;

	public ResponseEntityInputStream(HttpResponse response) throws UnsupportedOperationException, IOException {
		this.response = response;
		content = response.getEntity().getContent();
	}


	@Override
	public void close() throws IOException {
		try {
			content.close();
		} finally {
			if (response instanceof CloseableHttpResponse) {
				CloseableHttpResponse cr = (CloseableHttpResponse) response;
				try {
					cr.close();
				} catch(Exception e) {
					logger.error("Could not close HTTP response.", e);
				}
			} else {
				logger.trace(() -> "Http response object is of type "+response.getClass());
			}
		}
	}


	// Delegate methods

	@Override
	public int read() throws IOException {
		return content.read();
	}

	@Override
	public int hashCode() {
		return content.hashCode();
	}

	@Override
	public int read(byte[] b) throws IOException {
		return content.read(b);
	}

	@Override
	public boolean equals(Object obj) {
		return content.equals(obj);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return content.read(b, off, len);
	}

	@Override
	public long skip(long n) throws IOException {
		return content.skip(n);
	}

	@Override
	public String toString() {
		return content.toString();
	}

	@Override
	public int available() throws IOException {
		return content.available();
	}

	@Override
	public void mark(int readlimit) {
		content.mark(readlimit);
	}

	@Override
	public void reset() throws IOException {
		content.reset();
	}

	@Override
	public boolean markSupported() {
		return content.markSupported();
	}
}
