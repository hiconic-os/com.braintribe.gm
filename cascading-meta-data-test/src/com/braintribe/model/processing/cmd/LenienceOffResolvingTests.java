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

import java.util.function.Supplier;

import org.junit.Test;

import com.braintribe.model.generic.manipulation.Manipulation;
import com.braintribe.model.generic.manipulation.ManipulationType;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.meta.data.prompt.Visible;
import com.braintribe.model.processing.cmd.test.model.Color;
import com.braintribe.model.processing.cmd.test.model.Person;
import com.braintribe.model.processing.cmd.test.provider.RawModelProvider;
import com.braintribe.model.processing.meta.cmd.CascadingMetaDataException;

/**
 * 
 */
public class LenienceOffResolvingTests extends MetaDataResolvingTestBase {

	protected static final EntityType<?> nonModelEnityType = Manipulation.T;

	@Test(expected = CascadingMetaDataException.class)
	public void unknownEntityType() {
		getMetaData().entityType(nonModelEnityType).meta(Visible.T).exclusive();
	}

	@Test(expected = CascadingMetaDataException.class)
	public void unknownProperty() {
		getMetaData().entityType(Person.T).property("notExistingProperty").meta(Visible.T).exclusive();
	}

	@Test(expected = CascadingMetaDataException.class)
	public void unknownEnumType() {
		getMetaData().enumClass(ManipulationType.class).meta(Visible.T).exclusive();
	}

	@Test(expected = CascadingMetaDataException.class)
	public void unknownEnumConstant() {
		getMetaData().enumClass(Color.class).constant("nonExistingConstant").meta(Visible.T).exclusive();
	}

	@Override
	protected Supplier<GmMetaModel> getModelProvider() {
		return new RawModelProvider();
	}

}
