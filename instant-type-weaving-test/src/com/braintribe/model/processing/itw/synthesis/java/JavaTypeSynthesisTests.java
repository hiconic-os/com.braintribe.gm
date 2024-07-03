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

import static com.braintribe.testing.junit.assertions.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.junit.Test;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.processing.ImportantItwTestSuperType;
import com.braintribe.model.processing.itw.analysis.protomodel.ProtoGmEntityTypeImpl;
import com.braintribe.model.processing.itw.analysis.protomodel.ProtoGmSetTypeImpl;
import com.braintribe.model.processing.itw.analysis.protomodel.ProtoGmStringTypeImpl;
import com.braintribe.model.processing.itw.asm.AsmClass;
import com.braintribe.model.weaving.ProtoGmEntityType;
import com.braintribe.utils.junit.assertions.BtAssertions;

public class JavaTypeSynthesisTests extends ImportantItwTestSuperType {

	/**
	 * We just create a {@link GenericEntity} sub-type and check that the generated interface is OK.
	 */
	@Test
	public void simpleEntityType() {
		ProtoGmEntityType basicEntityType = buildEntityType();

		JavaTypeSynthesis jts = new JavaTypeSynthesis();
		AsmClass asmClass = jts.ensureClass(basicEntityType);
		Class<?> jvmClass = asmClass.getClassPool().getJvmClass(asmClass);

		BtAssertions.assertThat(jvmClass).isNotNull().isAssignableTo(GenericEntity.class);

		Method m = findEvalMethod(jvmClass);
		assertThat(m.getGenericReturnType().toString()).isEqualTo("com.braintribe.model.generic.eval.EvalContext<java.util.Set<java.lang.String>>");
	}

	private ProtoGmEntityType buildEntityType() {
		ProtoGmEntityType geType = new ProtoGmEntityTypeImpl();
		geType.setTypeSignature(GenericEntity.class.getName());

		ProtoGmSetTypeImpl setType = new ProtoGmSetTypeImpl();
		setType.setElementType(new ProtoGmStringTypeImpl());
		setType.setTypeSignature("set<string>");

		ProtoGmEntityTypeImpl result = new ProtoGmEntityTypeImpl();
		result.setTypeSignature("com.bt.test.Person");
		result.setSuperTypes(Arrays.asList(geType));
		result.setEvaluatesTo(setType);

		return result;
	}

	private Method findEvalMethod(Class<?> jvmClass) {
		for (Method m : jvmClass.getDeclaredMethods())
			if (m.getName().equals("eval"))
				return m;

		throw new IllegalArgumentException("No eval method found for class: " + jvmClass.getName());
	}

}
