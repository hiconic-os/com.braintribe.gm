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
package com.braintribe.marshaller.impl.basic.test;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.commons.lang.StringEscapeUtils;
import org.junit.Ignore;
import org.junit.Test;

import com.braintribe.codec.marshaller.api.GmCodec;
import com.braintribe.codec.marshaller.stax.StaxMarshaller;
import com.braintribe.codec.marshaller.xml.XmlMarshaller;

public class CrLfTest {
	private StaxMarshaller staxMarshaller;
	private XmlMarshaller xmlMarshaller;

	@Test
	public void testStax() throws Exception {
		StaxMarshaller marshaller = getStaxMarshaller();
		XmlMarshaller marshaller2 = getXmlMarshaller();
		Object assembly = getAssembly();
		FileOutputStream out = new FileOutputStream("crlf-stax4.xml");
		FileOutputStream out2 = new FileOutputStream("crlf-dom.xml");
		FileOutputStream out3 = new FileOutputStream("crlf-stax4x2.xml");
		marshaller.marshall(out, assembly);
		out.close();

		marshaller2.marshall(out2, assembly);
		out2.close();
		GmCodec<Object, String> stringCodec = marshaller.getStringCodec();
		String xml = stringCodec.encode(assembly);
		// System.out.println(xml);

		Object o = stringCodec.decode(xml);

		// String xml2 = stringCodec.encode(o);
		// System.out.println(xml2);

		marshaller.marshall(out3, o);
		out3.close();

		System.out.println(StringEscapeUtils.escapeJava(marshaller.unmarshall(new FileInputStream("crlf-stax4.xml")).toString()));
		System.out.println(StringEscapeUtils.escapeJava(marshaller.unmarshall(new FileInputStream("crlf-dom.xml")).toString()));
		System.out.println(StringEscapeUtils.escapeJava(marshaller.unmarshall(new FileInputStream("crlf-stax4x2.xml")).toString()));
		System.out.println(StringEscapeUtils.escapeJava(marshaller2.unmarshall(new FileInputStream("crlf-dom.xml")).toString()));
	}

	private static Object getAssembly() {
		return "<&>a\r\ne";
	}

	@Ignore
	private StaxMarshaller getStaxMarshaller() {
		if (staxMarshaller == null) {
			staxMarshaller = new StaxMarshaller();
			staxMarshaller.setCreateEnhancedEntities(true);
			return staxMarshaller;

		}

		return staxMarshaller;
	}

	@Ignore
	private XmlMarshaller getXmlMarshaller() {
		if (xmlMarshaller == null) {
			xmlMarshaller = new XmlMarshaller();
			return xmlMarshaller;

		}

		return xmlMarshaller;
	}
}
