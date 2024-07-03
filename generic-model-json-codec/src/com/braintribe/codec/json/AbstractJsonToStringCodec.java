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
package com.braintribe.codec.json;

import com.braintribe.cfg.Configurable;
import com.braintribe.codec.CodecException;
import com.braintribe.codec.marshaller.api.GmCodec;
import com.braintribe.codec.marshaller.api.GmDeserializationOptions;
import com.braintribe.codec.marshaller.api.GmSerializationOptions;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public abstract class AbstractJsonToStringCodec<T> implements GmCodec<T, String> {
	private ObjectMapper mapper = new ObjectMapper();
	private boolean prettyPrint = true;

	public AbstractJsonToStringCodec() {
		mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
	}

	@Configurable
	public void setPrettyPrint(boolean prettyPrint) {
		this.prettyPrint = prettyPrint;
	}

	protected abstract GmCodec<T, JsonNode> getJsonDelegateCodec();

	@Override
	public T decode(String encodedValue) throws CodecException {
		return decode(encodedValue, GmDeserializationOptions.deriveDefaults().build());
	}

	@Override
	public String encode(T value) throws CodecException {
		return encode(value, GmSerializationOptions.deriveDefaults().build());
	}

	@Override
	public T decode(String encodedValue, GmDeserializationOptions options) throws CodecException {
		try {
			JsonNode json = mapper.readValue(encodedValue, JsonNode.class);
			return getJsonDelegateCodec().decode(json, options);
		} catch (Exception e) {
			throw new CodecException("error while decoding json", e);
		}
	}

	@Override
	public String encode(T value, GmSerializationOptions options) throws CodecException {
		try {
			JsonNode jsonValue = getJsonDelegateCodec().encode(value, options);
			ObjectWriter writer = prettyPrint ? mapper.writerWithDefaultPrettyPrinter() : mapper.writer();
			String encodedValue = writer.writeValueAsString(jsonValue);
			return encodedValue;
		} catch (Exception e) {
			throw new CodecException("error while encoding json", e);
		}
	}

	@Override
	public Class<T> getValueClass() {
		return getJsonDelegateCodec().getValueClass();
	}
}
