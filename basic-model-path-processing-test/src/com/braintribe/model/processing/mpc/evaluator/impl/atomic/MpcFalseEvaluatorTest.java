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
package com.braintribe.model.processing.mpc.evaluator.impl.atomic;

import static com.braintribe.testing.junit.assertions.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.braintribe.model.generic.path.api.IModelPathElement;
import com.braintribe.model.mpc.atomic.MpcFalse;
import com.braintribe.model.mpc.logic.MpcNegation;
import com.braintribe.model.processing.MpGenerator;
import com.braintribe.model.processing.mpc.MPC;
import com.braintribe.model.processing.mpc.evaluator.api.MpcMatch;
import com.braintribe.model.processing.mpc.evaluator.impl.AbstractMpcTest;

/**
 * Provides tests for {@link MpcFalseEvaluator}.
 * 
 */
public class MpcFalseEvaluatorTest extends AbstractMpcTest {

	/**
	 * Validate that a {@link MpcFalse} condition will NOT MATCH against anything 
	 */
	@Test
	public void testFalseMatch() throws Exception {

		IModelPathElement path = MpGenerator.getSimplePath();
		MpcFalse condition = $.falseLiteral();

		// run the matching method
		MpcMatch evaluationResult = MPC.mpcMatches(condition, path);
		
		// validate null result
		assertThat(evaluationResult).isNull();

	}
	
	/**
	 * Validate that a negation of {@link MpcFalse} condition will MATCH against anything and consume one element
	 */
	@Test
	public void testNegationFalseMatch() throws Exception {

		IModelPathElement path = MpGenerator.getSimplePath();

		MpcNegation condition = $.negation($.falseLiteral());
		
		// run the matching method
		MpcMatch evaluationResult = MPC.mpcMatches(condition, path);
		
		// validate that same path has been returned
		assertThat(evaluationResult).isNotNull();
		assertThat(evaluationResult.getPath().getDepth()).isEqualTo(1);

	}

}
