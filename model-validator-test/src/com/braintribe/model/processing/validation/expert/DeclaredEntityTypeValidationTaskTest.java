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
package com.braintribe.model.processing.validation.expert;

import static com.braintribe.utils.lcd.CollectionTools2.asList;
import static com.braintribe.utils.lcd.CollectionTools2.asSet;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.braintribe.model.meta.GmEntityType;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.processing.validation.ValidationContext;
import com.braintribe.model.processing.validation.ValidationMode;
import com.braintribe.model.processing.validation.ValidationType;

public class DeclaredEntityTypeValidationTaskTest extends AbstractValidationTaskTest {
	
	private GmMetaModel model;
	private GmEntityType type;
	private ValidationContext context;
	
	@Before
	public void prepare() {
		type = TestEntityType.T.create();
		type.setGlobalId("test-global-id");
		TestProperty prop1 = TestProperty.T.create();
		prop1.setName("prop1");
		TestProperty prop2 = TestProperty.T.create();
		prop2.setName("prop2");
		type.setProperties(asList(prop1, prop2));
		type.setEvaluatesTo(TestEntityType.T.create());
		type.setMetaData(asSet(TestMetaData.T.create(), TestMetaData.T.create()));
		type.setPropertyMetaData(asSet(TestMetaData.T.create(), TestMetaData.T.create()));
		type.setPropertyOverrides(asList(TestPropertyOverride.T.create(), TestPropertyOverride.T.create()));
		type.setSuperTypes(asList(TestEntityType.T.create(), TestEntityType.T.create()));
		
		model = TestMetaModel.T.create();
		type.setDeclaringModel(model);
		
		context = new ValidationContext(ValidationType.SKELETON, ValidationMode.DECLARATION);
	}
	
	@Test
	public void testExecuting() {
		ValidationTask task = new DeclaredEntityTypeValidationTask(model, type);
		
		task.execute(context);
		
		assertThat(extractErrorMessages(context)).hasSize(0);
		assertThat(context.getValidationTasks()).hasSize(11);
		assertThat(context.pollValidationTask()).isInstanceOf(DeclaredPropertyValidationTask.class);
		assertThat(context.pollValidationTask()).isInstanceOf(DeclaredPropertyValidationTask.class);
		assertThat(context.pollValidationTask()).isInstanceOf(ReferencedTypeValidationTask.class);
		assertThat(context.pollValidationTask()).isInstanceOf(CoreMetaDataValidationTask.class);
		assertThat(context.pollValidationTask()).isInstanceOf(CoreMetaDataValidationTask.class);
		assertThat(context.pollValidationTask()).isInstanceOf(CoreMetaDataValidationTask.class);
		assertThat(context.pollValidationTask()).isInstanceOf(CoreMetaDataValidationTask.class);
		assertThat(context.pollValidationTask()).isInstanceOf(DeclaredPropertyOverrideValidationTask.class);
		assertThat(context.pollValidationTask()).isInstanceOf(DeclaredPropertyOverrideValidationTask.class);
		assertThat(context.pollValidationTask()).isInstanceOf(ReferencedTypeValidationTask.class);
		assertThat(context.pollValidationTask()).isInstanceOf(ReferencedTypeValidationTask.class);
	}
	
	@Test
	public void testExecuting_MissingGlobalId() {
		ValidationTask task = new DeclaredEntityTypeValidationTask(model, type);
		type.setGlobalId(null);
		
		task.execute(context);
		
		assertThat(extractErrorMessages(context)).hasSize(1);
	}
	
	@Test
	public void testExecuting_DuplicateProperties() {
		ValidationTask task = new DeclaredEntityTypeValidationTask(model, type);
		TestProperty prop1 = TestProperty.T.create();
		prop1.setName("prop1");
		type.getProperties().add(prop1);
		
		task.execute(context);
		
		assertThat(extractErrorMessages(context)).hasSize(1);
	}
	
	@Test
	public void testExecuting_NoSuperTypes() {
		ValidationTask task = new DeclaredEntityTypeValidationTask(model, type);
		type.getSuperTypes().clear();
		
		task.execute(context);
		
		assertThat(extractErrorMessages(context)).hasSize(1);
	}
}
