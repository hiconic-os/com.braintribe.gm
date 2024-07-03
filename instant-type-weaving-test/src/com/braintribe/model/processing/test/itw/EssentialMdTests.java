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

import static com.braintribe.model.processing.test.itw.entity.EssentialMdEntity.confidential;
import static com.braintribe.model.processing.test.itw.entity.EssentialMdEntity.regularProperty;
import static com.braintribe.model.processing.test.itw.entity.EssentialMdSiblingEntity.siblingConfidential;
import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.meta.data.prompt.Confidential;
import com.braintribe.model.processing.ImportantItwTestSuperType;
import com.braintribe.model.processing.test.itw.entity.EssentialMdEntity;
import com.braintribe.model.processing.test.itw.entity.EssentialMdSiblingEntity;
import com.braintribe.model.processing.test.itw.entity.EssentialMdSubEntity;

/**
 * Tests for essential {@link MetaData} which are also reflected by GM reflection. Currently only {@link Confidential} is supported.
 */
public class EssentialMdTests extends ImportantItwTestSuperType {

	@Test
	public void propertiesWithEssentialMDs() {
		MetaData.T.isAbstract();

		assertThat(EssentialMdEntity.T.getProperty(regularProperty).isConfidential()).isFalse();

		assertThat(EssentialMdEntity.T.getProperty(confidential).isConfidential()).isTrue();
		assertThat(EssentialMdSubEntity.T.getProperty(confidential).isConfidential()).isTrue();

		assertThat(EssentialMdSiblingEntity.T.getProperty(siblingConfidential).isConfidential()).isTrue();
		assertThat(EssentialMdSubEntity.T.getProperty(siblingConfidential).isConfidential()).isTrue();
	}

	@Test
	public void confidentialNotVisibleInToString() {
		EssentialMdEntity entity = EssentialMdEntity.T.create();
		entity.setConfidential("abcXYZ");

		assertThat(entity.toString()).doesNotContain(entity.getConfidential());

		// sub-type with initializer

		EssentialMdSubEntity subEntity = EssentialMdSubEntity.T.create();

		assertThat(subEntity.getConfidential()).isNotEmpty();
		assertThat(subEntity.toString()).doesNotContain(subEntity.getConfidential());
	}

}
