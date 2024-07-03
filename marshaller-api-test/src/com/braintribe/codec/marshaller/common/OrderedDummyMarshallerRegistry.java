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
package com.braintribe.codec.marshaller.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.braintribe.codec.marshaller.api.Marshaller;
import com.braintribe.codec.marshaller.api.MarshallerRegistry;
import com.braintribe.codec.marshaller.api.MarshallerRegistryEntry;
import com.braintribe.codec.marshaller.common.BasicConfigurableMarshallerRegistry;
import com.braintribe.common.lcd.NotImplementedException;
import com.braintribe.mimetype.MimeTypeParser;
import com.braintribe.mimetype.ParsedMimeType;

/**
 * 
 * Dummy {@link MarshallerRegistry} that completely ignores the marshallers themselves but preserves the order of the
 * registered marshallers' mime types so that they are in a predictable order during the tests. Most code copied from
 * {@link BasicConfigurableMarshallerRegistry}
 * 
 * @author Neidhart.Orlich
 *
 */
class OrderedDummyMarshallerRegistry implements MarshallerRegistry {

	private final List<String> marshallers = new ArrayList<>();

	@Override
	public Marshaller getMarshaller(String mimeType) {
		throw new NotImplementedException();
	}

	@Override
	public MarshallerRegistryEntry getMarshallerRegistryEntry(String mimeType) {
		throw new NotImplementedException();
	}

	public void registerMarshaller(String mimeType) {
		marshallers.add(mimeType);
	}

	@Override
	public List<MarshallerRegistryEntry> getMarshallerRegistryEntries(String mimeType) {
		if (mimeType == null)
			return Collections.EMPTY_LIST;

		Stream<String> found;

		if (containsWildcard(mimeType)) {
			found = getCompatibleRegistryEntries(mimeType);
		} else {
			String key = normalizeMimetype(mimeType);
			found = marshallers.stream().filter(key::equals);
		}

		return found.map(TrivialTestMarshallerRegistryEntry::new).collect(Collectors.toList());
	}

	private boolean containsWildcard(String mimeType) {
		return mimeType.length() > 0 && (mimeType.charAt(mimeType.length() - 1) == '*' || mimeType.charAt(0) == '*');
	}

	private Stream<String> getCompatibleRegistryEntries(String mimeTypeWithWildcard) {
		Pattern pattern = Pattern.compile(mimeTypeWithWildcard.replace("*", ".*"));
		return marshallers.stream().filter(mimeType -> pattern.matcher(mimeType).matches());
	}

	protected static String normalizeMimetype(String mimeType) {
		if (mimeType == null) {
			return null;
		}

		ParsedMimeType parsedType = MimeTypeParser.getParsedMimeType(mimeType);
		parsedType.getParams().keySet().retainAll(Collections.singleton("spec"));

		return parsedType.toString();
	}

	class TrivialTestMarshallerRegistryEntry implements MarshallerRegistryEntry {

		private final String mimeType;

		public TrivialTestMarshallerRegistryEntry(String mimeType) {
			this.mimeType = mimeType;
		}

		@Override
		public String getMimeType() {
			return mimeType;
		}

		@Override
		public Marshaller getMarshaller() {
			return new CoreTestMarshaller();
		}

	}

}
