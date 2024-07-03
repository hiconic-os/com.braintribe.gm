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
import com.braintribe.model.mpc.atomic.MpcTrue;
import com.braintribe.model.processing.MpGenerator;
import com.braintribe.model.processing.mpc.MPC;
import com.braintribe.model.processing.mpc.evaluator.api.MpcMatch;
import com.braintribe.model.processing.mpc.evaluator.impl.AbstractMpcTest;

/**
 * Provides tests for {@link MpcTrueEvaluator}.
 * 
 */
public class MpcTrueEvaluatorTest extends AbstractMpcTest {

	/**
	 * Validate that a {@link MpcTrue} condition will MATCH against anything and consume one element
	 */
	@Test
	public void testTrueCaptureMatch() throws Exception {

		IModelPathElement path = MpGenerator.getSimplePath();
		MpcTrue condition = $.trueLiteral(false);

		// run the matching method
		MpcMatch evaluationResult = MPC.mpcMatches(condition, path);
		
		// validate path has been consumed
		assertThat(evaluationResult).isNotNull();
		assertThat(evaluationResult.getPath().getDepth()).isEqualTo(0);

	}
	
	/**
	 * Validate that a {@link MpcTrue} condition will MATCH against anything and consume nothing
	 */
	@Test
	public void testTrueNoCaptureMatch() throws Exception {

		IModelPathElement path = MpGenerator.getSimplePath();
		MpcTrue condition = $.trueLiteral(true);

		// run the matching method
		MpcMatch evaluationResult = MPC.mpcMatches(condition, path);
		
		// validate path has not been consumed
		assertThat(evaluationResult).isNotNull();
		assertThat(evaluationResult.getPath().getDepth()).isEqualTo(1);

	}
	
	/**
	 * Validate that a {@link MpcTrue} condition will MATCH against null path
	 */
	@Test
	public void testNullMatch() throws Exception {

		MpcTrue condition = $.trueLiteral(false);

		// run the matching method
		MpcMatch evaluationResult = MPC.mpcMatches(condition, null);
		
		// validate there is a match with path null
		assertThat(evaluationResult).isNotNull();
		assertThat(evaluationResult.getPath()).isNull();

	}

}
