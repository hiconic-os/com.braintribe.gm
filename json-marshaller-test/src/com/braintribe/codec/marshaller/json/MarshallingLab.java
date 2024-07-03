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
package com.braintribe.codec.marshaller.json;

import java.io.StringReader;
import java.util.List;

import org.junit.Test;

import com.braintribe.codec.marshaller.api.GmDeserializationOptions;
import com.braintribe.codec.marshaller.api.GmSerializationOptions;
import com.braintribe.codec.marshaller.api.OutputPrettiness;
import com.braintribe.codec.marshaller.json.model.TestEntity;
import com.braintribe.codec.marshaller.json.model.TestEnum;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.meta.GmMetaModel;

public class MarshallingLab {
	@Test
	public void mapOutput() {
		TestEntity entity = TestEntity.T.create();

		entity.getEnumMap().put(TestEnum.ONE, "one");
		entity.getEnumMap().put(TestEnum.TWO, "two");
		entity.getStringMap().put("ONE", "one");
		entity.getStringMap().put("TWO", "two");
		entity.setDoubleValue(3.14);
		entity.setFloatValue(2.81F);
		entity.setIntegerValue(42);
		entity.setLongValue(2321321321321321321L);
		JsonStreamMarshaller marshaller = new JsonStreamMarshaller();
		marshaller.marshall(System.out, entity, GmSerializationOptions.deriveDefaults().outputPrettiness(OutputPrettiness.high).build());
	}

	@Test
	public void numberParsing() {

		String json = "[1, 2.3, 123213214214321321]";

		JsonStreamMarshaller marshaller = new JsonStreamMarshaller();
		List<Object> list = (List<Object>) marshaller.decode(json);

		for (Object element : list) {
			System.out.println(element.getClass() + ": " + element);
		}
	}

	// This Cannot be here!! It depends on document-model, which is in tribefire.cortex repo
	// @Test
	// public void documentParsing() throws Exception {
	//
	// JsonStreamMarshaller marshaller = new JsonStreamMarshaller();
	// try (InputStream in = new BufferedInputStream(new FileInputStream(new File("res/document.json")))) {
	// Document doc = (Document) marshaller.unmarshall(in);
	//
	// ByteArrayOutputStream baos = new ByteArrayOutputStream();
	// marshaller.marshall(baos, doc, GmSerializationOptions.defaults.outputPrettiness(OutputPrettiness.high));
	//
	// String json = baos.toString("UTF-8");
	//
	// try (InputStream bain = new ByteArrayInputStream(baos.toByteArray())) {
	// Document checkDoc = (Document) marshaller.unmarshall(bain);
	//
	// System.out.println("Successfully parsed document: "+checkDoc+" from\n"+json);
	// }
	// }
	//
	// }

	// @Test
	public void speedComparisonStreamVsDom() {

		GmMetaModel model = GMF.getTypeReflection().getModel("com.braintribe.model:MetaModel").getMetaModel();

		JsonStreamMarshaller marshaller = new JsonStreamMarshaller();
		JsonStreamMarshaller streamMarshaller = new JsonStreamMarshaller();

		String json = marshaller.encode(model, GmSerializationOptions.deriveDefaults().outputPrettiness(OutputPrettiness.high).build());

		System.out.println(json);

		{
			long s = System.nanoTime();
			try (StringReader reader = new StringReader(json)) {
				marshaller.unmarshall(reader, GmDeserializationOptions.deriveDefaults().build());
			}
			long d = System.nanoTime() - s;
			System.out.println(d / 1_000_000D);
		}

		{
			long s = System.nanoTime();
			try (StringReader reader = new StringReader(json)) {
				streamMarshaller.unmarshall(reader, GmDeserializationOptions.deriveDefaults().build());
			}
			long d = System.nanoTime() - s;
			System.out.println(d / 1_000_000D);
		}

		System.out.println(json);
	}
}
