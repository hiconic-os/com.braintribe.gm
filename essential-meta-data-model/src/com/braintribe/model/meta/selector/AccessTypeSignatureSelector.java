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
package com.braintribe.model.meta.selector;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface AccessTypeSignatureSelector extends MetaDataSelector {

	EntityType<AccessTypeSignatureSelector> T = EntityTypes.T(AccessTypeSignatureSelector.class);

	/**
	 * Type signature of the access this selector is active for. I.e. this is expected to be a sub-type of
	 * IncrementalAccess.
	 */
	String getDenotationTypeSignature();
	void setDenotationTypeSignature(String denotationTypeSignature);

	/**
	 * Determines whether we want to compare the access if assignable or equal.
	 */
	boolean getAssignable();
	void setAssignable(boolean assignable);

}
