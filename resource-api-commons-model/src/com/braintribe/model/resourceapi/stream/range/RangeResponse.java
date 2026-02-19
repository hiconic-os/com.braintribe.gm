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
package com.braintribe.model.resourceapi.stream.range;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

// Not sure why this isn't @Abstract
public interface RangeResponse extends GenericEntity {

	EntityType<RangeResponse> T = EntityTypes.T(RangeResponse.class);

	boolean getRanged();
	void setRanged(boolean ranged);

	/**
	 * Position of the first byte we take from the original payload, or from another perspective the number of bytes skipped from the original
	 * payload.
	 */
	Long getRangeStart();
	void setRangeStart(Long rangeStart);

	/**
	 * Position of the last byte we take from the original payload, inclusive. I.e., if we were only streaming one byte, this would be equal to
	 * rangeStart.
	 */
	Long getRangeEnd();
	void setRangeEnd(Long rangeEnd);

	/** Total size of the original payload. */
	Long getSize();
	void setSize(Long size);

}
