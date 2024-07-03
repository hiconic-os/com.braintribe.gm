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
package com.braintribe.model.uicommand;

import com.braintribe.model.command.Command;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * Command that reloads the current view displayed in GME.
 * @author michel.docouto
 */
public interface ReloadView extends Command {
	
	final EntityType<ReloadView> T = EntityTypes.T(ReloadView.class);
	
	/**
	 * If true, then we will reload not only the current views, but all views (when they first are activated).
	 */
	void setReloadAll(boolean reloadAll);
	
	/**
	 * If true, then we will reload not only the current views, but all views (when they first are activated).
	 */
	boolean getReloadAll();

}
