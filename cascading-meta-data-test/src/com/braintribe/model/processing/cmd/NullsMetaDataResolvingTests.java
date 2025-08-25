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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.Supplier;

import org.junit.Test;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.processing.cmd.test.meta.entity.SimpleEntityMetaData;
import com.braintribe.model.processing.cmd.test.meta.enumeration.SimpleEnumConstantMetaData;
import com.braintribe.model.processing.cmd.test.meta.enumeration.SimpleEnumMetaData;
import com.braintribe.model.processing.cmd.test.meta.property.SimplePropertyMetaData;
import com.braintribe.model.processing.cmd.test.model.Color;
import com.braintribe.model.processing.cmd.test.provider.NullsMdProvider;

/**
 * Testing leniency - if any {@code Set<MetaData>} property contains null, CMD treats it as if it wasn't there.
 * <p>
 * There was a problem where this would lead to an NPE while resolving the MD.
 */
public class NullsMetaDataResolvingTests extends MetaDataResolvingTestBase {

	/** @see NullsMdProvider#addMetaData */
	@Test
	public void nullsAreIgnored() {
		MetaData md;
		md = getMetaData().enumClass(Color.class).meta(SimpleEnumMetaData.T).exclusive();
		assertThat(md).isNull();

		md = getMetaData().enumClass(Color.class).meta(SimpleEnumConstantMetaData.T).exclusive();
		assertThat(md).isNull();

		md = getMetaData().entityType(GenericEntity.T).meta(SimpleEntityMetaData.T).exclusive();
		assertThat(md).isNull();

		md = getMetaData().entityType(GenericEntity.T).property("id").meta(SimplePropertyMetaData.T).exclusive();
		assertThat(md).isNull();
	}

	@Override
	protected Supplier<GmMetaModel> getModelProvider() {
		return new NullsMdProvider();
	}

}
