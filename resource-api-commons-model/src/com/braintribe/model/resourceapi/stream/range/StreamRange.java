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
import com.braintribe.model.generic.annotation.meta.Description;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface StreamRange extends GenericEntity {

	EntityType<StreamRange> T = EntityTypes.T(StreamRange.class);

	@Description("The starting point of the requested stream, with first byte starting on position 0.")
	Long getStart();
	void setStart(Long start);

	@Description("The inclusive end point of the requested stream.")
	Long getEnd();
	void setEnd(Long end);

	static StreamRange create(Long start, Long end) {
		StreamRange result = StreamRange.T.create();
		result.setStart(start);
		result.setEnd(end);

		return result;
	}
}
