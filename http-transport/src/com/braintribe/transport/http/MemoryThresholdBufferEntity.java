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

import org.apache.http.entity.AbstractHttpEntity;

import com.braintribe.utils.IOTools;
import com.braintribe.utils.stream.MemoryThresholdBuffer;

public class MemoryThresholdBufferEntity extends AbstractHttpEntity {

	protected MemoryThresholdBuffer buffer;
	
	public MemoryThresholdBufferEntity(MemoryThresholdBuffer buffer) {
		this.buffer = buffer;
	}
	
	@Override
	public boolean isRepeatable() {
		return buffer.multipleReadsPossible();
	}

	@Override
	public long getContentLength() {
		return this.buffer.getLength();
	}

	@Override
	public InputStream getContent() throws IOException, IllegalStateException {
		return this.buffer.openInputStream(true);
	}

	@Override
	public void writeTo(OutputStream outstream) throws IOException {
		try (InputStream is = this.buffer.openInputStream(true)) {
			IOTools.pump(is, outstream);
		}
	}

	@Override
	public boolean isStreaming() {
		return true;
	}

}
