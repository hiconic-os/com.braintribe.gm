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
package com.braintribe.testing.junit.assertions.gm.assertj.core.api;

import static com.braintribe.testing.junit.assertions.assertj.core.api.Assertions.assertThatExecuting;
import static com.braintribe.testing.junit.assertions.gm.assertj.core.api.GmAssertions.assertThat;

import org.junit.Test;

import com.braintribe.model.resource.Resource;

/**
 * Provides {@link GenericEntityAssert} related tests.
 *
 * @author michael.lafite
 */
public class GenericEntityAssertTest {

	@Test
	public void test() {
		Resource resource = Resource.T.create();
		resource.setCreator("joe");

		assertThat(resource).hasPresentProperty("fileSize");
		assertThatExecuting(() -> assertThat(resource).hasAbsentProperty("fileSize")).fails().with(AssertionError.class);

		assertThat(resource).hasNoSessionAttached();
		assertThatExecuting(() -> assertThat(resource).hasSessionAttached()).fails().with(AssertionError.class);
		
		Resource otherResource = Resource.T.create();
		resource.setCreator("john");
		
		assertThat(resource).hasIdMatchingIdOf(otherResource);
		resource.setId(123);
		otherResource.setId(resource.getId());
		assertThat(resource).hasIdMatchingIdOf(otherResource);
		otherResource.setId(456);
		assertThatExecuting(() -> assertThat(resource).hasIdMatchingIdOf(otherResource)).fails().with(AssertionError.class);
	}

}
