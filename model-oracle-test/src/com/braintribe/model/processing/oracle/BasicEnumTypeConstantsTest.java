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

import com.braintribe.model.meta.GmEnumConstant;
import com.braintribe.model.processing.meta.oracle.BasicEntityTypeProperties;
import com.braintribe.model.processing.meta.oracle.EntityTypeProperties;
import com.braintribe.model.processing.meta.oracle.EnumTypeConstants;
import com.braintribe.model.processing.meta.oracle.EnumTypeOracle;
import com.braintribe.model.processing.oracle.model.basic.animal.Gender;
import com.braintribe.testing.junit.assertions.assertj.core.api.Assertions;

/**
 * @see EntityTypeProperties
 * @see BasicEntityTypeProperties
 * 
 * @author peter.gazdik
 */
public class BasicEnumTypeConstantsTest extends AbstractOracleTest {

	private final EnumTypeOracle genderOracle = oracle.getEnumTypeOracle(Gender.class);
	private final EnumTypeConstants enumTypeConstants = genderOracle.getConstants();
	private Set<String> constantNames;

	@Test
	public void getAllProperties() throws Exception {
		collectPropertyNames(enumTypeConstants.asGmEnumConstants());
		assertNames("M", "F", "H");
	}

	@Test
	public void getFilteredProperties() throws Exception {
		collectPropertyNames(enumTypeConstants.filter(gmConstant -> gmConstant.getName().contains("F")).asGmEnumConstants());
		assertNames("F");
	}

	private void collectPropertyNames(Stream<GmEnumConstant> gmEnumConstants) {
		constantNames = gmEnumConstants.map(GmEnumConstant::getName).collect(Collectors.toSet());
	}

	private void assertNames(String... expected) {
		Assertions.assertThat(constantNames).containsOnly(expected);
	}
}
