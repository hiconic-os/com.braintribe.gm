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
package com.braintribe.codec.marshaller.dom;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Set;
import java.util.function.Consumer;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.braintribe.codec.CodecException;
import com.braintribe.codec.marshaller.api.GmCodec;
import com.braintribe.codec.marshaller.api.GmDeserializationOptions;
import com.braintribe.codec.marshaller.api.GmSerializationOptions;
import com.braintribe.model.generic.reflection.GenericModelType;

public class GmXmlCodec<T> implements GmCodec<T, String> {
	private GmDomCodec<T> domCodec;
	private static DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
	private static final TransformerFactory poxTranformerFactory = TransformerFactory.newInstance();

	public GmXmlCodec(Class<T> valueClass) {
		domCodec = new GmDomCodec<T>(valueClass);
	}

	public GmXmlCodec(GenericModelType type) {
		domCodec = new GmDomCodec<T>(type);
	}

	public GmXmlCodec() {
		domCodec = new GmDomCodec<T>();
	}

	public GmDomCodec<T> getDomCodec() {
		return domCodec;
	}

	@Override
	public String encode(T value) throws CodecException {
		return encode(value, GmSerializationOptions.deriveDefaults().build());
	}

	@Override
	public T decode(String encodedValue) throws CodecException {
		return decode(encodedValue, GmDeserializationOptions.deriveDefaults().build());
	}

	@Override
	public Class<T> getValueClass() {
		return domCodec.getValueClass();
	}

	@Override
	public T decode(String encodedValue, GmDeserializationOptions options) throws CodecException {
		StringReader reader = new StringReader(encodedValue);
		Document document = null;
		try {
			document = documentBuilderFactory.newDocumentBuilder().parse(new InputSource(reader));
		} catch (Exception e) {
			throw new CodecException("error while decoding xml into DOM", e);
		}

		return domCodec.decode(document);
	}

	@Override
	public String encode(T value, GmSerializationOptions options) throws CodecException {
		try {

			Document document = domCodec.encode(value, options);

			StringWriter writer = new StringWriter();

			DOMSource domSource = new DOMSource(document);
			StreamResult streamResult = new StreamResult(writer);

			Transformer serializer;
			synchronized (poxTranformerFactory) {
				poxTranformerFactory.setAttribute("indent-number", 2);
				serializer = poxTranformerFactory.newTransformer();
			}

			serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			serializer.setOutputProperty(OutputKeys.INDENT, "yes");

			serializer.transform(domSource, streamResult);

			return writer.toString();
		} catch (CodecException e) {
			throw e;
		} catch (Exception e) {
			throw new CodecException("error while encoding value", e);
		}
	}

	public void setRequiredTypesReceiver(Consumer<Set<String>> requiredTypesReceiver) {
		domCodec.setRequiredTypesReceiver(requiredTypesReceiver);
	}

}
