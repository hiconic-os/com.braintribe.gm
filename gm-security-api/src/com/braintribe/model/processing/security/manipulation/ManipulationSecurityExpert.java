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
package com.braintribe.model.processing.security.manipulation;

import com.braintribe.model.generic.manipulation.Manipulation;
import com.braintribe.model.processing.security.SecurityAspectException;
import com.braintribe.model.processing.security.query.expert.AccessSecurityExpert;

/**
 * An expert that is responsible for some aspect of the {@link Manipulation} related security. The validation process
 * goes as follows:
 * <p>
 * First the expert is asked to create it's own context object via the
 * {@link #createExpertContext(ManipulationSecurityContext)} method. The expert will later have access to this object
 * via {@link ManipulationSecurityContext}. This is just a technical detail, since there is only one instance of a given
 * expert, so this makes sure multiple concurrent usages are easily manageable.
 * <p>
 * The second part is the exposition of the manipulations to the expert, where the
 * {@link #expose(ManipulationSecurityExpositionContext)} method is being invoked for each manipulation. The information coming
 * from this manipulation, plus all other information, are accessible via {@link ManipulationSecurityExpositionContext}.
 * <p>
 * After the exposition part is finished, the expert is invoked one more time, via it's
 * {@link #validate(ManipulationSecurityContext)} method. This method is basically a notification for the expert, that
 * all the manipulations were already exposed, so it may finish the validation now. (Of course, the expert can be
 * validating the manipulations in the "expose" phase as well, in which case this may be implemented as an empty
 * method.)
 * <p>
 * To report any security violation, the expert should use the method
 * {@link ManipulationSecurityContext#addViolationEntry(SecurityViolationEntry)}.
 */
public interface ManipulationSecurityExpert extends AccessSecurityExpert {

	Object createExpertContext(ManipulationSecurityContext context);

	void expose(ManipulationSecurityExpositionContext context) throws SecurityAspectException;

	void validate(ManipulationSecurityContext context) throws SecurityAspectException;
}
