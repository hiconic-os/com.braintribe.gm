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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.braintribe.model.meta.GmType;
import com.braintribe.model.processing.validation.ValidationContext;
import com.braintribe.model.processing.validation.ValidationMode;
import com.braintribe.model.processing.validation.ValidationType;

public class TypeValidationTaskTest extends AbstractValidationTaskTest {
	
	private GmType type;
	private ValidationContext context;
	
	@Before
	public void prepare() {
		type = TestType.T.create();
		type.setGlobalId("test-global-id");
		type.setDeclaringModel(TestMetaModel.T.create());
		type.setTypeSignature(GmType.T.getTypeSignature());
		
		context = new ValidationContext(ValidationType.SKELETON, ValidationMode.DECLARATION);
	}
	
	@Test
	public void testExecuting() {
		ValidationTask task = new TypeValidationTask(type);
		
		task.execute(context);
		
		assertThat(extractErrorMessages(context)).hasSize(0);
		assertThat(context.getValidationTasks()).hasSize(0);
	}
	
	@Test
	public void testExecuting_MissingGlobalId() {
		ValidationTask task = new TypeValidationTask(type);
		type.setGlobalId(null);
		
		task.execute(context);
		
		assertThat(extractErrorMessages(context)).hasSize(1);
	}
	
	@Test
	public void testExecuting_DeclaringModelMissing() {
		ValidationTask task = new TypeValidationTask(type);
		type.setDeclaringModel(null);
		
		task.execute(context);
		
		assertThat(extractErrorMessages(context)).hasSize(1);
	}
	
	@Test
	public void testExecuting_TypeSignatureMissing() {
		ValidationTask task = new TypeValidationTask(type);
		type.setTypeSignature(null);
		
		task.execute(context);
		
		assertThat(extractErrorMessages(context)).hasSize(1);
	}
	
	@Test
	public void testExecuting_TypeSignatureIsWrong() {
		ValidationTask task = new TypeValidationTask(type);
		type.setTypeSignature("wrong.type.Signature");
		
		task.execute(context);
		
		assertThat(extractErrorMessages(context)).hasSize(1);
	}
}
