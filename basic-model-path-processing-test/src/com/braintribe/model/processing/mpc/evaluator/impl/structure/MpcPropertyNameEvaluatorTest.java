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
package com.braintribe.model.processing.mpc.evaluator.impl.structure;

import static com.braintribe.testing.junit.assertions.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.braintribe.model.generic.path.api.IModelPathElement;
import com.braintribe.model.generic.path.api.ModelPathElementType;
import com.braintribe.model.mpc.structure.MpcPropertyName;
import com.braintribe.model.processing.MpGenerator;
import com.braintribe.model.processing.mpc.MPC;
import com.braintribe.model.processing.mpc.evaluator.api.MpcMatch;
import com.braintribe.model.processing.mpc.evaluator.impl.AbstractMpcTest;

/**
 * Provides tests for {@link MpcPropertyNameEvaluator}.
 * 
 */
public class MpcPropertyNameEvaluatorTest extends AbstractMpcTest {

	/**
	 * Validate that a {@link MpcPropertyName} condition where it is set to "name" will MATCH against a
	 * {@link IModelPathElement} which consists of "Root.name" ( {@link MpGenerator#getSimplePath()}).
	 */
	@Test
	public void testMatchingPropertyName() throws Exception {

		IModelPathElement path = MpGenerator.getSimplePath();

		MpcPropertyName condition = $.propertyNameCondition("name");

		// evaluate the condition against the path
		MpcMatch evalResult = MPC.mpcMatches(condition, path);

		// validate output
		assertThat(evalResult).isNotNull();
		// validate that the result is one item shorter
		assertThat(evalResult.getPath().getDepth()).isEqualTo(0);
		// validate that the current element is indeed the root
		assertThat(evalResult.getPath().getElementType()).isEqualTo(ModelPathElementType.Root);
	}

	/**
	 * Validate that a {@link MpcPropertyName} condition where it is set to "notName" will NOT MATCH against a
	 * {@link IModelPathElement} which consists of "Root.name" ( {@link MpGenerator#getSimplePath()}).
	 */
	@Test
	public void testNotMatchingPropertyName() throws Exception {

		IModelPathElement path = MpGenerator.getSimplePath();

		MpcPropertyName condition = $.propertyNameCondition("notName");

		// evaluate the condition against the path
		MpcMatch evalResult = MPC.mpcMatches(condition, path);

		// validate output
		assertThat(evalResult).isNull();

	}

}
