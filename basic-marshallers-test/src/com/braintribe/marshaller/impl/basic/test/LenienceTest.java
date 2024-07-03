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

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Test;

import com.braintribe.codec.jseval.genericmodel.GenericModelJsEvalCodec;
import com.braintribe.codec.jseval.genericmodel.PrettyJavaScriptPrototypes;
import com.braintribe.codec.marshaller.api.Marshaller;
import com.braintribe.codec.marshaller.bin.BinMarshaller;
import com.braintribe.codec.marshaller.dom.GmXmlCodec;
import com.braintribe.codec.marshaller.jseval.JsEvalMarshaller;
import com.braintribe.codec.marshaller.stax.StaxMarshaller;
import com.braintribe.codec.marshaller.xml.XmlMarshaller;
import com.braintribe.marshaller.impl.basic.test.model.JackOfAllTrades;

public class LenienceTest {
	private StaxMarshaller staxMarshaller;
	private XmlMarshaller xmlMarshaller;
	private GmXmlCodec<Object> xmlCodec;
	private BinMarshaller binMarshaller;
	private JsEvalMarshaller jseMarshaller;

	@Test
	public void testStaxLenience() throws Exception {
		Marshaller marshaller = getStaxMarshaller();

		InputStream in = getClass().getResourceAsStream("lenience.xml");

		marshaller.unmarshall(in);

		in.close();

		in = getClass().getResourceAsStream("lenience4.xml");

		marshaller.unmarshall(in);

		in.close();

	}

	public static void generateFiles() {
		try {
			Marshaller xmlMarshaller = new XmlMarshaller();
			Marshaller staxMarshaller = new StaxMarshaller();

			FileOutputStream out1 = new FileOutputStream("lenience.xml");
			FileOutputStream out2 = new FileOutputStream("lenience4.xml");

			Object assembly = getAssembly();

			xmlMarshaller.marshall(out1, assembly);
			staxMarshaller.marshall(out2, assembly);

			out1.close();
			out2.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Object getAssembly() {
		JackOfAllTrades j1 = JackOfAllTrades.T.create();
		JackOfAllTrades j2 = JackOfAllTrades.T.create();

		return Arrays.asList(j1, j2);
	}

	@Ignore
	private StaxMarshaller getStaxMarshaller() {
		if (staxMarshaller == null) {
			staxMarshaller = new StaxMarshaller();
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

	@Ignore
	private GmXmlCodec<Object> getXmlCodec() {
		if (xmlCodec == null) {
			xmlCodec = new GmXmlCodec<Object>();
		}

		return xmlCodec;
	}

	@Ignore
	private BinMarshaller getBinMarshaller() {
		if (binMarshaller == null) {
			binMarshaller = new BinMarshaller();
			binMarshaller.setWriteRequiredTypes(true);
		}

		return binMarshaller;
	}

	@Ignore
	private JsEvalMarshaller getJseMarshaller() {
		if (jseMarshaller == null) {
			jseMarshaller = new JsEvalMarshaller();
			GenericModelJsEvalCodec<Object> codec = new GenericModelJsEvalCodec<Object>();
			codec.setHostedMode(false);
			codec.setPrototypes(new PrettyJavaScriptPrototypes());
			jseMarshaller.setCodec(codec);
		}

		return jseMarshaller;
	}
}
