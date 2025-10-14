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
package com.braintribe.model.processing.itw.synthesis.java;

import static com.braintribe.model.processing.itw.synthesis.java.JtsBasicTests.requireDeclaredMethod;
import static com.braintribe.testing.junit.assertions.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.junit.Test;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.meta.GmEntityType;
import com.braintribe.model.processing.ImportantItwTestSuperType;
import com.braintribe.model.processing.itw.analysis.JavaTypeAnalysis;
import com.braintribe.model.processing.itw.asm.AsmClass;
import com.braintribe.model.processing.test.itw.entity.annotation.CustomMdEnum;
import com.braintribe.model.processing.test.itw.entity.annotation.CustomMd_Annotation;
import com.braintribe.model.processing.test.itw.entity.annotation.CustomRepeatableMd_Annotation;
import com.braintribe.model.processing.test.itw.entity.annotation.CustomRepeatableMd_Annotations;
import com.braintribe.model.processing.test.itw.entity.annotation.HasCustomMdProperties;
import com.braintribe.utils.junit.assertions.BtAssertions;
import com.braintribe.utils.lcd.NullSafe;

public class JtsAdvancedTests extends ImportantItwTestSuperType {

	private static JavaTypeAnalysis jta = new JavaTypeAnalysis();

	/** We analyze an existing entity, modify the signature and see if synthesis works. */
	@Test
	public void entityWithCustomAnnotationsType() {
		GmEntityType gmType = (GmEntityType) jta.getGmType(HasCustomMdProperties.class);
		gmType.setTypeSignature(testSignatureFor(gmType));

		JavaTypeSynthesis jts = new JavaTypeSynthesis();
		AsmClass asmClass = jts.ensureClass(gmType);
		Class<?> jvmClass = asmClass.getClassPool().getJvmClass(asmClass);

		BtAssertions.assertThat(jvmClass).isNotNull().isAssignableTo(GenericEntity.class);

		{
			Method m = requireDeclaredMethod(jvmClass, "getPredicate");
			assertThat(m.getReturnType()).isSameAs(String.class);
		}

		{
			Method m = requireDeclaredMethod(jvmClass, "getCustomMd");
			assertThat(m.getReturnType()).isSameAs(String.class);

			CustomMd_Annotation anno = requireAnnotation(m, CustomMd_Annotation.class);
			assertThat(anno.inherited()).isEqualTo(false);
			assertThat(anno.important()).isEqualTo(true);
			assertThat(anno.customEnum()).isEqualTo(CustomMdEnum.bbb);
			assertThat(anno.customEnumList()).containsExactly(CustomMdEnum.aaa, CustomMdEnum.bbb);
			assertThat(anno.customEnumSet()).containsExactly(CustomMdEnum.aaa, CustomMdEnum.ccc);
		}

		{
			Method m = requireDeclaredMethod(jvmClass, "getSingleRepeatableMd");
			assertThat(m.getReturnType()).isSameAs(String.class);

			CustomRepeatableMd_Annotation anno = requireAnnotation(m, CustomRepeatableMd_Annotation.class);
			assertThat(anno.value()).isEqualTo("one");
		}

		{
			Method m = requireDeclaredMethod(jvmClass, "getTwoRepeatableMds");
			assertThat(m.getReturnType()).isSameAs(String.class);

			CustomRepeatableMd_Annotations repeatableAnno = requireAnnotation(m, CustomRepeatableMd_Annotations.class);
			CustomRepeatableMd_Annotation[] annos = repeatableAnno.value();
			assertThat(annos).hasSize(2);

			// NOTE IF FAILING:  just change it to expect the 2 values in any order.
			// (Hopefully not relevant, the order should be stable despite GmEntityType.metaData being a Set.)
			assertThat(annos[0].value()).isEqualTo("one");
			assertThat(annos[1].value()).isEqualTo("two");
		}
	}

	private String testSignatureFor(GmEntityType gmType) {
		return "test." + gmType.getTypeSignature();
	}

	private <T extends Annotation> T requireAnnotation(Method m, Class<T> annoClass) {
		return NullSafe.nonNull(m.getAnnotation(annoClass), "annotation: " + annoClass.getSimpleName());
	}

}
