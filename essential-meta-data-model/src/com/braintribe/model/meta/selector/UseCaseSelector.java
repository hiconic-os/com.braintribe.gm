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
package com.braintribe.model.meta.selector;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * Selector for "use-case" (expressed by string value) for which meta data should be active. This may be used to
 * disambiguate the meaning of some meta data.
 * <p>
 * For example the visibility may be set for two different reasons (security, GUI), so if one wanted to just prohibit a
 * property from being visible in one GUI component, he would simply append this selector with value like "myGuiPanel".
 */

public interface UseCaseSelector extends MetaDataSelector {

	EntityType<UseCaseSelector> T = EntityTypes.T(UseCaseSelector.class);

	public String getUseCase();
	public void setUseCase(String useCase);

}
