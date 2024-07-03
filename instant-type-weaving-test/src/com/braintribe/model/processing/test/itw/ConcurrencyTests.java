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
package com.braintribe.model.processing.test.itw;

import static com.braintribe.model.generic.builder.meta.MetaModelBuilder.metaModel;

import java.util.concurrent.CountDownLatch;

import org.junit.Rule;
import org.junit.Test;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.processing.ImportantItwTestSuperType;
import com.braintribe.model.processing.test.itw.build.GmEntityBuilder;
import com.braintribe.utils.junit.core.rules.ConcurrentRule;

/**
 * 
 */
public class ConcurrencyTests extends ImportantItwTestSuperType {

	private static final int THREADS = 30;

	@Rule
	public ConcurrentRule concurrentRule = new ConcurrentRule(THREADS);

	private final CountDownLatch cdl = new CountDownLatch(THREADS);
	private int counter = 1;

	@Test
	public void testConcurrency() throws Exception {
		GmMetaModel gmMetaModel = getMetaModel();

		cdl.countDown();
		cdl.await();

		gmMetaModel.deploy();
	}

	private GmEntityBuilder sub;
	private GmEntityBuilder supper; // super is keyword...

	private synchronized GmMetaModel getMetaModel() {
		GmEntityBuilder ge = new GmEntityBuilder(GenericEntity.class.getName());
		ge.setIsAbstract(true);

		GmEntityBuilder sge = new GmEntityBuilder(GenericEntity.class.getName() + "Sub");
		sge.setIsAbstract(false).addSuper(ge);

		GmMetaModel metaModel = metaModel();

		int entityId = this.counter++;

		supper = new GmEntityBuilder("com.braintribe.model.processing.test.itw.concurrent.Entity" + entityId);
		sub = new GmEntityBuilder("com.braintribe.model.processing.test.itw.concurrent.SubEntity" + entityId);

		supper.addSuper(sge);
		supper.addSuper(ge);
		supper.addProperty("myself", supper);
		supper.addProperty("sub", sub);

		sub.addSuper(supper.gmEntityType());

		supper.addToMetaModel(metaModel);
		sub.addToMetaModel(metaModel);

		return metaModel;
	}

	protected <T extends GenericEntity> T instantiate(Class<T> beanClass) {
		EntityType<GenericEntity> entityType = GMF.getTypeReflection().getEntityType(beanClass);
		return beanClass.cast(entityType.create());
	}

}
