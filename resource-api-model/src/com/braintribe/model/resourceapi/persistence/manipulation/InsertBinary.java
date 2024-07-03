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
package com.braintribe.model.resourceapi.persistence.manipulation;

import com.braintribe.model.generic.annotation.meta.Description;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * @author peter.gazdik
 */
@Description("Inserts binary data respresented by 'payload' ")
public interface InsertBinary extends BinaryManipulationWithPayload {

	EntityType<InsertBinary> T = EntityTypes.T(InsertBinary.class);

	@Description("Specifies at which position the new payload will be inserted, i.e. after how many bytes of the original payload. Position 0 means prepending.")
	long getPosition();
	void setPosition(long position);

}
