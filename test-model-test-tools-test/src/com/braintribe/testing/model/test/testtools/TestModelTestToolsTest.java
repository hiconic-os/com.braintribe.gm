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
package com.braintribe.testing.model.test.testtools;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.testing.model.test.technical.features.ComplexEntity;
import com.braintribe.testing.model.test.technical.features.SimpleEntity;
import com.braintribe.testing.model.test.technical.features.SimpleEnum;
import com.braintribe.testing.model.test.technical.features.multipleinheritance.AB_BC_but_not_ABC;
import com.braintribe.testing.model.test.technical.limits.ManyValuesEnum;

/**
 * Provides tests for {@link TestModelTestTools}.
 *
 * @author michael.lafite
 *
 */
public class TestModelTestToolsTest {

	@Test
	public void testCreateTestModelMetaModel() {
		GmMetaModel model = TestModelTestTools.createTestModelMetaModel();

		assertThat(model.getTypes()).extracting("typeSignature").contains(SimpleEntity.class.getName(), ComplexEntity.class.getName(),
				AB_BC_but_not_ABC.class.getName());

		assertThat(model.getTypes()).extracting("typeSignature").contains(SimpleEnum.class.getName(), ManyValuesEnum.class.getName());
	}

}
