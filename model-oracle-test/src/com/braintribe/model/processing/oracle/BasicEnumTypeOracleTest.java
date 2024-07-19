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

import java.util.List;

import org.junit.Test;

import com.braintribe.model.generic.reflection.EnumType;
import com.braintribe.model.meta.GmEnumType;
import com.braintribe.model.meta.info.GmEnumTypeInfo;
import com.braintribe.model.processing.meta.oracle.BasicEnumTypeOracle;
import com.braintribe.model.processing.meta.oracle.EnumTypeOracle;
import com.braintribe.model.processing.oracle.model.basic.animal.Gender;
import com.braintribe.model.processing.oracle.model.extended.Color;
import com.braintribe.testing.junit.assertions.assertj.core.api.Assertions;

/**
 * @see EnumTypeOracle
 * @see BasicEnumTypeOracle
 * 
 * @author peter.gazdik
 */
public class BasicEnumTypeOracleTest extends AbstractOracleTest {

	@Test
	public void getCorrectGmType() throws Exception {
		GmEnumType gmType = getEnumOracle(Gender.class).asGmType();

		Assertions.assertThat(gmType).isNotNull();
		Assertions.assertThat(gmType.getTypeSignature()).isEqualTo(Gender.class.getName());
	}

	@Test
	public void getCorrectType() throws Exception {
		EnumType<?> enumType = getEnumOracle(Gender.class).asType();

		Assertions.assertThat(enumType).isNotNull();
		Assertions.assertThat(enumType.getTypeSignature()).isEqualTo(Gender.class.getName());
	}

	@Test
	public void isDeclared() throws Exception {
		Assertions.assertThat(getEnumOracle(Color.class).isDeclared()).isTrue();
		Assertions.assertThat(getEnumOracle(Gender.class).isDeclared()).isFalse();
	}

	@Test
	public void getCorrectGmEnumTypeInfos() throws Exception {
		// The infos are actually covered by the next test which collects meta-data, so no need to assert much.
		List<GmEnumTypeInfo> gmEnumTypeInfos = getEnumOracle(Gender.class).getGmEnumTypeInfos();
		Assertions.assertThat(gmEnumTypeInfos).hasSize(4);
	}

	@Test
	public void getCorrectMetaData() throws Exception {
		assertOrigins(getEnumOracle(Gender.class).getMetaData(), FARM, MAMMAL, FISH, ANIMAL);
	}

	@Test
	public void getCorrectQualifiedMetaData() throws Exception {
		assertQualifiedOrigins(getEnumOracle(Gender.class).getQualifiedMetaData(), FARM, MAMMAL, FISH, ANIMAL);
		assertQualifiedOwnerIds(getEnumOracle(Gender.class).getQualifiedMetaData(), //
				enumOId(Gender.class, FARM_MODEL), enumOId(Gender.class, MAMMAL_MODEL), enumOId(Gender.class, FISH_MODEL), enumId(Gender.class));
	}

}
