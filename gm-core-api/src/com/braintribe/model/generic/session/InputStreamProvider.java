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
package com.braintribe.model.generic.session;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import com.braintribe.model.generic.GmCoreApiInteropNamespaces;

import jsinterop.annotations.JsType;

@JsType(namespace = GmCoreApiInteropNamespaces.session)
public interface InputStreamProvider {
	InputStream openInputStream() throws IOException;

	static InputStreamProvider fromStringUtf8(String s) {
		return fromBytes(s.getBytes(StandardCharsets.UTF_8)); 
	}

	static InputStreamProvider fromBytes(byte[] bytes) {
		return () -> new ByteArrayInputStream(bytes);
	}
	
}
