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

import static org.fest.assertions.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import org.junit.Test;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.meta.data.EntityTypeMetaData;
import com.braintribe.model.meta.data.EnumConstantMetaData;
import com.braintribe.model.meta.data.EnumTypeMetaData;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.meta.data.ModelMetaData;
import com.braintribe.model.meta.data.PropertyMetaData;
import com.braintribe.model.processing.cmd.test.meta.ActivableMetaData;
import com.braintribe.model.processing.cmd.test.meta.entity.SimpleEntityMetaData;
import com.braintribe.model.processing.cmd.test.meta.enumeration.SimpleEnumConstantMetaData;
import com.braintribe.model.processing.cmd.test.meta.enumeration.SimpleEnumMetaData;
import com.braintribe.model.processing.cmd.test.meta.model.SimpleModelMetaData;
import com.braintribe.model.processing.cmd.test.meta.property.SimplePropertyMetaData;
import com.braintribe.model.processing.cmd.test.model.Color;
import com.braintribe.model.processing.cmd.test.model.Teacher;
import com.braintribe.model.processing.cmd.test.provider.AbstractModelSupplier;
import com.braintribe.model.processing.cmd.test.provider.RawModelProvider;
import com.braintribe.model.processing.meta.cmd.CmdResolverBuilder;
import com.braintribe.model.processing.meta.cmd.extended.EntityRelatedMdDescriptor;
import com.braintribe.model.processing.meta.cmd.extended.EnumRelatedMdDescriptor;
import com.braintribe.model.processing.meta.cmd.extended.MdDescriptor;
import com.braintribe.model.processing.meta.cmd.extended.ModelMdDescriptor;

/**
 * Tests that default value is resolved correctly.
 */
public class DefaultMetaDataResolvingTests extends MetaDataResolvingTestBase {

	// #######################################
	// ## . . . . . Default value . . . . . ##
	// #######################################

	@Test
	public void test_Model_ResolvesDefaultValue() {
		ModelMetaData mmd = getMetaData().meta(SimpleModelMetaData.T).exclusive();
		assertOneMetaData(SimpleModelMetaData.T, mmd);
	}

	@Test
	public void test_Enum_ResolvesDefaultValue() {
		EnumTypeMetaData mmd = getMetaData().enumClass(Color.class).meta(SimpleEnumMetaData.T).exclusive();
		assertOneMetaData(SimpleEnumMetaData.T, mmd);
	}

	@Test
	public void test_EnumConstant_ResolvesDefaultValue() {
		EnumConstantMetaData mmd = getMetaData().enumConstant(Color.GREEN).meta(SimpleEnumConstantMetaData.T).exclusive();
		assertOneMetaData(SimpleEnumConstantMetaData.T, mmd);
	}

	@Test
	public void test_Entity_ResolvesDefaultValue() {
		EntityTypeMetaData mmd = getMetaData().entityClass(Teacher.class).meta(SimpleEntityMetaData.T).exclusive();
		assertOneMetaData(SimpleEntityMetaData.T, mmd);
	}

	@Test
	public void test_Property_ResolvesDefaultValue() {
		PropertyMetaData mmd = getMetaData().entityClass(Teacher.class).property("age").meta(SimplePropertyMetaData.T).exclusive();
		assertOneMetaData(SimplePropertyMetaData.T, mmd);
	}

	// ########################################
	// ## . . . Default extended value . . . ##
	// ########################################

	@Test
	public void test_Model_ResolvesDefaultValue_Extended() {
		ModelMdDescriptor mmd = getMetaData().meta(SimpleModelMetaData.T).exclusiveExtended();
		assertOneDefaultExtendedMetaData(SimpleModelMetaData.class, mmd);
	}

	@Test
	public void test_Enum_ResolvesDefaultValue_Extended() {
		EnumRelatedMdDescriptor mmd = getMetaData().enumClass(Color.class).meta(SimpleEnumMetaData.T).exclusiveExtended();
		assertOneDefaultExtendedMetaData(SimpleEnumMetaData.class, mmd);

		assertThat(mmd.getOwnerTypeInfo()).isNull();
	}

	@Test
	public void test_EnumConstant_ResolvesDefaultValue_Extended() {
		EnumRelatedMdDescriptor mmd = getMetaData().enumConstant(Color.GREEN).meta(SimpleEnumConstantMetaData.T).exclusiveExtended();
		assertOneDefaultExtendedMetaData(SimpleEnumConstantMetaData.class, mmd);

		assertThat(mmd.getOwnerTypeInfo()).isNull();
	}

	@Test
	public void test_Entity_ResolvesDefaultValue_Extended() {
		EntityRelatedMdDescriptor mmd = getMetaData().entityClass(Teacher.class).meta(SimpleEntityMetaData.T).exclusiveExtended();
		assertOneDefaultExtendedMetaData(SimpleEntityMetaData.class, mmd);

		assertThat(mmd.isInherited()).isFalse();
		assertThat(mmd.getOwnerTypeInfo()).isNull();
	}

	@Test
	public void test_Property_ResolvesDefaultValue_Extended() {
		EntityRelatedMdDescriptor mmd = getMetaData().entityClass(Teacher.class).property("age").meta(SimplePropertyMetaData.T).exclusiveExtended();
		assertOneDefaultExtendedMetaData(SimplePropertyMetaData.class, mmd);

		assertThat(mmd.isInherited()).isFalse();
		assertThat(mmd.getOwnerTypeInfo()).isNull();
	}

	// ########################################
	// ## . . . . . . Assertions . . . . . . ##
	// ########################################

	private <T extends MetaData & ActivableMetaData> void assertOneDefaultExtendedMetaData(Class<T> clazz, MdDescriptor mdd) {
		assertThat(mdd).isNotNull();
		MetaData md = mdd.getResolvedValue();
		assertThat(md).isNotNull().isInstanceOf(clazz);
		assertThat(((ActivableMetaData) md).getActive()).isTrue();
		assertThat(mdd.getResolvedAsDefault()).isTrue();
		assertThat(mdd.origin()).isEqualTo("[default]");
	}

	@Override
	protected Supplier<GmMetaModel> getModelProvider() {
		return new RawModelProvider();
	}

	@Override
	protected void setupCmdResolver(CmdResolverBuilder crb) {
		Set<MetaData> set = new HashSet<MetaData>();

		set.add(newMd(SimpleModelMetaData.T));
		set.add(newMd(SimpleEntityMetaData.T));
		set.add(newMd(SimplePropertyMetaData.T));
		set.add(newMd(SimpleEnumMetaData.T));
		set.add(newMd(SimpleEnumConstantMetaData.T));

		crb.setDefaultMetaData(set);
	}

	private <M extends MetaData & ActivableMetaData> M newMd(EntityType<M> mdEt) {
		return AbstractModelSupplier.newMd(mdEt, true);
	}
}
