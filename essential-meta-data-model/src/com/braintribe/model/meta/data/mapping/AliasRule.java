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
package com.braintribe.model.meta.data.mapping;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.meta.data.ModelSkeletonCompatible;
import com.braintribe.model.meta.data.UniversalMetaData;

/**
 * Similar to {@link Alias}, but the new name is derived based on {@link #getCasing() casing} and {@link #getSeparator() separator}.
 */
public interface AliasRule extends UniversalMetaData, ModelSkeletonCompatible {

	EntityType<AliasRule> T = EntityTypes.T(AliasRule.class);

	WordCasing getCasing();
	void setCasing(WordCasing casing);

	WordSeparator getSeparator();
	void setSeparator(WordSeparator separator);

}
