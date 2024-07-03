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
package com.braintribe.testing.model.test.technical.features;

import static com.braintribe.testing.junit.assertions.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Test;

import com.braintribe.model.generic.annotation.Initializer;
import com.braintribe.testing.test.AbstractTest;

/**
 * Tests {@link Initializer}s, i.e. default values, using entity {@link InitializerEntity}.
 *
 * @author michael.lafite
 */
public class InitializerEntityTest extends AbstractTest {

	@Test
	public void test() {
		InitializerEntity entity = InitializerEntity.T.create();

		assertThat(entity.getEnumProperty()).isEqualTo(SimpleEnum.TWO);
		assertThat(entity.getStringProperty()).isEqualTo("abc");
		assertThat(entity.getBooleanProperty()).isTrue();
		assertThat(entity.getIntegerProperty()).isEqualTo(123);
		assertThat(entity.getLongProperty()).isEqualTo(123);
		assertThat(entity.getFloatProperty()).isEqualTo(123.45f);
		assertThat(entity.getDoubleProperty()).isEqualTo(123.45d);
		assertThat(entity.getDecimalProperty()).isEqualTo(new BigDecimal("123.45"));
		assertThat(entity.getDateProperty()).isBefore(new Date());

		assertThat(entity.getPrimitiveBooleanProperty()).isTrue();
		assertThat(entity.getPrimitiveIntegerProperty()).isEqualTo(123);
		assertThat(entity.getPrimitiveLongProperty()).isEqualTo(123);
		assertThat(entity.getPrimitiveFloatProperty()).isEqualTo(123.45f);
		assertThat(entity.getPrimitiveDoubleProperty()).isEqualTo(123.45d);
	}

}
