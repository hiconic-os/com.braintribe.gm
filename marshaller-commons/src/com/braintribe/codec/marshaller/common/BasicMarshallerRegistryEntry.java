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

import com.braintribe.codec.marshaller.api.Marshaller;
import com.braintribe.codec.marshaller.api.MarshallerRegistryEntry;
import com.braintribe.mimetype.MimeTypeBasedRegistry.MimeTypeBaseEntry;

public class BasicMarshallerRegistryEntry extends MimeTypeBaseEntry<Marshaller> implements MarshallerRegistryEntry {

	public BasicMarshallerRegistryEntry() {
		super(null, null);
	}

	public BasicMarshallerRegistryEntry(String mimeType, Marshaller marshaller) {
		super(mimeType, marshaller);
	}

	@Override
	public Marshaller getMarshaller() {
		return value;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public void setMarshaller(Marshaller marshaller) {
		this.value = marshaller;
	}

}
