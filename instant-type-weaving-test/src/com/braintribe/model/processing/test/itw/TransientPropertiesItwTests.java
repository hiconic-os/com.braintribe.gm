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

import static com.braintribe.testing.junit.assertions.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Timestamp;

import org.junit.Test;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.TransientProperty;
import com.braintribe.model.processing.ImportantItwTestSuperType;
import com.braintribe.model.processing.test.itw.entity.trans.TransientPropertyEntity;
import com.braintribe.model.processing.test.itw.entity.trans.TransientPropertySubEntity;
import com.braintribe.model.processing.test.jta.model.errors.NonGmEnum;

/**
 * 
 */
public class TransientPropertiesItwTests extends ImportantItwTestSuperType {

	// ################################################
	// #. . . . . . . Getters/Setters . . . . . . . .##
	// ################################################

	@Test
	public void getterSetterAccess() {
		testGetSet(TransientPropertyEntity.T.create());
		testGetSet(TransientPropertyEntity.T.createPlain());

		assertThat(TransientPropertyEntity.T.findProperty("transientName")).isNull();
	}

	private void testGetSet(TransientPropertyEntity e) {
		e.setName("name");
		assertThat(e.getName()).isEqualTo("name");

		e.setObject(Object.class);
		assertThat(e.getObject()).isSameAs(Object.class);

		e.setObject(new Timestamp(0));
		assertThat(e.getObject()).isEqualTo(new Timestamp(0));
	}

	@Test
	public void getterSetterAccess_Sub() {
		testGetSet_Sub(TransientPropertySubEntity.T.create());
		testGetSet_Sub(TransientPropertySubEntity.T.createPlain());
	}

	private void testGetSet_Sub(TransientPropertySubEntity e) {
		testGetSet(e);

		e.setSubName("sub name");
		assertThat(e.getSubName()).isEqualTo("sub name");
	}

	// ################################################
	// ## . . . . . . . . Reflection . . . . . . . . ##
	// ################################################

	@Test
	public void reflected() {
		EntityType<?> et = TransientPropertyEntity.T;

		assertThat(et.getTransientProperties()).hasSize(TransientPropertyEntity.NUMBER_OF_PROPS);

		assertTp(et, "name", String.class);
		assertTp(et, "object", Object.class);
		assertTp(et, "timestamp", Timestamp.class);
		assertTp(et, "nonGmEnum", NonGmEnum.class);
		assertTp(et, "self", et.getJavaType());
	}

	@Test
	public void reflected_Sub() {
		EntityType<?> et = TransientPropertySubEntity.T;

		assertThat(et.getTransientProperties()).hasSize(TransientPropertySubEntity.NUMBER_OF_PROPS);

		TransientProperty tName = et.getTransientProperty("name");
		TransientProperty tSubName = et.getTransientProperty("subName");

		assertThat(tName.getDeclaringType()).isSameAs(TransientPropertyEntity.T);
		assertThat(tSubName.getDeclaringType()).isSameAs(TransientPropertySubEntity.T);
	}

	private void assertTp(EntityType<?> et, String name, Class<?> type) {
		TransientProperty tp = et.getTransientProperty(name);

		assertThat(tp.getName()).isEqualTo(name);
		assertThat(tp.getType()).isEqualTo(type);
	}

	@Test
	public void reflectionAccess() {
		EntityType<TransientPropertyEntity> et = TransientPropertyEntity.T;
		TransientProperty tName = et.getTransientProperty("name");

		TransientPropertyEntity e = et.create();
		tName.set(e, "NAME");
		assertThat(e.getName()).isEqualTo("NAME");

		e.setName("NOME");
		assertThat(tName.<String> get(e)).isEqualTo("NOME");
	}

}
