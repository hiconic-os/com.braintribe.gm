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

import static com.braintribe.testing.junit.assertions.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

import org.junit.Test;

import com.braintribe.codec.marshaller.api.GmSerializationOptions;
import com.braintribe.codec.marshaller.api.TypeExplicitness;
import com.braintribe.codec.marshaller.api.TypeExplicitnessOption;
import com.braintribe.codec.marshaller.yaml.model.MapPropertyEntity;
import com.braintribe.codec.marshaller.yaml.model.PersonEntity;
import com.braintribe.codec.marshaller.yaml.model.SimpleEntity;
import com.braintribe.testing.test.AbstractTest;

public class YamlMarshallerMarshallingTest extends AbstractTest implements YamlMarshallerTestUtils {

	@Test
	public void testMapProblem() {
		SimpleEntity e1 = SimpleEntity.T.create();
		SimpleEntity e2 = SimpleEntity.T.create();
		e1.setInt(1);
		e2.setInt(2);

		MapPropertyEntity mapE = MapPropertyEntity.T.create();
		mapE.getMap().put(e1, e2);

		YamlMarshaller marshaller = new YamlMarshaller();

		ByteArrayOutputStream capture = new ByteArrayOutputStream();

		marshaller.marshall(capture, mapE);

		MapPropertyEntity mapE1 = (MapPropertyEntity) marshaller.unmarshall(new ByteArrayInputStream(capture.toByteArray()));
		assertThat(mapE1).isNotNull();
	}

	@Test
	public void testAnchoring() throws Exception {
		PersonEntity person = createPerson();

		YamlMarshaller marshaller = new YamlMarshaller();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		//@formatter:off
		GmSerializationOptions options = GmSerializationOptions.deriveDefaults()
				.set(TypeExplicitnessOption.class, TypeExplicitness.polymorphic)
				.inferredRootType(PersonEntity.T)
				.build();
		//@formatter:on

		marshaller.marshall(baos, person, options);

		String mashalledYaml = baos.toString();

		System.out.println(mashalledYaml);

		File expectedOutcome = testFile("expected-simple-anchoring.yaml");
		assertThat(expectedOutcome).hasContent(mashalledYaml);
	}

	@Test
	public void testAnchoringTypeSafe() throws Exception {
		PersonEntity person = createPerson();

		YamlMarshaller marshaller = new YamlMarshaller();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		GmSerializationOptions options = GmSerializationOptions.deriveDefaults().build();

		marshaller.marshall(baos, person, options);

		String mashalledYaml = baos.toString();

		System.out.println(mashalledYaml);

		File expectedOutcome = testFile("expected-simple-anchoring-type-safe.yaml");
		assertThat(expectedOutcome).hasContent(mashalledYaml);
	}

	@Test
	public void testEmptyEntityTypeSafe() throws Exception {
		PersonEntity person = createPerson2();

		YamlMarshaller marshaller = new YamlMarshaller();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		GmSerializationOptions options = GmSerializationOptions.deriveDefaults().build();

		marshaller.marshall(baos, person, options);

		String mashalledYaml = baos.toString();

		System.out.println(mashalledYaml);

		File expectedOutcome = testFile("expected-empty-entity-type-safe.yaml");
		assertThat(expectedOutcome).hasContent(mashalledYaml);
	}

	@Test
	public void testEmptyEntity() throws Exception {
		PersonEntity person = createPerson2();

		YamlMarshaller marshaller = new YamlMarshaller();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		GmSerializationOptions options = GmSerializationOptions.deriveDefaults().set(TypeExplicitnessOption.class, TypeExplicitness.polymorphic)
				.inferredRootType(PersonEntity.T).build();

		marshaller.marshall(baos, person, options);

		String mashalledYaml = baos.toString();

		System.out.println(mashalledYaml);

		File expectedOutcome = testFile("expected-empty-entity.yaml");
		assertThat(expectedOutcome).hasContent(mashalledYaml);
	}

	private PersonEntity createPerson() {
		PersonEntity tina = PersonEntity.T.create();
		tina.setFirstName("Tina");

		PersonEntity dirk = PersonEntity.T.create();
		dirk.setFirstName("Dirk");

		tina.setFriend(dirk);
		dirk.setFriend(tina);

		return tina;
	}

	private PersonEntity createPerson2() {
		PersonEntity tina = PersonEntity.T.create();
		tina.setFirstName("Tina");

		PersonEntity dirk = PersonEntity.T.create();

		tina.setFriend(dirk);

		return tina;
	}

}
