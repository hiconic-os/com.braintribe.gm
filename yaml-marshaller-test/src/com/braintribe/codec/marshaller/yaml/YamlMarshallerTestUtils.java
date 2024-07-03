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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.braintribe.codec.marshaller.api.GmDeserializationOptions;
import com.braintribe.codec.marshaller.api.GmSerializationOptions;
import com.braintribe.testing.junit.assertions.assertj.core.api.Assertions;

public interface YamlMarshallerTestUtils {

	static void assertContent(String inputFile, Object expectation) throws IOException, FileNotFoundException {
		assertContent(inputFile, expectation, GmDeserializationOptions.defaultOptions);
	}

	static void assertContent(String inputFile, Object expectation, GmDeserializationOptions options)
			throws IOException, FileNotFoundException {
		YamlMarshaller marshaller = new YamlMarshaller();

		Object parsedValue = null;

		try (InputStream in = new FileInputStream(inputFile)) {
			parsedValue = marshaller.unmarshall(in, options);
		}

		Assertions.assertThat(parsedValue).as("Marshaller did not generate expected elements").isEqualTo(expectation);
	}

	static byte[] marshallToByteArray(Object object, GmSerializationOptions marshallingOptions) {
		YamlMarshaller marshaller = new YamlMarshaller();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		marshaller.marshall(baos, object, marshallingOptions);

		return baos.toByteArray();
	}

	static String marshallToString(Object object, GmSerializationOptions marshallingOptions) {
		YamlMarshaller marshaller = new YamlMarshaller();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		marshaller.marshall(baos, object, marshallingOptions);

		return baos.toString();
	}

	static <T> T marshallingRoundTrip(Object object, GmSerializationOptions marshallingOptions) throws IOException {
		YamlMarshaller marshaller = new YamlMarshaller();

		try (ByteArrayInputStream in = new ByteArrayInputStream(marshallToByteArray(object, marshallingOptions))) {
			return (T) marshaller.unmarshall(in);
		}
	}
}
