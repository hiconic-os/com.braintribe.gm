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
package com.braintribe.gm.model.uiaction;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * This is the model equivalent to the INSTANTIATE_ENTITY known action.
 *
 */
public interface InstantiateEntityActionFolderContent extends ActionFolderContent {
	
	final EntityType<InstantiateEntityActionFolderContent> T = EntityTypes.T(InstantiateEntityActionFolderContent.class);
	
	/**
	* if True allow choose transient instantiation
	*/		
	void  setShowTransient(boolean show);
	boolean getShowTransient();
	
	/**
	* if True allow choose instantiation for all types, not just for context one
	*/		
	void  setShowAll(boolean show);
	boolean getShowAll();
	
	/**
	* if True show items inside the menu
	*/		
	void  setShowInstancesAtMenu(boolean show);
	boolean getShowInstancesAtMenu();	
	
	
	/**
	* Maximum allowed items to show at Menu, if exists more items as limit, than do not show at Menu
	*/			
	void setShowAtMenuMaxLimit(int limit);
	int getShowAtMenuMaxLimit();
	
	/**
	* if True than is disabled to create new Instance for all types
	*/		
	void  setDisableAllInstances(boolean disable);
	boolean getDisableAllInstances();
}
