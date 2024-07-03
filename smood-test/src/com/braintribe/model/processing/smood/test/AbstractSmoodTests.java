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
package com.braintribe.model.processing.smood.test;

import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.junit.Before;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelTypeReflection;
import com.braintribe.model.generic.value.EntityReference;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.processing.query.test.builder.DataBuilder;
import com.braintribe.model.processing.query.test.model.MetaModelProvider;
import com.braintribe.model.processing.smood.Smood;
import com.braintribe.utils.lcd.CollectionTools2;

/**
 * 
 */
public abstract class AbstractSmoodTests {

	protected static final GenericModelTypeReflection typeReflection = GMF.getTypeReflection();

	protected DataBuilder b;
	protected Smood smood;

	@Before
	public void setup() {
		smood = new Smood(new ReentrantReadWriteLock());
		setSmoodMetaModel();

		b = newBuilder();

		postConstruct();
	}

	protected void setSmoodMetaModel() {
		smood.setMetaModel(provideEnrichedMetaModel());
	}

	protected GmMetaModel provideEnrichedMetaModel() {
		return MetaModelProvider.provideEnrichedModel();
	}

	protected void postConstruct() {
		// intentionally left blank, to be implemented by sub-types if needed
	}

	protected DataBuilder newBuilder() {
		return new DataBuilder(smood);
	}

	protected void registerAtSmood(GenericEntity... entities) {
		registerAtSmood(true, entities);
	}

	protected void registerAtSmood(boolean generateId, GenericEntity... entities) {
		for (GenericEntity entity : entities)
			smood.registerEntity(entity, generateId);
	}

	protected EntityReference entityReference(GenericEntity entity) {
		return entity.reference();
	}

	protected <T extends GenericEntity> EntityType<T> entityType(T entity) {
		return entity.entityType();
	}

	protected <K, V> Map<K, V> asMap(Object... objs) {
		return CollectionTools2.asMap(objs);
	}

}
