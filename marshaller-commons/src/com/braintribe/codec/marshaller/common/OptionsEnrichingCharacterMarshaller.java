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
// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
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
