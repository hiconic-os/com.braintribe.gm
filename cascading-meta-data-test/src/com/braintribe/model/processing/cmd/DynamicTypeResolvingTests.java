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

import static com.braintribe.testing.junit.assertions.gm.assertj.core.api.GmAssertions.assertThat;

import java.util.function.Supplier;

import org.junit.Test;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.proxy.DynamicEntityType;
import com.braintribe.model.generic.proxy.DynamicProperty;
import com.braintribe.model.generic.proxy.ProxyEntity;
import com.braintribe.model.generic.reflection.BaseType;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.meta.data.prompt.Visible;
import com.braintribe.model.processing.cmd.test.provider.RawModelProvider;
import com.braintribe.model.processing.meta.cmd.extended.MdDescriptor;

/**
 * Tests for MD defined on {@link DynamicEntityType} and it's {@link DynamicProperty properties}.
 */
public class DynamicTypeResolvingTests extends MetaDataResolvingTestBase {

	private static final String DYNAMIC_TYPE_SIGNATURE = "com.bt.DynamicType";

	private final DynamicEntityType entityType = new DynamicEntityType(DYNAMIC_TYPE_SIGNATURE);

	// #######################################
	// ## . . . . . Default value . . . . . ##
	// #######################################

	@Test
	public void entityMdForType() {
		entityType.getMetaData().add(Visible.T.create());

		assertVisible(getMetaData().entityType(entityType).meta(Visible.T).exclusive());
	}

	@Test
	public void entityMdForType_Extended() {
		entityType.getMetaData().add(Visible.T.create());

		assertVisibleExtended(getMetaData().entityType(entityType).meta(Visible.T).exclusiveExtended());
	}

	@Test
	public void entityMdForInstance() {
		entityType.getMetaData().add(Visible.T.create());
		ProxyEntity entity = entityType.create();

		assertVisible(getMetaData().entity(entity).meta(Visible.T).exclusive());
	}

	@Test
	public void entityMdForInstance_Extended() {
		entityType.getMetaData().add(Visible.T.create());
		ProxyEntity entity = entityType.create();

		assertVisibleExtended(getMetaData().entity(entity).meta(Visible.T).exclusiveExtended());
	}

	@Test
	public void propertyMd_DefinedOnEntityType() {
		entityType.getPropertyMetaData().add(Visible.T.create());

		assertVisible(getMetaData().entityType(entityType).property(GenericEntity.globalId).meta(Visible.T).exclusive());
	}

	@Test
	public void propertyMd_DefinedOnEntityType_Extended() {
		entityType.getPropertyMetaData().add(Visible.T.create());

		assertVisibleExtended(getMetaData().entityType(entityType).property(GenericEntity.globalId).meta(Visible.T).exclusiveExtended());
	}

	@Test
	public void propertyMd_DefinedOnProperty() {
		String propertyName = "dynamicProperty";

		DynamicProperty property = entityType.addProperty(propertyName, BaseType.INSTANCE);
		property.getMetaData().add(Visible.T.create());

		assertVisible(getMetaData().entityType(entityType).property(propertyName).meta(Visible.T).exclusive());
	}

	@Test
	public void propertyMd_DefinedOnProperty_Extended() {
		String propertyName = "dynamicProperty";

		DynamicProperty property = entityType.addProperty(propertyName, BaseType.INSTANCE);
		property.getMetaData().add(Visible.T.create());

		assertVisibleExtended(getMetaData().entityType(entityType).property(propertyName).meta(Visible.T).exclusiveExtended());
	}

	private void assertVisible(MetaData md) {
		assertThat(md).isExactly(Visible.T);
	}

	private void assertVisibleExtended(MdDescriptor md) {
		assertThat(md.getResolvedValue()).isExactly(Visible.T);
		assertThat(md.getOwnerModel()).isNull();
	}

	@Override
	protected Supplier<GmMetaModel> getModelProvider() {
		return new RawModelProvider();
	}

}
