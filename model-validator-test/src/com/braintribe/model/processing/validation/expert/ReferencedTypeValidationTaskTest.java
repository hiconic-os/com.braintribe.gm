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

import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.meta.GmType;
import com.braintribe.model.processing.validation.ValidationContext;
import com.braintribe.model.processing.validation.ValidationMode;
import com.braintribe.model.processing.validation.ValidationType;

public class ReferencedTypeValidationTaskTest extends AbstractValidationTaskTest {
	
	private GmMetaModel declaringModel;
	private GmType type;
	private ValidationContext context;
	
	@Before
	public void prepare() {
		declaringModel = TestMetaModel.T.create();
		type = TestType.T.create();
		declaringModel.setTypes(asSet(type));
		type.setDeclaringModel(declaringModel);
		
		context = new ValidationContext(ValidationType.SKELETON, ValidationMode.DECLARATION);
	}
	
	@Test
	public void testExecuting_DeclaringModelIsCurrentModel() {
		ValidationTask task = new ReferencedTypeValidationTask(declaringModel, type);
		
		task.execute(context);
		
		assertThat(extractErrorMessages(context)).hasSize(0);
		assertThat(context.getValidationTasks()).hasSize(1);
		assertThat(context.pollValidationTask()).isInstanceOf(TypeValidationTask.class);
	}
	
	@Test
	public void testExecuting_DeclaringModelIsCurrentModelsDependency() {
		ValidationTask task = new ReferencedTypeValidationTask(declaringModel, type);
		TestMetaModel depDeclaringModel = TestMetaModel.T.create();
		type.setDeclaringModel(depDeclaringModel);
		declaringModel.setDependencies(asList(depDeclaringModel));
		
		task.execute(context);
		
		assertThat(extractErrorMessages(context)).hasSize(0);
		assertThat(context.getValidationTasks()).hasSize(1);
		assertThat(context.pollValidationTask()).isInstanceOf(TypeValidationTask.class);
	}
	
	@Test
	public void testExecuting_DeclaringModelIsCurrentModel_TypeMissing() {
		ValidationTask task = new ReferencedTypeValidationTask(declaringModel, type);
		declaringModel.setTypes(asSet());
		
		task.execute(context);
		
		assertThat(extractErrorMessages(context)).hasSize(1);
	}
	
	@Test
	public void testExecuting_DeclaringModelIsCurrentModelsDependency_MissingDependency() {
		ValidationTask task = new ReferencedTypeValidationTask(declaringModel, type);
		TestMetaModel depDeclaringModel = TestMetaModel.T.create();
		type.setDeclaringModel(depDeclaringModel);
		
		task.execute(context);
		
		assertThat(extractErrorMessages(context)).hasSize(1);
	}
}
