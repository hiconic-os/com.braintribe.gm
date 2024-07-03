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
package com.braintribe.model.queryplan.value;

import java.util.Collection;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;


/**
 * Represents a {@link Collection} of {@link GenericEntity entities} which are retrieved from an index with given {@link #getIndexId()
 * indexId} for given {@link #getKeys() keys}. These keys can actually be resolved to a collection, or just a single value, but when it has
 * the type {@link Collection}, we always interpret it as a collection rather than a single value of collection type, because we do not
 * support indices on collection properties.
 */

public interface IndexValue extends ConstantValue {

	EntityType<IndexValue> T = EntityTypes.T(IndexValue.class);

	String getIndexId();
	void setIndexId(String indexId);

	ConstantValue getKeys();
	void setKeys(ConstantValue keys);

	@Override
	default ValueType valueType() {
		return ValueType.indexValue;
	}

}
