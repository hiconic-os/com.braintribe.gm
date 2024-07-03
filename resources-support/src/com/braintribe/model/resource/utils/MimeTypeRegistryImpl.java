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
package com.braintribe.model.resource.utils;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import com.braintribe.model.resource.api.MimeTypeRegistry;
import com.braintribe.utils.collection.api.MultiMap;
import com.braintribe.utils.collection.impl.HashMultiMap;

public class MimeTypeRegistryImpl implements MimeTypeRegistry {

	private final MultiMap<String, String> mimeTypeToExtensions = new HashMultiMap<>(true);
	private final MultiMap<String, String> extensionToMimeTypes = new HashMultiMap<>(true);

	@Override
	public void registerMapping(String mimeType, String extension) {
		mimeTypeToExtensions.put(mimeType, extension);
		extensionToMimeTypes.put(extension, mimeType);
	}

	@Override
	public Collection<String> getExtensions(String mimeType) {
		return mimeTypeToExtensions.getAll(mimeType);
	}

	@Override
	public Optional<String> getExtension(String mimeType) {
		return mimeTypeToExtensions.getAll(mimeType).stream().findFirst();
	}

	@Override
	public Collection<String> getMimeTypes(String extension) {
		return extensionToMimeTypes.getAll(extension);

	}

	@Override
	public Optional<String> getMimeType(String extension) {
		return extensionToMimeTypes.getAll(extension).stream().findFirst();
	}

	@Override
	public Set<Entry<String, String>> getEntries() {
		return mimeTypeToExtensions.entrySet();
	}
}
