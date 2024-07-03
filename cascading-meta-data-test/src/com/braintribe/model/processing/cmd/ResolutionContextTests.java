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

import static com.braintribe.utils.lcd.CollectionTools2.newList;

import java.util.List;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;

import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.processing.cmd.test.meta.entity.SimpleEntityMetaData;
import com.braintribe.model.processing.cmd.test.meta.enumeration.SimpleEnumConstantMetaData;
import com.braintribe.model.processing.cmd.test.meta.enumeration.SimpleEnumMetaData;
import com.braintribe.model.processing.cmd.test.meta.model.SimpleModelMetaData;
import com.braintribe.model.processing.cmd.test.meta.property.SimplePropertyMetaData;
import com.braintribe.model.processing.cmd.test.meta.selector.AspectCheckingSelector;
import com.braintribe.model.processing.cmd.test.meta.selector.AspectCheckingSelectorExpert;
import com.braintribe.model.processing.cmd.test.model.Color;
import com.braintribe.model.processing.cmd.test.model.Person;
import com.braintribe.model.processing.cmd.test.provider.ResolutionContextMdProvider;
import com.braintribe.model.processing.meta.cmd.CmdResolverBuilder;
import com.braintribe.model.processing.meta.cmd.context.SelectorContextAspect;
import com.braintribe.model.processing.meta.cmd.context.aspects.EntityAspect;
import com.braintribe.model.processing.meta.cmd.context.aspects.EntityTypeAspect;
import com.braintribe.model.processing.meta.cmd.context.aspects.GmEntityTypeAspect;
import com.braintribe.model.processing.meta.cmd.context.aspects.GmEnumTypeAspect;
import com.braintribe.model.processing.meta.cmd.context.aspects.GmPropertyAspect;

/**
 * Here we are testing that the right aspects are being put to the resolution context.
 * 
 * Every test first creates a list of expected aspects and then does a resolution for simple meta-data, defined in
 * {@link ResolutionContextMdProvider}. The expert ( {@link AspectCheckingSelectorExpert}) then checks that all expected aspects are part of the
 * context, if you, it returns <tt>true</tt>, otherwise it throws an exception.
 * 
 * @see ResolutionContextMdProvider
 * @see AspectCheckingSelector
 * @see AspectCheckingSelectorExpert
 */
public class ResolutionContextTests extends MetaDataResolvingTestBase {

	private static List<Class<? extends SelectorContextAspect<?>>> expectedAspects = newList();
	private static AspectCheckingSelectorExpert selectorExpert = new AspectCheckingSelectorExpert(expectedAspects);

	@Before
	public void clearExpectedAspects() {
		expectedAspects.clear();
	}

	@Test
	public void model() {
		MetaData md = getMetaData().meta(SimpleModelMetaData.T).exclusive();
		assertOneMetaData(SimpleModelMetaData.T, md);
	}

	@Test
	public void entityType() {
		expectedAspects.add(GmEntityTypeAspect.class);

		MetaData md = getMetaData().entityClass(Person.class).meta(SimpleEntityMetaData.T).exclusive();
		assertOneMetaData(SimpleEntityMetaData.T, md);
	}

	@Test
	public void entity() {
		expectedAspects.add(GmEntityTypeAspect.class);

		expectedAspects.add(EntityAspect.class);
		expectedAspects.add(EntityTypeAspect.class);

		MetaData md = getMetaData().entity(Person.T.create()).meta(SimpleEntityMetaData.T).exclusive();
		assertOneMetaData(SimpleEntityMetaData.T, md);
	}

	@Test
	public void property() {
		expectedAspects.add(GmEntityTypeAspect.class);
		expectedAspects.add(GmPropertyAspect.class);

		MetaData md = getMetaData().entityClass(Person.class).property("name").meta(SimplePropertyMetaData.T).exclusive();
		assertOneMetaData(SimplePropertyMetaData.T, md);
	}

	@Test
	public void enumeration() {
		expectedAspects.add(GmEnumTypeAspect.class);

		MetaData md = getMetaData().enumClass(Color.class).meta(SimpleEnumMetaData.T).exclusive();
		assertOneMetaData(SimpleEnumMetaData.T, md);
	}

	@Test
	public void enumConstant() {
		expectedAspects.add(GmEnumTypeAspect.class);

		MetaData md = getMetaData().enumConstant(Color.GREEN).meta(SimpleEnumConstantMetaData.T).exclusive();
		assertOneMetaData(SimpleEnumConstantMetaData.T, md);
	}

	@Override
	protected Supplier<GmMetaModel> getModelProvider() {
		return new ResolutionContextMdProvider();
	}

	@Override
	protected void setupCmdResolver(CmdResolverBuilder crb) {
		crb.addExpert(AspectCheckingSelector.T, selectorExpert);
	}

}
