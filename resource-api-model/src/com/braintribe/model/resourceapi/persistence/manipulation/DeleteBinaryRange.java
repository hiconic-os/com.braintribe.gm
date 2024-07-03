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
public interface DeleteBinaryRange extends BinaryManipulation {

	EntityType<DeleteBinaryRange> T = EntityTypes.T(DeleteBinaryRange.class);

	@Description("Specifies from which position the bytes will be deleted, i.e. how many bytes will be left at the beginning. Position 0 means we are deleting from the beginning.")
	long getPosition();
	void setPosition(long position);

	@Description("Specifies the number of bytes that will be deleted.")
	long getLength();
	void setLength(long length);

}
