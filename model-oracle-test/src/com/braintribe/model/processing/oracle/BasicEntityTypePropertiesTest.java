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

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import com.braintribe.model.meta.GmProperty;
import com.braintribe.model.processing.meta.oracle.BasicEnumTypeConstants;
import com.braintribe.model.processing.meta.oracle.EntityTypeOracle;
import com.braintribe.model.processing.meta.oracle.EntityTypeProperties;
import com.braintribe.model.processing.meta.oracle.EnumTypeConstants;
import com.braintribe.model.processing.oracle.model.basic.mammal.Dog;
import com.braintribe.testing.junit.assertions.assertj.core.api.Assertions;

/**
 * @see EnumTypeConstants
 * @see BasicEnumTypeConstants
 * 
 * @author peter.gazdik
 */
public class BasicEntityTypePropertiesTest extends AbstractOracleTest {

	private final EntityTypeOracle dogOracle = oracle.getEntityTypeOracle(Dog.T);
	private final EntityTypeProperties entityTypeProperties = dogOracle.getProperties();
	private Set<String> propertyNames;

	@Test
	public void getAllProperties() throws Exception {
		collectPropertyNames(entityTypeProperties.asGmProperties());
		assertNames("breed", "mammalClass", "name", "gender", "weight", "initialized", "partition", "globalId", "id", "petNumber");
	}

	@Test
	public void getDeclaredProperties() throws Exception {
		collectPropertyNames(entityTypeProperties.onlyDeclared().asGmProperties());
		assertNames("breed");
	}

	@Test
	public void getInheritedProperties() throws Exception {
		collectPropertyNames(entityTypeProperties.onlyInherited().asGmProperties());
		assertNames("mammalClass", "name", "gender", "weight", "initialized", "partition", "globalId", "id", "petNumber");
	}

	@Test
	public void getFilteredProperties() throws Exception {
		collectPropertyNames(entityTypeProperties.filter(gmProperty -> gmProperty.getName().contains("er")).asGmProperties());
		assertNames("gender", "petNumber");
	}

	private void collectPropertyNames(Stream<GmProperty> gmProperties) {
		propertyNames = gmProperties.map(GmProperty::getName).collect(Collectors.toSet());
	}

	private void assertNames(String... expected) {
		Assertions.assertThat(propertyNames).containsOnly(expected);
	}
}
