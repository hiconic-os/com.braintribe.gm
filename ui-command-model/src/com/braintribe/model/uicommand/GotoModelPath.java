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

import java.util.Set;

import com.braintribe.model.command.Command;

import com.braintribe.model.path.GmModelPath;
import com.braintribe.model.path.GmModelPathElement;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 *  Command Interface for Run/Firing {@link GmModelPath} at TF Studio
 * 
 * 
 */

public interface GotoModelPath extends Command {

	final EntityType<GotoModelPath> T = EntityTypes.T(GotoModelPath.class);
	
	/**
	* @param modelPath
	* 
	* The {@link GmModelPath} which would be run/fire
	*/
	void setPath(GmModelPath modelPath);

	/**
	* @return the {@link GmModelPath}
	*/		
	GmModelPath getPath();
	
	/**
	* Collection of elements {@link GmModelPathElement} for which Action is triggered (if available)
	*/		
	void setOpenWithActionElements(Set<GmModelPathElement> openWithActionElements);
	/**
	* @return set of elements {@link GmModelPathElement}
	*/		
	Set<GmModelPathElement> getOpenWithActionElements();
	
	/**
	* Which element should be selected. If null, latest one is selected
	*/		
	void setSelectedElement(GmModelPathElement selectedElement);

	/**
	* @return selected element {@link GmModelPathElement}
	*/		
	GmModelPathElement getSelectedElement();
		
	/**
	* if True open at actual currentView as a new Tether elements, if False opened at new Tab
	*/		
	void  setAddToCurrentView(boolean add);
	/**
	* @return if add to current view
	*/		
	boolean getAddToCurrentView();
	
	/**
	* if True all elements are opened at Tether, if False just last one is opened
	*/			
	void  setShowFullModelPath(boolean show);	
	/**
	* @return if show full model path
	*/		
	boolean getShowFullModelPath();
}
