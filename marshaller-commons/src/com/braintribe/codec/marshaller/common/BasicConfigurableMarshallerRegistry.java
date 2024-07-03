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
// ============================================================================

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import com.braintribe.codec.marshaller.api.ConfigurableMarshallerRegistry;
import com.braintribe.codec.marshaller.api.Marshaller;
import com.braintribe.codec.marshaller.api.MarshallerRegistryEntry;
import com.braintribe.mimetype.MimeTypeBasedRegistry;

public class BasicConfigurableMarshallerRegistry extends MimeTypeBasedRegistry<Marshaller> implements ConfigurableMarshallerRegistry {

	public void setMarshallers(Map<String, Marshaller> marshallersMap) {
		for (Map.Entry<String, Marshaller> entry : marshallersMap.entrySet()) {
			String key = entry.getKey();
			if (key != null) {
				key = normalizeMimetype(key);
				registerMarshaller(key, entry.getValue());
			}
		}
	}

	public void setMarshallerRegistryEntries(Set<MarshallerRegistryEntry> marshallerRegistryEntries) {
		for (MarshallerRegistryEntry entry : marshallerRegistryEntries)
			registerMarshallerRegistryEntry(entry);
	}

    public void registerMarshallerRegistryEntry(MarshallerRegistryEntry entry) {
		if (entry instanceof MimeTypeBaseEntry)
			registerEntry((MimeTypeBaseEntry<Marshaller>) entry);
		else
			registerValue(entry.getMimeType(), entry.getMarshaller());
    }

	@Override
	protected BasicMarshallerRegistryEntry newEntry(String mimeType, Marshaller value) {
		return new BasicMarshallerRegistryEntry(mimeType, value);
	}

    @Override
	public Marshaller getMarshaller(String mimeType) {
		return find(mimeType);
	}

	@Override
	public MarshallerRegistryEntry getMarshallerRegistryEntry(String mimeType) {
		return (MarshallerRegistryEntry) findEntry(mimeType);
	}

	@Override
	public void registerMarshaller(String mimeType, Marshaller marshaller) {
		registerValue(mimeType, marshaller);
	}

	@Override
	public List<MarshallerRegistryEntry> getMarshallerRegistryEntries(String mimeType) {
		// First cast not needed in eclipse, but needed for javac
		return (List<MarshallerRegistryEntry>) (List<?>) getEntries(mimeType);
	}

	@Override
	public Stream<MarshallerRegistryEntry> streamMarshallerRegistryEntries(String mimeType) {
		// First cast not needed in eclipse, but needed for javac
		return (Stream<MarshallerRegistryEntry>) (Stream<?>)  streamEntries(mimeType);
	}

	@Override
	public void unregisterMarshaller(String mimeType, Marshaller marshaller) {
		unregisterValue(mimeType, marshaller);
	}

}
