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

import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.tools.AssemblyTools;
import com.braintribe.model.meta.GmType;
import com.braintribe.model.meta.GmTypeKind;
import com.braintribe.model.processing.itw.analysis.JavaTypeAnalysis;
import com.braintribe.model.processing.itw.analysis.JavaTypeAnalysisException;
import com.braintribe.testing.model.test.technical.features.AnotherComplexEntity;
import com.braintribe.testing.model.test.technical.features.ComplexEntity;
import com.braintribe.testing.model.test.technical.features.SimpleEnum;
import com.braintribe.testing.model.test.technical.features.SimpleTypesEntity;
import com.braintribe.utils.junit.assertions.BtAssertions;

public class JtaCompleteNessTest {

	@Test
	public void testSimpleTypes() {
		Map<String, GmType> types = runJtaFor(SimpleTypesEntity.class);

		Map<String, GmTypeKind> typesToBeTested = new HashMap<>();

		typesToBeTested.put("object", GmTypeKind.BASE);
		typesToBeTested.put("string", GmTypeKind.STRING);
		typesToBeTested.put("boolean", GmTypeKind.BOOLEAN);
		typesToBeTested.put("integer", GmTypeKind.INTEGER);
		typesToBeTested.put("long", GmTypeKind.LONG);
		typesToBeTested.put("float", GmTypeKind.FLOAT);
		typesToBeTested.put("double", GmTypeKind.DOUBLE);
		typesToBeTested.put("decimal", GmTypeKind.DECIMAL);
		typesToBeTested.put("date", GmTypeKind.DATE);
		typesToBeTested.put(GenericEntity.class.getName(), GmTypeKind.ENTITY);
		typesToBeTested.put(SimpleTypesEntity.class.getName(), GmTypeKind.ENTITY);

		for (Map.Entry<String, GmTypeKind> entry : typesToBeTested.entrySet()) {
			String typeSignature = entry.getKey();
			GmTypeKind typeKind = entry.getValue();

			GmType type = types.get(typeSignature);

			BtAssertions.assertThat(type).as("Type not found: " + typeSignature).isNotNull();
			BtAssertions.assertThat(type.typeKind()).isSameAs(typeKind);
		}
	}

	@Test
	public void testComplexTypes() {
		Map<String, GmType> types = runJtaFor(ComplexEntity.class);

		Map<String, GmTypeKind> typesToBeTested = new HashMap<>();

		typesToBeTested.put(GenericEntity.class.getName(), GmTypeKind.ENTITY);
		typesToBeTested.put(ComplexEntity.class.getName(), GmTypeKind.ENTITY);
		typesToBeTested.put(AnotherComplexEntity.class.getName(), GmTypeKind.ENTITY);
		typesToBeTested.put(SimpleEnum.class.getName(), GmTypeKind.ENUM);

		for (Map.Entry<String, GmTypeKind> entry : typesToBeTested.entrySet()) {
			String typeSignature = entry.getKey();
			GmTypeKind typeKind = entry.getValue();

			GmType type = types.get(typeSignature);

			BtAssertions.assertThat(type).as("Type not found: " + typeSignature).isNotNull();
			BtAssertions.assertThat(type.typeKind()).isSameAs(typeKind);
		}
	}

	private Map<String, GmType> runJtaFor(Class<? extends GenericEntity> entityClass) throws JavaTypeAnalysisException {
		GmType gmType = new JavaTypeAnalysis().getGmType(entityClass);

		Collection<GmType> gmTypes = AssemblyTools.findAll(gmType, GmType.T, (type) -> true);

		return gmTypes.stream().collect(toMap(GmType::getTypeSignature, t -> t));
	}
}
