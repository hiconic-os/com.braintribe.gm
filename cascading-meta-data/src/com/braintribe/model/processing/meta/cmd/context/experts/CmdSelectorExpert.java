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
package com.braintribe.model.processing.meta.cmd.context.experts;

import com.braintribe.model.meta.selector.MetaDataSelector;

/**
 * This interface is only used to mark all the {@link SelectorExpert}s which are implemented by the CMD resolver itself,
 * i.e. the purpose of this interface is strictly for documentation.
 */
public interface CmdSelectorExpert<T extends MetaDataSelector> extends SelectorExpert<T> {
	// empty
}
