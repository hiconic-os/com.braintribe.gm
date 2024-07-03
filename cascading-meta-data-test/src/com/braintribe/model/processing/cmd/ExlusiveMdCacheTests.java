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

import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.meta.data.EntityTypeMetaData;
import com.braintribe.model.processing.cmd.test.meta.entity.SimpleEntityMetaData;
import com.braintribe.model.processing.cmd.test.meta.entity.SimpleInheritedMetaData;
import com.braintribe.model.processing.cmd.test.meta.selector.UnreachableSelector;
import com.braintribe.model.processing.cmd.test.meta.selector.UnreachableSelectorExpert;
import com.braintribe.model.processing.cmd.test.model.Teacher;
import com.braintribe.model.processing.cmd.test.provider.ExclusiveMdProvider;
import com.braintribe.model.processing.meta.cmd.CascadingMetaDataException;
import com.braintribe.model.processing.meta.cmd.CmdResolverBuilder;

/**
 * Tests whether the caching works the right way when resolving exclusive meta-data.
 */
public class ExlusiveMdCacheTests extends MetaDataResolvingTestBase {

	/**
	 * This just shows our {@link UnreachableSelector} and {@link UnreachableSelectorExpert} are setup properly, i.e. that any resolution that tries
	 * to resolve those ends up with an exception.
	 * 
	 * @see ExclusiveMdProvider#addSimpleExclusiveMd()
	 */
	@Test(expected = CascadingMetaDataException.class)
	public void showProperTestSetup() {
		getMetaData().entityType(Teacher.T).meta(SimpleEntityMetaData.T).list();
	}

	/** @see ExclusiveMdProvider#addSimpleExclusiveMd() */
	@Test
	public void test_Entity_StopsOnFirstStaticWithoutLookingToSuper() {
		EntityTypeMetaData md = getMetaData().entityClass(Teacher.class).meta(SimpleEntityMetaData.T).exclusive();
		assertOneMetaData(SimpleEntityMetaData.T, md);
	}

	/** @see ExclusiveMdProvider#addInheritedExclusiveMd() */
	@Test
	public void test_Entity_StopsInFirstSuperWithoutGoingToSecond() {
		EntityTypeMetaData md = getMetaData().entityClass(Teacher.class).meta(SimpleInheritedMetaData.T).exclusive();
		assertOneMetaData(SimpleInheritedMetaData.T, md);
	}

	@Override
	protected Supplier<GmMetaModel> getModelProvider() {
		return new ExclusiveMdProvider();
	}

	@Override
	protected void setupCmdResolver(CmdResolverBuilder crb) {
		crb.addExpert(UnreachableSelector.T, new UnreachableSelectorExpert());
	}

}
