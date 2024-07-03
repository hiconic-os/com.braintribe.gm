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
package com.braintribe.model.processing.test.itw;

import org.junit.Test;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelTypeReflection;
import com.braintribe.model.processing.ImportantItwTestSuperType;
import com.braintribe.model.processing.test.itw.entity.PrimitivePropsEntity;

/**
 * 
 */
public class PrimitiveEntityItwTests extends ImportantItwTestSuperType {

	@Test
	public void testSimpleEntity() {
		PrimitivePropsEntity entity = instantiate(PrimitivePropsEntity.class);

		entity.getIntValue();
		entity.getLongValue();
		entity.getFloatValue();
		entity.getDoubleValue();
		entity.getBooleanValue();
	}

	protected <T extends GenericEntity> T instantiate(Class<T> beanClass) {
		EntityType<T> entityType = typeReflection().getEntityType(beanClass);
		return entityType.create();
	}

	protected <T extends GenericEntity> T instantiatePlain(Class<T> beanClass) {
		EntityType<T> entityType = typeReflection().getEntityType(beanClass);
		return entityType.createPlain();
	}

	private static GenericModelTypeReflection typeReflection() {
		return GMF.getTypeReflection();
	}

}
