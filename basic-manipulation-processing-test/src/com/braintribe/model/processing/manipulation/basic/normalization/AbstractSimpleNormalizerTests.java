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
package com.braintribe.model.processing.manipulation.basic.normalization;

import static com.braintribe.utils.lcd.CollectionTools2.newList;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.fest.assertions.Assertions;

import com.braintribe.model.generic.manipulation.AtomicManipulation;
import com.braintribe.model.processing.test.tools.meta.ManipulationTrackingMode;

/**
 * 
 */
public abstract class AbstractSimpleNormalizerTests extends AbstractNormalizerTests {

	@Override
	protected void normalize() {
		List<AtomicManipulation> manipulations = newList(recordedManipulations);
		NormalizationContext context = new NormalizationContext(trackingMode == ManipulationTrackingMode.GLOBAL);
		normalizedManipulations = new SimpleManipulationNormalizer(manipulations, context).normalize();
	}

	// ####################################
	// ## . . . . . Assertions . . . . . ##
	// ####################################

	protected void assertEmpty() {
		Assertions.assertThat(recordedManipulations).isNotNull().isNotEmpty();
		Assertions.assertThat(normalizedManipulations).isNotNull().isEmpty();
	}

	/**
	 * The parameters are the expected positions of the recorded manipulations in the "normaliedManipulations" list. If some manipulation is expected
	 * to be removed during normalization, one indicates this with a negative value. For example, if from the 3 recorded manipulations we expect the
	 * one in the middle to be removed, one would verify that by calling this method with parameters: <code>(0, -1, 1)</code>.
	 */
	protected void assertPositions(int... indices) {
		if (indices.length != recordedManipulations.size()) {
			throw new RuntimeException("WRONG ARRAY OF INDICES. RECORDED MANIPULATIONS: " + recordedManipulations.size()
					+ ", BUT SPECIFIED POSITIONS: " + indices.length);
		}

		Set<AtomicManipulation> normalizedSet = new HashSet<AtomicManipulation>(normalizedManipulations);

		for (int i = 0; i < indices.length; i++) {
			int expectedPosition = indices[i];
			AtomicManipulation recorded = recordedManipulations.get(i);

			if (expectedPosition < 0) {
				Assertions.assertThat(normalizedSet).excludes(recorded);
			} else {
				Assertions.assertThat(normalizedManipulations.get(expectedPosition)).as("Wrong manipulation for index: " + i).isSameAs(recorded);
			}
		}
	}
}
