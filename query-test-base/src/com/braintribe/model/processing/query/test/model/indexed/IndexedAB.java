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
package com.braintribe.model.processing.query.test.model.indexed;

import com.braintribe.model.generic.annotation.ToStringInformation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * @author peter.gazdik
 */
@ToStringInformation("${#type_short}[${ambig}]")
public interface IndexedAB extends IndexedA, IndexedB {

	EntityType<IndexedAB> T = EntityTypes.T(IndexedAB.class);

	// @formatter:off
	@Override default void putAmbig(String ambig) { IndexedA.super.putAmbig(ambig); }
	@Override default void putUnique(String unique) { IndexedA.super.putUnique(unique); }
	@Override default void putMetric(String metric) { IndexedA.super.putMetric(metric); }
	// @formatter:on

}
