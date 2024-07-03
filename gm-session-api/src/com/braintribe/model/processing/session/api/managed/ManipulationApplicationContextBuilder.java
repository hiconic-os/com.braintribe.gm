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
package com.braintribe.model.processing.session.api.managed;

import com.braintribe.model.generic.manipulation.Manipulation;
import com.braintribe.model.generic.session.exception.GmSessionException;

/**
 * A fluent builder for setting various parameters for the manipulation application process.
 */
public interface ManipulationApplicationContextBuilder {

	/** Triggers the actual application of the manipulation. */
	ManipulationReport apply(Manipulation manipulation) throws GmSessionException;

	ManipulationApplicationContext context();
	
	/**
	 * Determines what type of manipulations will be provided to the {@link #apply(Manipulation)} method.
	 */
	ManipulationApplicationContextBuilder mode(ManipulationMode mode);
	
	/**
	 * Tells whether manipulations should be treated lenient (silently ignored instead of generating exception)
	 */
	ManipulationApplicationContextBuilder lenience(ManipulationLenience lenience);

}
