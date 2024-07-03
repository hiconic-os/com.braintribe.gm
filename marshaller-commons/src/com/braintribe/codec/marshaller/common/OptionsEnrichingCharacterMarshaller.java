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

import java.io.Reader;
import java.io.Writer;

import com.braintribe.codec.marshaller.api.CharacterMarshaller;
import com.braintribe.codec.marshaller.api.GmDeserializationOptions;
import com.braintribe.codec.marshaller.api.GmSerializationOptions;
import com.braintribe.codec.marshaller.api.MarshallException;

public class OptionsEnrichingCharacterMarshaller extends AbstractOptionsEnrichingMarshaller implements CharacterMarshaller {
	private CharacterMarshaller delegate;
	
	public void setDelegate(CharacterMarshaller delegate) {
		this.delegate = delegate;
	}

	@Override
	protected CharacterMarshaller getDelegate() {
		return delegate;
	}

	@Override
	public void marshall(Writer writer, Object value, GmSerializationOptions options) throws MarshallException {
		getDelegate().marshall(writer, value, serializationOptionsEnricher.apply(options));
	}

	@Override
	public Object unmarshall(Reader reader, GmDeserializationOptions options) throws MarshallException {
		return getDelegate().unmarshall(reader, deserializationOptionsEnricher.apply(options));
	}
}
