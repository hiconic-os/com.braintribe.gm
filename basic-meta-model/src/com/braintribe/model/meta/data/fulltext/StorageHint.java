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
package com.braintribe.model.meta.data.fulltext;


import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.meta.data.PropertyMetaData;

/**
 * This MetaData defines how an entity property is persisted within the FullText store.
 * 
 * @author gunther.schenk
 *
 */

public interface StorageHint extends PropertyMetaData, FulltextMetaData {

	EntityType<StorageHint> T = EntityTypes.T(StorageHint.class);

	// @formatter:off
	void setOption(StorageOption option);
	StorageOption getOption();
	// @formatter:on

}
