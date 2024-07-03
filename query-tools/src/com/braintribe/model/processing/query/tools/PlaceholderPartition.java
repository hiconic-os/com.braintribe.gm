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
package com.braintribe.model.processing.query.tools;

import com.braintribe.model.generic.GenericEntity;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.query.Operand;
import com.braintribe.model.query.PropertyOperand;

/**
 * This is used as an identifiable replacement for PropertyOperand related to a {@link GenericEntity#partition} property, when replacing with String
 * is not possible (e.g. due to type constraints - cloning a {@link PropertyOperand} representing a partition property must result in an entity).
 */
public interface PlaceholderPartition extends Operand {

	EntityType<PlaceholderPartition> T = EntityTypes.T(PlaceholderPartition.class);

	PlaceholderPartition INSTANCE = PlaceholderPartition.T.create();

}
