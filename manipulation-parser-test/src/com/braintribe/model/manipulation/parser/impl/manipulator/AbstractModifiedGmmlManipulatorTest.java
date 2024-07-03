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
package com.braintribe.model.manipulation.parser.impl.manipulator;

import java.util.function.Function;

import com.braintribe.model.processing.manipulation.parser.api.GmmlManipulatorErrorHandler;
import com.braintribe.model.processing.manipulation.parser.impl.listener.error.LenientErrorHandler;

/**
 * To test the lenient mode, we record the manipulations, stringify them, modify the string with {@link #gmmlModifier}, and then apply the modified
 * string.
 * 
 * Ever test has the following structure:
 * <ul>
 * <li>Set the {@link #gmmlModifier} to cause some problem with the manipulations</li>
 * <li>Run the process (see {@link #recordStringifyAndApply}), which also applies the modifier set in previous step.</li>
 * <li>Assert that the result is correct.</li>
 * </ul>
 * 
 * @author peter.gazdik
 */
public abstract class AbstractModifiedGmmlManipulatorTest extends AbstractManipulatorTest {

	protected Function<String, String> gmmlModifier;

	@Override
	protected String processManipulationString(String string) {
		String s = gmmlModifier.apply(string);
		return s;
	}

	@Override
	protected GmmlManipulatorErrorHandler errorHandler() {
		return LenientErrorHandler.INSTANCE;
	}

}
