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

import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.meta.GmProperty;
import com.braintribe.model.meta.info.GmPropertyInfo;
import com.braintribe.model.processing.meta.oracle.BasicPropertyOracle;
import com.braintribe.model.processing.meta.oracle.PropertyOracle;
import com.braintribe.model.processing.oracle.model.ModelOracleModelProvider;
import com.braintribe.model.processing.oracle.model.basic.animal.Animal;
import com.braintribe.model.processing.oracle.model.basic.mammal.Dog;
import com.braintribe.model.processing.oracle.model.extended.Mutant;
import com.braintribe.testing.junit.assertions.assertj.core.api.Assertions;

/**
 * @see PropertyOracle
 * @see BasicPropertyOracle
 * 
 * @author peter.gazdik
 */
public class BasicPropertyOracleTest extends AbstractOracleTest {

	@Test
	public void getCorrectGmProperty() throws Exception {
		GmProperty gmProperty = getEntityOracle(Dog.T).getProperty(GENDER).asGmProperty();

		Assertions.assertThat(gmProperty).isNotNull();
		Assertions.assertThat(gmProperty.getName()).isEqualTo(GENDER);
		Assertions.assertThat(gmProperty.getDeclaringType().getTypeSignature()).isEqualTo(Animal.T.getTypeSignature());
	}

	@Test
	public void getCorrectProperty() throws Exception {
		Property property = getEntityOracle(Dog.T).getProperty(GENDER).asProperty();

		Assertions.assertThat(property).isNotNull();
		Assertions.assertThat(property.getName()).isEqualTo(GENDER);
		Assertions.assertThat(property.getDeclaringType().getTypeSignature()).isEqualTo(Animal.T.getTypeSignature());
	}

	@Test
	public void getCorrectGmPropertyInfos_NoInheritance() throws Exception {
		// The infos are actually covered by the next test which collects meta-data, so no need to assert much.
		List<GmPropertyInfo> gmPropertyInfos = getEntityOracle(Animal.T).getProperty(GENDER).getGmPropertyInfos();
		Assertions.assertThat(gmPropertyInfos).hasSize(4);
	}

	@Test
	public void getCorrectMetaData_NoInheritance() throws Exception {
		assertOrigins(getEntityOracle(Animal.T).getProperty(GENDER).getMetaData(), //
				qe(FARM, ANIMAL), //
				qe(MAMMAL, ANIMAL), //
				qe(FISH, ANIMAL), //
				qe(ANIMAL, ANIMAL) //
		);
	}

	@Test
	public void getCorrectQualifiedMetaData_NoInheritance() throws Exception {
		assertQualifiedOrigins(getEntityOracle(Animal.T).getProperty(GENDER).getQualifiedMetaData(), //
				qe(FARM, ANIMAL), //
				qe(MAMMAL, ANIMAL), //
				qe(FISH, ANIMAL), //
				qe(ANIMAL, ANIMAL) //
		);

		assertQualifiedOwnerIds(getEntityOracle(Animal.T).getProperty(GENDER).getQualifiedMetaData(), //
				propertyOId(Animal.T, GENDER, FARM_MODEL), //
				propertyOId(Animal.T, GENDER, MAMMAL_MODEL), //
				propertyOId(Animal.T, GENDER, FISH_MODEL), //
				propertyId(Animal.T, GENDER) //
		);
	}

	/**
	 * We test that we only get the info defined on this level, nothing inherited.
	 */
	@Test
	public void getCorrectGmProperties_YesInheritance() throws Exception {
		// The infos are actually covered by the next test which collects meta-data, so no need to assert much.
		List<GmPropertyInfo> gmEntityTypeInfos = getEntityOracle(Mutant.T).getProperty(GENDER).getGmPropertyInfos();
		Assertions.assertThat(gmEntityTypeInfos).hasSize(1);
	}

	@Test
	public void getCorrectMetaData_YesInheritance() throws Exception {
		// @formatter:off
		assertOrigins(getEntityOracle(Mutant.T).getProperty(GENDER).getMetaData(), 
				qe(FARM, MUTANT)
			);
		// @formatter:on
	}

	@Test
	public void getCorrectGmProperty_Inherited_NoOverride() throws Exception {
		GmProperty gmProperty = getEntityOracle(Dog.T).getProperty(WEIGHT).asGmProperty();

		Assertions.assertThat(gmProperty).isNotNull();
		Assertions.assertThat(gmProperty.getName()).isEqualTo(WEIGHT);
		Assertions.assertThat(gmProperty.getDeclaringType().getTypeSignature()).isEqualTo(Animal.T.getTypeSignature());
	}

	@Test
	public void getCorrectGmPropertyInfos_Inherited_NoOverride() throws Exception {
		List<GmPropertyInfo> gmPropertyInfos = getEntityOracle(Dog.T).getProperty(WEIGHT).getGmPropertyInfos();
		Assertions.assertThat(gmPropertyInfos).isEmpty();
	}

	/** qe - qualified entity */
	private static String qe(String modelName, String typeSignature) {
		return ModelOracleModelProvider.originName(modelName, typeSignature);
	}
}
