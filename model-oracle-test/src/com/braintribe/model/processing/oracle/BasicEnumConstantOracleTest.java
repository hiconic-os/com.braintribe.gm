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

import com.braintribe.model.meta.GmEnumConstant;
import com.braintribe.model.meta.info.GmEnumConstantInfo;
import com.braintribe.model.processing.meta.oracle.BasicEnumConstantOracle;
import com.braintribe.model.processing.meta.oracle.EnumConstantOracle;
import com.braintribe.model.processing.oracle.model.basic.animal.Gender;
import com.braintribe.testing.junit.assertions.assertj.core.api.Assertions;

/**
 * @see EnumConstantOracle
 * @see BasicEnumConstantOracle
 * 
 * @author peter.gazdik
 */
public class BasicEnumConstantOracleTest extends AbstractOracleTest {

	@Test
	public void getCorrectGmEnumConstant() throws Exception {
		GmEnumConstant gmEnumConstant = getEnumOracle(Gender.class).getConstant(Gender.M).asGmEnumConstant();

		Assertions.assertThat(gmEnumConstant).isNotNull();
		Assertions.assertThat(gmEnumConstant.getName()).isEqualTo("M");
		Assertions.assertThat(gmEnumConstant.getDeclaringType().getTypeSignature()).isEqualTo(Gender.class.getName());
	}

	@Test
	public void getCorrectEnumConstant() throws Exception {
		Enum<?> enumValue = getEnumOracle(Gender.class).getConstant(Gender.M).asEnum();

		Assertions.assertThat(enumValue).isNotNull();
		Assertions.assertThat(enumValue.name()).isEqualTo("M");
		Assertions.assertThat(enumValue.getDeclaringClass()).isSameAs(Gender.class);
	}

	@Test
	public void getCorrectGmEnumConstantsProperties() throws Exception {
		// The infos are actually covered by the next test which collects meta-data, so no need to assert much.
		List<GmEnumConstantInfo> gmEnumConstantInfos = getEnumOracle(Gender.class).getConstant(Gender.M).getGmEnumConstantInfos();
		Assertions.assertThat(gmEnumConstantInfos).hasSize(4);
	}

	@Test
	public void getCorrectMetaData() throws Exception {
		assertOrigins(getEnumOracle(Gender.class).getConstant(Gender.M).getMetaData(), FARM, MAMMAL, FISH, ANIMAL);
	}

	@Test
	public void getCorrectQualifiedMetaData() throws Exception {
		assertQualifiedOrigins(getEnumOracle(Gender.class).getConstant(Gender.M).getQualifiedMetaData(), FARM, MAMMAL, FISH, ANIMAL);
		assertQualifiedOwnerIds(getEnumOracle(Gender.class).getConstant(Gender.M).getQualifiedMetaData(), //
				constantOId(Gender.M, FARM_MODEL), //
				constantOId(Gender.M, MAMMAL_MODEL), //
				constantOId(Gender.M, FISH_MODEL), //
				constantId(Gender.M));
	}
}
