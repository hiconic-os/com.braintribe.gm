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
package com.braintribe.model.processing.cmd;

import java.util.List;
import java.util.function.Supplier;

import org.junit.Test;

import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.meta.data.EntityTypeMetaData;
import com.braintribe.model.meta.data.EnumConstantMetaData;
import com.braintribe.model.meta.data.PropertyMetaData;
import com.braintribe.model.processing.cmd.test.meta.entity.SimpleEntityMetaData;
import com.braintribe.model.processing.cmd.test.meta.enumeration.SimpleEnumConstantMetaData;
import com.braintribe.model.processing.cmd.test.meta.property.SimplePropertyMetaData;
import com.braintribe.model.processing.cmd.test.model.Color;
import com.braintribe.model.processing.cmd.test.model.Person;
import com.braintribe.model.processing.cmd.test.model.Teacher;
import com.braintribe.model.processing.cmd.test.provider.ImportantMdProvider;
import com.braintribe.model.processing.meta.cmd.result.ConstantMdResult;
import com.braintribe.model.processing.meta.cmd.result.EntityMdResult;
import com.braintribe.model.processing.meta.cmd.result.PropertyMdResult;

/**
 * Tests that important MDs are resolved correctly.
 */
public class ImportantMetaDataResolvingTests extends MetaDataResolvingTestBase {

	// ######################################
	// ## . . . . . Important MD . . . . . ##
	// ######################################

	/** @see ImportantMdProvider#addImportantEntityMd */
	@Test
	public void test_Entity_ResolvesImportantMd() {
		EntityMdResult<SimpleEntityMetaData> emd = getMetaData().entityClass(Teacher.class).meta(SimpleEntityMetaData.T);

		EntityTypeMetaData exclusive = emd.exclusive();
		assertOneMetaData(SimpleEntityMetaData.T, exclusive);
		assertTypeSignature(exclusive, Person.class);

		List<? extends EntityTypeMetaData> list = emd.list();
		assertMultipleMetaData(SimpleEntityMetaData.T, list, 2);
		assertTypeSignatures(list, Person.class, Teacher.class);
	}

	/** @see ImportantMdProvider#addImportantPropertyMd */
	@Test
	public void test_Property_ResolvesImportantMd_ImportantHasHigherPrio() {
		PropertyMdResult<SimplePropertyMetaData> pmd = getMetaData().entityClass(Teacher.class).property("age").meta(SimplePropertyMetaData.T);

		PropertyMetaData exclusive = pmd.exclusive();
		assertOneMetaData(SimplePropertyMetaData.T, exclusive);
		assertTypeSignature(exclusive, Person.class);

		List<? extends PropertyMetaData> list = pmd.list();
		assertMultipleMetaData(SimplePropertyMetaData.T, list, 2);
		assertTypeSignatures(list, Person.class, Teacher.class);
	}

	/** @see ImportantMdProvider#addImportantPropertyMdWithLowPrio */
	@Test
	public void test_Property_ResolvesImportantMd_LocalHasHigherPrio() {
		PropertyMdResult<SimplePropertyMetaData> pmd = getMetaData().entityClass(Teacher.class).property("name").meta(SimplePropertyMetaData.T);

		PropertyMetaData exclusive = pmd.exclusive();
		assertOneMetaData(SimplePropertyMetaData.T, exclusive);
		assertTypeSignature(exclusive, Teacher.class);

		List<? extends PropertyMetaData> list = pmd.list();
		assertMultipleMetaData(SimplePropertyMetaData.T, list, 2);
		assertTypeSignatures(list, Teacher.class, Person.class);
	}

	/** @see ImportantMdProvider#addImportantEnumConstantMd */
	@Test
	public void test_EnumConstant_ResolvesImportantMd() {
		ConstantMdResult<SimpleEnumConstantMetaData> emd = getMetaData().enumConstant(Color.GREEN).meta(SimpleEnumConstantMetaData.T);

		EnumConstantMetaData exclusive = emd.exclusive();
		assertOneMetaData(SimpleEnumConstantMetaData.T, exclusive);
	}

	// ########################################
	// ## . . . . . . . Setup . . . . . . . .##
	// ########################################

	@Override
	protected Supplier<GmMetaModel> getModelProvider() {
		return new ImportantMdProvider();
	}

}
