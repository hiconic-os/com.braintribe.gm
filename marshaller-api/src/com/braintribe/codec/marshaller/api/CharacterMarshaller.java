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

import java.io.Reader;
import java.io.Writer;

import com.braintribe.gm.model.reason.Maybe;

public interface CharacterMarshaller extends Marshaller {

	void marshall(Writer writer, Object value, GmSerializationOptions options) throws MarshallException;

	default void marshall(Writer writer, Object value) throws MarshallException {
		marshall(writer, value, GmSerializationOptions.deriveDefaults().build());
	}

	Object unmarshall(Reader reader, GmDeserializationOptions options) throws MarshallException;

	default Object unmarshall(Reader reader) throws MarshallException {
		return unmarshall(reader, GmDeserializationOptions.deriveDefaults().build());
	}

	default Maybe<Object> unmarshallReasoned(Reader reader, GmDeserializationOptions options) {
		return Maybe.complete(unmarshall(reader, options));
	}

	default Maybe<Object> unmarshallReasoned(Reader reader) throws MarshallException {
		return unmarshallReasoned(reader, GmDeserializationOptions.defaultOptions);
	}
}
