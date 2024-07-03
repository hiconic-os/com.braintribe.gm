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

import static com.braintribe.model.generic.reflection.Model.modelGlobalId;

import org.junit.Test;

import com.braintribe.model.processing.meta.oracle.BasicModelOracle;
import com.braintribe.model.processing.meta.oracle.ModelOracle;

/**
 * @see ModelOracle
 * @see BasicModelOracle
 * 
 * @author peter.gazdik
 */
public class BasicModelOracleTest extends AbstractOracleTest {

	@Test
	public void getCorrectMetaData() throws Exception {
		assertOrigins(oracle.getMetaData(), FARM, MAMMAL, FISH, ANIMAL);
	}

	@Test
	public void getCorrectQualifiedMetaData() throws Exception {
		assertQualifiedOrigins(oracle.getQualifiedMetaData(), FARM, MAMMAL, FISH, ANIMAL);
		assertQualifiedOwnerIds(oracle.getQualifiedMetaData(), modelGlobalId(FARM_MODEL), modelGlobalId(MAMMAL_MODEL), modelGlobalId(FISH_MODEL),
				modelGlobalId(ANIMAL_MODEL));
	}

}
