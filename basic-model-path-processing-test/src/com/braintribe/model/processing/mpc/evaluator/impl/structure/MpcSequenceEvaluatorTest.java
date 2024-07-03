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

import com.braintribe.model.bvd.predicate.InstanceOf;
import com.braintribe.model.generic.path.api.IModelPathElement;
import com.braintribe.model.mpc.atomic.MpcTrue;
import com.braintribe.model.mpc.structure.MpcElementType;
import com.braintribe.model.mpc.structure.MpcSequence;
import com.braintribe.model.mpc.value.MpcElementAxis;
import com.braintribe.model.path.GmModelPathElementType;
import com.braintribe.model.processing.MpGenerator;
import com.braintribe.model.processing.misc.model.Person;
import com.braintribe.model.processing.mpc.MPC;
import com.braintribe.model.processing.mpc.evaluator.api.MpcMatch;
import com.braintribe.model.processing.mpc.evaluator.impl.AbstractMpcTest;

/**
 * Provides tests for {@link MpcSequenceEvaluator}.
 * 
 */
public class MpcSequenceEvaluatorTest extends AbstractMpcTest {

	/**
	 * Validate that a {@link MpcSequence} condition with CAPTURE where there are two sequences that will MATCH against
	 * a {@link IModelPathElement} which consists of "Root.name" ( {@link MpGenerator#getSimplePath()}).
	 */
	@Test
	public void testMatchingSequenceWithCapture() throws Exception {

		IModelPathElement path = MpGenerator.getSimplePath();

		MpcElementType firstSequence = $.elementTypeCondition(GmModelPathElementType.Property);

		InstanceOf secondSequence = $.instanceOf(MpcElementAxis.value, Person.class.getName());

		MpcSequence condition = $.sequence(false, secondSequence, firstSequence);

		// run the matching method
		MpcMatch evaluationResult = MPC.mpcMatches(condition, path);

		// validate output
		assertThat(evaluationResult).isNotNull();
		// validate that the path has been consumed up to the root
		assertThat(evaluationResult.getPath()).isNull();
	}

	/**
	 * Validate that a {@link MpcSequence} condition with NO CAPTURE where there are two sequences that will MATCH
	 * against a {@link IModelPathElement} which consists of "Root.name" ( {@link MpGenerator#getSimplePath()}).
	 */
	@Test
	public void testMatchingSequenceWithNoCapture() throws Exception {

		IModelPathElement path = MpGenerator.getSimplePath();

		MpcElementType firstSequence = $.elementTypeCondition(GmModelPathElementType.Property);

		InstanceOf secondSequence = $.instanceOf(MpcElementAxis.value, Person.class.getName());

		MpcSequence condition = $.sequence(true,  secondSequence, firstSequence);

		// run the matching method
		MpcMatch evaluationResult = MPC.mpcMatches(condition, path);

		// validate output
		assertThat(evaluationResult).isNotNull();
		// validate that the path has not been consumed
		assertThat(evaluationResult.getPath()).isNotNull();
		assertThat(evaluationResult.getPath().getDepth()).isEqualTo(1);
	}

	/**
	 * Validate that a {@link MpcSequence} condition where there are two sequences that will NOT MATCH
	 * against a {@link IModelPathElement} which consists of "Root.name" ( {@link MpGenerator#getSimplePath()}).
	 */
	@Test
	public void testNotMatchingSequence() throws Exception {

		IModelPathElement path = MpGenerator.getSimplePath();

		MpcElementType firstSequence = $.elementTypeCondition(GmModelPathElementType.Property);

		InstanceOf secondSequence = $.instanceOf(MpcElementAxis.value, Person.class.getName());

		MpcSequence condition = $.sequence(false, firstSequence, secondSequence);

		// run the matching method
		MpcMatch evaluationResult = MPC.mpcMatches(condition, path);

		// validate that there was no match
		assertThat(evaluationResult).isNull();
	}
	
	
	/**
	 * Validate that a {@link MpcSequence} condition where there are two sequences that will NOT MATCH
	 * against a {@link IModelPathElement} which consists of "Root" ( {@link MpGenerator#getSimplePath()}).
	 */
	@Test
	public void testNotMatchingShortPathSequence() throws Exception {

		IModelPathElement path = MpGenerator.getSimplePath();

		path = path.getPrevious();

		MpcTrue firstSequence = $.trueLiteral(false);

		MpcSequence condition = $.sequence(false, firstSequence, firstSequence);

		// run the matching method
		MpcMatch evaluationResult = MPC.mpcMatches(condition, path);

		// validate that there was no match
		assertThat(evaluationResult).isNull();
	}
}
