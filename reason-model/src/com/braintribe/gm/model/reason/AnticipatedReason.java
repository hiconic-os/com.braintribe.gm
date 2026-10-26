// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2026
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ============================================================================
package com.braintribe.gm.model.reason;

import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * A reason representing an anticipated unsatisfied outcome of an operation.
 * <p>
 * An anticipated reason is part of the operation's modeled semantics and does not by itself indicate a technical malfunction. Depending on
 * the calling context, it may be treated as an error, a fallback condition, or regular control flow. This classification deliberately does
 * not prescribe logging, severity, retry behavior, or transport status.
 */
@Abstract
public interface AnticipatedReason extends Reason {

	EntityType<AnticipatedReason> T = EntityTypes.T(AnticipatedReason.class);
}
