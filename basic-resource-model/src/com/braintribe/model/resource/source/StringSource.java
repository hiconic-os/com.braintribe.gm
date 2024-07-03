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
package com.braintribe.model.resource.source;

import java.io.ByteArrayInputStream;
import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;

import com.braintribe.model.generic.annotation.Initializer;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.session.InputStreamProvider;

/**
 * A {@link ResourceSource} that stores its data inside an internal String. It can be accessed via InputStream/OutputStream.
 * 
 */

public interface StringSource extends ResourceSource, StreamableSource {

	final EntityType<StringSource> T = EntityTypes.T(StringSource.class);

	String content = "content";

	String getContent();
	void setContent(String content);

	String encoding = "encoding";

	@Initializer("'UTF-8'")
	String getEncoding();
	void setEncoding(String encoding);

	@Override
	default InputStreamProvider inputStreamProvider() {
		return () -> new ByteArrayInputStream(toBytes());
	}

	default byte[] toBytes() {
		String contentData = getContent();
		if (contentData == null)
			return new byte[] {};
		String enc = getEncoding();
		if (enc == null)
			enc = "UTF-8";
		try {
			return contentData.getBytes(enc);
		} catch (UnsupportedEncodingException e) {
			throw new UncheckedIOException(e);
		}
	}
}
