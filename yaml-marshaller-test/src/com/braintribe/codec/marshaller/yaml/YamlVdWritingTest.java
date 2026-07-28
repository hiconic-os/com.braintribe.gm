// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ============================================================================
package com.braintribe.codec.marshaller.yaml;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import org.junit.Test;

import com.braintribe.codec.marshaller.api.GmSerializationOptions;
import com.braintribe.codec.marshaller.api.PlaceholderSupport;
import com.braintribe.codec.marshaller.yaml.model.TestEntity;
import com.braintribe.gm.config.yaml.YamlConfigurations;
import com.braintribe.model.bvd.convert.Convert;
import com.braintribe.model.bvd.convert.ToBoolean;
import com.braintribe.model.bvd.convert.ToDate;
import com.braintribe.model.bvd.convert.ToInteger;
import com.braintribe.model.bvd.convert.ToString;
import com.braintribe.model.bvd.string.Concatenation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.value.Variable;
import com.braintribe.model.generic.value.ValueDescriptor;

public class YamlVdWritingTest {

	private static final String PLACEHOLDER_CONFIGURATION = //
			"intValue: \"${HTTP_PORT}\"\n" + //
			"booleanValue: \"${FEATURE_ENABLED}\"\n" + //
			"dateValue: \"${START_DATE}\"\n" + //
			"stringValue: \"jdbc:$$literal://${DB_HOST}:${DB_PORT}/${DB_NAME}\"\n";

	@Test
	public void preservesTypedAndConcatenatedPlaceholdersAcrossRoundTrip() {
		TestEntity first = read(PLACEHOLDER_CONFIGURATION);

		assertSingleVariable(first, "intValue", ToInteger.T, "HTTP_PORT");
		assertSingleVariable(first, "booleanValue", ToBoolean.T, "FEATURE_ENABLED");
		assertSingleVariable(first, "dateValue", ToDate.T, "START_DATE");
		assertJdbcTemplate(first);

		String written = write(first);

		assertThat(written)
				.contains("intValue: \"${HTTP_PORT}\"")
				.contains("booleanValue: \"${FEATURE_ENABLED}\"")
				.contains("dateValue: \"${START_DATE}\"")
				.contains("stringValue: \"jdbc:$$literal://${DB_HOST}:${DB_PORT}/${DB_NAME}\"")
				.doesNotContain("ToInteger")
				.doesNotContain("ToBoolean")
				.doesNotContain("ToDate")
				.doesNotContain("ToString")
				.doesNotContain("Concatenation");

		TestEntity second = read(written);

		assertSingleVariable(second, "intValue", ToInteger.T, "HTTP_PORT");
		assertSingleVariable(second, "booleanValue", ToBoolean.T, "FEATURE_ENABLED");
		assertSingleVariable(second, "dateValue", ToDate.T, "START_DATE");
		assertJdbcTemplate(second);
	}

	private static TestEntity read(String yaml) {
		return YamlConfigurations.read(TestEntity.T)
				.placeholders()
				.from(new StringReader(yaml))
				.get();
	}

	private static String write(TestEntity entity) {
		GmSerializationOptions options = GmSerializationOptions.deriveDefaults()
				.inferredRootType(TestEntity.T)
				.set(PlaceholderSupport.class, true)
				.build();

		StringWriter writer = new StringWriter();
		new YamlMarshaller().marshall(writer, entity, options);
		return writer.toString();
	}

	private static void assertSingleVariable(TestEntity entity, String propertyName,
			EntityType<? extends Convert> conversionType, String variableName) {
		Convert conversion = conversion(entity, propertyName, conversionType);
		assertThat(conversion.getOperand()).isInstanceOf(Variable.class);
		assertThat(((Variable) conversion.getOperand()).getName()).isEqualTo(variableName);
	}

	private static void assertJdbcTemplate(TestEntity entity) {
		Convert conversion = conversion(entity, "stringValue", ToString.T);
		assertThat(conversion.getOperand()).isInstanceOf(Concatenation.class);

		List<Object> operands = ((Concatenation) conversion.getOperand()).getOperands();
		assertThat(operands).hasSize(6);
		assertThat(operands.get(0)).isEqualTo("jdbc:$literal://");
		assertVariable(operands.get(1), "DB_HOST");
		assertThat(operands.get(2)).isEqualTo(":");
		assertVariable(operands.get(3), "DB_PORT");
		assertThat(operands.get(4)).isEqualTo("/");
		assertVariable(operands.get(5), "DB_NAME");
	}

	private static Convert conversion(TestEntity entity, String propertyName, EntityType<? extends Convert> conversionType) {
		Property property = TestEntity.T.getProperty(propertyName);
		ValueDescriptor descriptor = property.getVd(entity);

		assertThat(descriptor).isNotNull();
		assertThat(descriptor.entityType()).isEqualTo(conversionType);
		return (Convert) descriptor;
	}

	private static void assertVariable(Object operand, String name) {
		assertThat(operand).isInstanceOf(Variable.class);
		assertThat(((Variable) operand).getName()).isEqualTo(name);
	}
}
