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

import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.meta.data.mapping.Alias;
import com.braintribe.model.processing.validation.ValidationContext;
import com.braintribe.model.processing.validation.ValidationMode;
import com.braintribe.model.processing.validation.ValidationType;

public class CoreMetaDataValidationTaskTest extends AbstractValidationTaskTest {

	private ValidationContext context;

	@Before
	public void prepare() {
		context = new ValidationContext(ValidationType.SKELETON, ValidationMode.DECLARATION);
	}

	@Test
	public void testExecuting() {
		MetaData metaData = Alias.T.create();
		metaData.setGlobalId("test-global-id");
		ValidationTask task = new CoreMetaDataValidationTask(metaData);

		task.execute(context);

		assertThat(extractErrorMessages(context)).hasSize(0);
		assertThat(context.getValidationTasks()).hasSize(0);
	}

	@Test
	public void testExecuting_MissingGlobalId() {
		MetaData metaData = Alias.T.create();
		metaData.setGlobalId(null);
		ValidationTask task = new CoreMetaDataValidationTask(metaData);

		task.execute(context);

		assertThat(extractErrorMessages(context)).hasSize(1);
	}

	@Test
	public void testExecuting_NotAllowedMetaData() {
		MetaData metaData = TestMetaData.T.create();
		metaData.setGlobalId("test-global-id");
		ValidationTask task = new CoreMetaDataValidationTask(metaData);

		task.execute(context);

		assertThat(extractErrorMessages(context)).hasSize(1);
	}

	@Test
	public void testExecuting_PresentMetaDataSelector() {
		MetaData metaData = Alias.T.create();
		metaData.setGlobalId("test-global-id");
		metaData.setSelector(TestMetaDataSelector.T.create());
		ValidationTask task = new CoreMetaDataValidationTask(metaData);

		task.execute(context);

		assertThat(extractErrorMessages(context)).hasSize(1);
	}
}
