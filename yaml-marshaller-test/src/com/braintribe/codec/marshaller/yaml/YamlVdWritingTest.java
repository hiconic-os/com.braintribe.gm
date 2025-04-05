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
package com.braintribe.codec.marshaller.yaml;

import java.io.IOException;
import java.io.Writer;

import org.assertj.core.util.Arrays;
import org.junit.Test;

import com.braintribe.codec.marshaller.api.GmSerializationOptions;
import com.braintribe.codec.marshaller.api.PlaceholderSupport;
import com.braintribe.codec.marshaller.yaml.model.TestEntity;
import com.braintribe.model.bvd.string.Concatenation;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.value.Variable;
import com.braintribe.utils.stream.PrintStreamWriter;

public class YamlVdWritingTest {
	
	@Test
	public void testVdTransfer() throws IOException {
		TestEntity testEntity1 = TestEntity.T.create();
		TestEntity testEntity2 = TestEntity.T.create();

		Property intValueProperty = TestEntity.T.getProperty("intValue");
		
		intValueProperty.setVd(testEntity1, var("num"));
		
		Object val = intValueProperty.get(testEntity1);
		intValueProperty.setDirectUnsafe(testEntity2, val);

		System.out.println((Object)intValueProperty.get(testEntity2));
	}
	
	@Test
	public void testPlaceholderWriting() throws IOException {
		
		TestEntity testEntity = TestEntity.T.create();

		Property intValueProperty = TestEntity.T.getProperty("intValue");
		Property stringValueProperty = TestEntity.T.getProperty("stringValue");
		
		Variable intExpr = Variable.T.create();
		intExpr.setTypeSignature("string");
		intExpr.setName("intValue");
		
		intValueProperty.setVd(testEntity, var("num"));
		stringValueProperty.setVd(testEntity, concat("prefix-", var("str1"), "-$infix$-", var("str2"), "-suffix"));
		
		YamlMarshaller marshaller = new YamlMarshaller();
		
		try (Writer writer = new PrintStreamWriter(System.out, true)) {
			GmSerializationOptions options = GmSerializationOptions.deriveDefaults().set(PlaceholderSupport.class, true).build();
			marshaller.marshall(writer, testEntity, options);
		}
	}

	private static Concatenation concat(Object... ops) {
		Concatenation concatenation = Concatenation.T.create();
		concatenation.getOperands().addAll(Arrays.asList(ops));
		return concatenation;
	}
	
	private static Variable var(String name) {
		Variable var = Variable.T.create();
		var.setTypeSignature("string");
		var.setName(name);
		
		return var;
	}
}
