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

import com.braintribe.model.meta.GmEnumType;
import com.braintribe.model.processing.validation.ValidationContext;
import com.braintribe.model.processing.validation.ValidationMode;
import com.braintribe.model.processing.validation.ValidationType;

public class DeclaredEnumTypeValidationTaskTest extends AbstractValidationTaskTest {
	
	private GmEnumType type;
	private ValidationContext context;
	
	@Before
	public void prepare() {
		type = TestEnumType.T.create();
		type.setGlobalId("test-global-id");
		TestEnumConstant const1 = TestEnumConstant.T.create();
		const1.setName("const1");
		TestEnumConstant const2 = TestEnumConstant.T.create();
		const2.setName("const2");
		type.setConstants(asList(const1, const2));
		type.setMetaData(asSet(TestMetaData.T.create(), TestMetaData.T.create()));
		type.setEnumConstantMetaData(asSet(TestMetaData.T.create(), TestMetaData.T.create()));
		
		context = new ValidationContext(ValidationType.SKELETON, ValidationMode.DECLARATION);
	}
	
	@Test
	public void testExecuting() {
		ValidationTask task = new DeclaredEnumTypeValidationTask(type);
		
		task.execute(context);
		
		assertThat(extractErrorMessages(context)).hasSize(0);
		assertThat(context.getValidationTasks()).hasSize(6);
		assertThat(context.pollValidationTask()).isInstanceOf(DeclaredConstantValidationTask.class);
		assertThat(context.pollValidationTask()).isInstanceOf(DeclaredConstantValidationTask.class);
		assertThat(context.pollValidationTask()).isInstanceOf(CoreMetaDataValidationTask.class);
		assertThat(context.pollValidationTask()).isInstanceOf(CoreMetaDataValidationTask.class);
		assertThat(context.pollValidationTask()).isInstanceOf(CoreMetaDataValidationTask.class);
		assertThat(context.pollValidationTask()).isInstanceOf(CoreMetaDataValidationTask.class);
	}
	
	@Test
	public void testExecuting_MissingGlobalId() {
		ValidationTask task = new DeclaredEnumTypeValidationTask(type);
		type.setGlobalId(null);
		
		task.execute(context);
		
		assertThat(extractErrorMessages(context)).hasSize(1);
	}
	
	@Test
	public void testExecuting_DuplicateConstants() {
		ValidationTask task = new DeclaredEnumTypeValidationTask(type);
		TestEnumConstant const1 = TestEnumConstant.T.create();
		const1.setName("const1");
		type.getConstants().add(const1);
		
		task.execute(context);
		
		assertThat(extractErrorMessages(context)).hasSize(1);
	}
}
