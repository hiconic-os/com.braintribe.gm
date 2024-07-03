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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Function;

import com.braintribe.codec.marshaller.api.GmDeserializationOptions;
import com.braintribe.codec.marshaller.api.GmSerializationOptions;
import com.braintribe.codec.marshaller.api.MarshallException;
import com.braintribe.codec.marshaller.api.Marshaller;

public abstract class AbstractOptionsEnrichingMarshaller implements Marshaller {
	protected Function<GmSerializationOptions, GmSerializationOptions> serializationOptionsEnricher = Function.identity();
	protected Function<GmDeserializationOptions, GmDeserializationOptions> deserializationOptionsEnricher = Function.identity();

	public void setDeserializationOptionsEnricher(Function<GmDeserializationOptions, GmDeserializationOptions> deserializationOptionsEnricher) {
		this.deserializationOptionsEnricher = deserializationOptionsEnricher;
	}
	
	public void setSerializationOptionsEnricher(Function<GmSerializationOptions, GmSerializationOptions> serializationOptionsEnricher) {
		this.serializationOptionsEnricher = serializationOptionsEnricher;
	}
	
	@Override
	public void marshall(OutputStream out, Object value, GmSerializationOptions options) throws MarshallException {
		getDelegate().marshall(out, value, serializationOptionsEnricher.apply(options));
	}
	
	@Override
	public Object unmarshall(InputStream in, GmDeserializationOptions options) throws MarshallException {
		return getDelegate().unmarshall(in, deserializationOptionsEnricher.apply(options));
	}
	
	protected abstract Marshaller getDelegate();
}
