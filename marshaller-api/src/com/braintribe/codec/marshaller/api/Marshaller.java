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

import java.io.InputStream;
import java.io.OutputStream;

import com.braintribe.gm.model.reason.Maybe;

public interface Marshaller {

	void marshall(OutputStream out, Object value, GmSerializationOptions options) throws MarshallException;

	default void marshall(OutputStream out, Object value) throws MarshallException {
		marshall(out, value, GmSerializationOptions.defaultOptions);
	}

	Object unmarshall(InputStream in, GmDeserializationOptions options) throws MarshallException;
	
	default Object unmarshall(InputStream in) throws MarshallException {
		return unmarshall(in, GmDeserializationOptions.defaultOptions);
	}
	
	default Maybe<Object> unmarshallReasoned(InputStream in, GmDeserializationOptions options) {
		return Maybe.complete(unmarshall(in, options));	
	}
	
	default Maybe<Object> unmarshallReasoned(InputStream in) throws MarshallException {
		return unmarshallReasoned(in, GmDeserializationOptions.defaultOptions);
	}

}
