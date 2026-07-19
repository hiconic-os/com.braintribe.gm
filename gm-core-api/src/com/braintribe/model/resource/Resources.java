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
package com.braintribe.model.resource;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;

import com.braintribe.model.generic.session.InputStreamProvider;

public interface Resources {

	/**
	 * Creates a new transient {@link Resource} for given text using UTF-8 encoding.
	 * <p>
	 * 
	 * @see Resource#createTransient(InputStreamProvider)
	 */
	static Resource createTransient(String text) {
		return Resource.createTransient( //
				() -> new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)));
	}

	/**
	 * Creates a new transient {@link Resource} for given text and charset.
	 * <p>
	 * 
	 * @see Resource#createTransient(InputStreamProvider)
	 */
	static Resource createTransient(String text, String charsetName) {
		return Resource.createTransient( //
				() -> new ByteArrayInputStream(text.getBytes(charsetName)));
	}

	/**
	 * Creates a new transient {@link Resource} for given {@link File}.
	 * <p>
	 * 
	 * @see Resource#createTransient(InputStreamProvider)
	 */
	static Resource createTransient(File file) {
		return Resource.createTransient( //
				() -> new FileInputStream(file));
	}

	/** @see Resource#createTransient(InputStreamProvider) */
	static Resource createTransient(InputStreamProvider inputStreamProvider) {
		return Resource.createTransient(inputStreamProvider);
	}
}
