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
package com.braintribe.codec.marshaller.jseval;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.Required;
import com.braintribe.codec.jseval.genericmodel.GenericModelJsEvalCodec;
import com.braintribe.codec.marshaller.api.GmDeserializationOptions;
import com.braintribe.codec.marshaller.api.GmSerializationOptions;
import com.braintribe.codec.marshaller.api.MarshallException;
import com.braintribe.codec.marshaller.api.Marshaller;
import com.braintribe.logging.Logger;

public class JsEvalMarshaller implements Marshaller {
	private static final Logger logger = Logger.getLogger(JsEvalMarshaller.class);
	private GenericModelJsEvalCodec<Object> codec;
	
	@Configurable @Required
	public void setCodec(GenericModelJsEvalCodec<Object> codec) {
		this.codec = codec;
	}
	
	@Override
	public void marshall(OutputStream out, Object value)
			throws MarshallException {
		Writer writer = null;
		try {
			writer = new OutputStreamWriter(out, "UTF-8");
			String jsEvalCode = codec.encode(value);
			writer.write(jsEvalCode);
			writer.flush();
		} catch (Exception e) {
			throw new MarshallException("error while encoding value", e);
		}
		finally {
			if (writer != null)
				try {
					writer.close();
				} catch (IOException e) {
					logger.error("error while closing writer", e);
				}
		}
	}
	
	@Override
	public Object unmarshall(InputStream in) throws MarshallException {
		throw new UnsupportedOperationException("no unmarshall supported");
	}

	@Override
	public void marshall(OutputStream out, Object value, GmSerializationOptions options) throws MarshallException {
		marshall(out, value);
	}

	@Override
	public Object unmarshall(InputStream in, GmDeserializationOptions options) throws MarshallException {
		return unmarshall(in);
	}
	
}
