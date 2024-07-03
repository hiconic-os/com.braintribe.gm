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
package com.braintribe.codec.marshaller.api;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public interface MarshallerRegistry {

	/**
	 * Tries to find the marshaller registered for the given mimeType, this method supports wildcards.
	 * 
	 * e.g.: "application/*"
	 * 
	 * @param mimeType
	 *            the mimeType to query for, must not be {@code null}
	 * @return the marshaller for the given mimeType, or {@code null} if not found
	 */
	Marshaller getMarshaller(String mimeType);

	/**
	 * Tries to find the MarshallerRegistryEntry registered for the given mimeType, this method supports wildcards.
	 * 
	 * e.g.: "application/*"
	 * 
	 * @param mimeType
	 *            the mimeType to query for, must not be {@code null}
	 * @return the MarshallerRegistryEntry for the given mimeType, or {@code null} if not found
	 */
	MarshallerRegistryEntry getMarshallerRegistryEntry(String mimeType);

	/**
	 * Gets the supported mimeTypes for the given mimeType, this method supports wildcards.
	 * 
	 * e.g.: "application/*"
	 * 
	 * @param mimeType
	 *            the mimeType, must not be {@code null}
	 * @return the compatible marshaller entries, never {@code null}
	 */
	// or "*/*"
	default List<MarshallerRegistryEntry> getMarshallerRegistryEntries(String mimeType) {
		MarshallerRegistryEntry entry = getMarshallerRegistryEntry(mimeType);
		if(entry == null) {
			return Collections.EMPTY_LIST;
		}
		return Collections.singletonList(entry);
	}
	
	default Stream<MarshallerRegistryEntry> streamMarshallerRegistryEntries(String mimeType) {
		return getMarshallerRegistryEntries(mimeType).stream();
	}
}
