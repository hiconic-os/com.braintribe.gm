// ============================================================================
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
package com.braintribe.model.meta.data.query;

import java.util.List;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.meta.data.EntityTypeMetaData;
import com.braintribe.model.meta.data.ModelSkeletonCompatible;

/**
 * Specifies that there should be a composite index for given list of properties..
 */
public interface CompositeIndex extends EntityTypeMetaData, ModelSkeletonCompatible {

	EntityType<CompositeIndex> T = EntityTypes.T(CompositeIndex.class);

	List<String> getPropertyNames();
	void setPropertyNames(List<String> propertyNames);

}
