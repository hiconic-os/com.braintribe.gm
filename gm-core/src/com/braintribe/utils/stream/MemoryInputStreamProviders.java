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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import com.braintribe.model.generic.session.InputStreamProvider;

public class MemoryInputStreamProviders {

	public static InputStreamProvider from(String content) {
		if (content == null) {
			throw new NullPointerException("The content must not be null.");
		}
		try {
			return new ByteArrayInputStreamProvider(content.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("For reasons unknown the content "+content+" could not be converted to a UTF-8 byte array.", e);
		}
	}

	public static InputStreamProvider from(byte[] content) {
		if (content == null) {
			throw new NullPointerException("The content must not be null.");
		}
		return new ByteArrayInputStreamProvider(content);
	}

	private static class ByteArrayInputStreamProvider implements InputStreamProvider {
		private final byte[] content;
		
		public ByteArrayInputStreamProvider(byte[] content) {
			this.content = content;
		}
		
		@Override
		public InputStream openInputStream() throws IOException {
			return new ByteArrayInputStream(content);
		}
	}
	
}
