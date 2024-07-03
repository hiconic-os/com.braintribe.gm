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
 * Command Interface for Run/Firing Web page defined as Url at TF Studio
 * 
 */

public interface GotoUrl extends Command {

	final EntityType<GotoUrl> T = EntityTypes.T(GotoUrl.class);

	/**
	* @param modelUrl
	* 
	* The Url defined by the String which would be run/fire
	*/
	public void setUrl(String modelUrl);
	
	/**
	* @return the Url as a String 
	*/		
	public String getUrl();
	
	/**
	* @param target
	* 
	* Define the target where to show the Url
	*/
	public void setTarget(String target);	
	
	/**
	* @return the Target as a String 
	*/		
	public String getTarget();
	
	/**
	* @param useImage
	* 
	* Define if use images at web page defined by Url
	*/
	public void setUseImage(Boolean useImage);
	
	/**
	* @return the Boolean if use images
	*/		
	public Boolean getUseImage();
}
