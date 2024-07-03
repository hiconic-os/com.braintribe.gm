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
package com.braintribe.model.processing.mpc.evaluator.impl.logic;

import static com.braintribe.testing.junit.assertions.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.braintribe.model.generic.path.api.IModelPathElement;
import com.braintribe.model.generic.path.api.ModelPathElementType;
import com.braintribe.model.mpc.logic.MpcNegation;
import com.braintribe.model.processing.MpGenerator;
import com.braintribe.model.processing.mpc.MPC;
import com.braintribe.model.processing.mpc.evaluator.api.MpcMatch;
import com.braintribe.model.processing.mpc.evaluator.impl.AbstractMpcTest;

/**
 * Provides tests for {@link MpcNegationEvaluator}.
 * 
 */
public class MpcNegationEvaluatorTest extends AbstractMpcTest {

	/**
	 * Validate that a {@link MpcNegation} condition of {@link $#negationCondition()} which will MATCH against a
	 * {@link IModelPathElement} which consists of "Root.name" ( {@link MpGenerator#getSimplePath()}).
	 */
	@Test
	public void testSimpleNegationMatch() throws Exception {

		IModelPathElement path = MpGenerator.getSimplePath();

		MpcNegation condition = $.negationCondition();

		// evaluate the condition against the path
		MpcMatch evalResult = MPC.mpcMatches(condition, path);

		// validate output
		assertThat(evalResult).isNull();
	}

	/**
	 * Validate that a {@link MpcNegation} condition of {@link $#negationCondition()} which will NOT MATCH against a
	 * {@link IModelPathElement} which consists of "Root.favouriteColours[1]" ( {@link MpGenerator#getTernaryPath()}).
	 */
	@Test
	public void testSimpleNegationNoMatch() throws Exception {

		IModelPathElement path = MpGenerator.getTernaryPath();

		MpcNegation condition = $.negationCondition();

		// evaluate the condition against the path
		MpcMatch evalResult = MPC.mpcMatches(condition, path);

		assertThat(evalResult).isNotNull();
		assertThat(evalResult.getPath()).isNotNull();
		// check the path consists of root and another element
		assertThat(evalResult.getPath().getDepth()).isEqualTo(2);
		// validate that the current element is indeed a list item
		assertThat(evalResult.getPath().getElementType()).isEqualTo(ModelPathElementType.ListItem);
		// validate that the previous element is indeed property
		assertThat(evalResult.getPath().getPrevious().getElementType()).isEqualTo(ModelPathElementType.Property);
		// validate that the first element is indeed the root
		assertThat(evalResult.getPath().getPrevious().getPrevious().getElementType()).isEqualTo(ModelPathElementType.Root);

	}

}
