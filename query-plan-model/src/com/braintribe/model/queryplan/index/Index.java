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
package com.braintribe.model.queryplan.index;

import com.braintribe.model.generic.annotation.Abstract;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

import com.braintribe.model.generic.GenericEntity;

/**
 * Represents something which can provide Tuples for given index values. May either be a wrapper for the underlying index of repository (
 * {@link RepositoryIndex}), or an index created for the purpose of executing this query ( {@link GeneratedIndex}), for example to make an
 * implicit join more effective.
 * 
 * The sub-type {@link MetricIndex} represents an index that can also provide a range of values according to given bounds.
 */
@Abstract
public interface Index extends GenericEntity {

	EntityType<Index> T = EntityTypes.T(Index.class);

	IndexType indexType();

}
