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
package com.braintribe.model.processing.test.jta;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.meta.GmEntityType;
import com.braintribe.model.meta.GmEnumConstant;
import com.braintribe.model.meta.GmEnumType;
import com.braintribe.model.processing.itw.analysis.JavaTypeAnalysis;
import com.braintribe.model.processing.test.itw.entity.trans.TransientPropertyEntity;
import com.braintribe.model.processing.test.jta.model.EntityWithStaticGetSetMethods;
import com.braintribe.model.processing.test.jta.model.EnumTypeWithLiteral;
import com.braintribe.model.processing.test.jta.model.OverriddenIdEntity;
import com.braintribe.model.processing.test.jta.model.errors.EntityWithInvalidSetter;
import com.braintribe.model.processing.test.jta.model.errors.EntityWithWrongNonGmEnumEval;
import com.braintribe.model.processing.test.jta.model.errors.EntityWithWrongNonGmEnumProperty;

/**
 * @author peter.gazdik
 */
public class JtaSimpleTest {

	@Test
	public void overriddenPropertyIsHandledCorrectly() {
		GenericEntity.T.getTypeSignature(); // init JTA

		GmEntityType type = (GmEntityType) new JavaTypeAnalysis().getGmType(OverriddenIdEntity.class);

		assertThat(type.getPropertyOverrides()).isNotEmpty();
	}

	/**
	 * DEVCX-531: There was a bug that the "T" literal was recognized as one of the constants.
	 */
	@Test
	public void enumTypeLiteralIsHandledCorrectly() {
		GmEnumType type = (GmEnumType) new JavaTypeAnalysis().getGmType(EnumTypeWithLiteral.class);

		List<String> constantNames = type.getConstants().stream() //
				.map(GmEnumConstant::getName) //
				.collect(Collectors.toList());

		assertThat(constantNames).containsExactly(EnumTypeWithLiteral.enumValue.name());
	}

	@Test(expected = IllegalArgumentException.class)
	public void enumWithoutEnumBaseAsPropertyIsWrong() {
		new JavaTypeAnalysis().getGmType(EntityWithWrongNonGmEnumProperty.class);
	}

	@Test(expected = IllegalArgumentException.class)
	public void enumWithoutEnumBaseAsEvalTypeIsWrong() {
		new JavaTypeAnalysis().getGmType(EntityWithWrongNonGmEnumEval.class);
	}

	@Test(expected = IllegalArgumentException.class)
	public void deafultMethodCalledLikeSettterIsWrong() {
		new JavaTypeAnalysis().getGmType(EntityWithInvalidSetter.class);
	}

	@Test
	public void staticGetSetMethodsAreOk() {
		GmEntityType type = (GmEntityType) new JavaTypeAnalysis().getGmType(EntityWithStaticGetSetMethods.class);

		assertThat(type.getProperties()).isNullOrEmpty();
	}

	@Test
	public void transientPropertyIsIgnored() {
		GmEntityType type = (GmEntityType) new JavaTypeAnalysis().getGmType(TransientPropertyEntity.class);

		assertThat(type.getProperties()).isNullOrEmpty();
	}
}
