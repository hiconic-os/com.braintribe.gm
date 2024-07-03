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
package com.braintribe.model.processing.oracle;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.meta.GmCustomType;
import com.braintribe.model.processing.meta.oracle.BasicModelTypes;
import com.braintribe.model.processing.meta.oracle.ModelTypes;
import com.braintribe.model.processing.oracle.model.basic.animal.Animal;
import com.braintribe.model.processing.oracle.model.basic.animal.Gender;
import com.braintribe.model.processing.oracle.model.basic.fish.Fish;
import com.braintribe.model.processing.oracle.model.basic.mammal.Dog;
import com.braintribe.model.processing.oracle.model.extended.Color;
import com.braintribe.model.processing.oracle.model.extended.Farm;
import com.braintribe.testing.junit.assertions.assertj.core.api.Assertions;

/**
 * @see ModelTypes
 * @see BasicModelTypes
 * 
 * @author peter.gazdik
 */
public class BasicModelTypesTest extends AbstractOracleTest {

	private final ModelTypes types = oracle.getTypes();
	private Set<String> signatures;

	@Test
	public void getAllTypes() throws Exception {
		collectSignatures(types.asGmTypes());

		assertNotContainsSignatures("object", "integer");
		assertSignatures(Animal.T, Fish.T, Dog.T, Farm.T);
		assertSignatures(Gender.class.getName());
	}

	@Test
	public void getDeclaredTypes() throws Exception {
		collectSignatures(types.onlyDeclared().asGmTypes());

		assertSignatures(Farm.T);
		assertSignatures(Color.class.getName());

		assertNotContainsSignatures(Animal.T, Fish.T, Dog.T);
		assertNotContainsSignatures(Gender.class.getName());
	}

	@Test
	public void getInheritedTypes() throws Exception {
		collectSignatures(types.onlyInherited().asGmTypes());

		assertSignatures(Animal.T, Fish.T, Dog.T);
		assertSignatures(Gender.class.getName());

		assertNotContainsSignatures(Farm.T);
		assertNotContainsSignatures(Color.class.getName());
	}

	@Test
	public void getOnlyEntities() throws Exception {
		collectSignatures(types.onlyEntities().asGmTypes());

		assertSignatures(Animal.T, Fish.T, Dog.T, Farm.T);
		assertNotContainsSignatures(Gender.class.getName(), Color.class.getName());
	}

	@Test
	public void getOnlyEnums() throws Exception {
		collectSignatures(types.onlyEnums().asGmTypes());

		assertSignatures(Gender.class.getName(), Color.class.getName());
		assertNotContainsSignatures(Animal.T, Fish.T, Dog.T, Farm.T);
	}

	private void collectSignatures(Stream<GmCustomType> gmTypes) {
		signatures = gmTypes.map(GmCustomType::getTypeSignature).collect(Collectors.toSet());
	}

	private void assertSignatures(EntityType<?>... ets) {
		Set<String> expected = Arrays.asList(ets).stream().map(EntityType::getTypeSignature).collect(Collectors.toSet());
		assertSignaturesHelper(expected);
	}

	private void assertSignatures(String... expected) {
		Assertions.assertThat(signatures).contains(expected);
	}

	private void assertSignaturesHelper(Iterable<String> expected) {
		Assertions.assertThat(signatures).containsAll(expected);
	}

	private void assertNotContainsSignatures(EntityType<?>... ets) {
		Set<String> expected = Arrays.asList(ets).stream().map(EntityType::getTypeSignature).collect(Collectors.toSet());
		assertNotContainsSignaturesHelper(expected);
	}

	private void assertNotContainsSignatures(String... expected) {
		Assertions.assertThat(signatures).doesNotContain(expected);
	}

	private void assertNotContainsSignaturesHelper(Iterable<String> expected) {
		Assertions.assertThat(signatures).doesNotContainAnyElementsOf(expected);
	}

}
