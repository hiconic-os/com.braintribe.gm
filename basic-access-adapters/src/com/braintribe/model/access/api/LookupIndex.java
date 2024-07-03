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
package com.braintribe.model.access.api;

import java.util.Collection;

import com.braintribe.model.generic.GenericEntity;

/**
 * 
 */
public interface LookupIndex {

	/** Returns the first entity for this index and indexed-property value */
	GenericEntity getValueForIndex(Object indexValue);

	/** Returns all entities for this index and indexed-property value */
	Collection<? extends GenericEntity> getAllValuesForIndex(Object indexValue);

	/** Returns all entities for this index and collection of indexed-property values */
	Collection<? extends GenericEntity> getAllValuesForIndices(Collection<?> indexValues);
}
