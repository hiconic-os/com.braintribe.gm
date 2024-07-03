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
package com.braintribe.model.processing.test.typeReflection;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.GenericModelTypeReflection;

/**
 * @author peter.gazdik
 */
public class TypeReflectionTest {

	static final GenericModelTypeReflection typeReflection = GMF.getTypeReflection();
	static final String GE_SIGNATURE = GenericEntity.class.getName();
	static final String WRONG_SIGNATURE = "non-existent-entity";

	@Test
	public void findsEntityByName() {
		typeReflection.getType(GE_SIGNATURE);
	}

	@Test
	public void findsCollectionByName() {
		typeReflection.getType(String.format("list<%s>", GE_SIGNATURE));
		typeReflection.getType(String.format("set<%s>", GE_SIGNATURE));
		typeReflection.getType(String.format("map<%s,%s>", GE_SIGNATURE, GE_SIGNATURE));
	}

	@Test
	public void returnsNullWhenNotExists() {
		assertThat(findType(WRONG_SIGNATURE)).isNull();

		assertThat(findType(String.format("list<%s>", WRONG_SIGNATURE))).isNull();
		assertThat(findType(String.format("set<%s>", WRONG_SIGNATURE))).isNull();
		assertThat(findType(String.format("map<%s,%s>", WRONG_SIGNATURE, WRONG_SIGNATURE))).isNull();
	}

	private Object findType(String typeSignature) {
		return typeReflection.findType(typeSignature);
	}

}
