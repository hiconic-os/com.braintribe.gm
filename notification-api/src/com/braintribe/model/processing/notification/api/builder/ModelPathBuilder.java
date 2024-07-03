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
package com.braintribe.model.processing.notification.api.builder;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.path.GmModelPath;
import com.braintribe.model.path.GmModelPathElement;
import com.braintribe.model.path.GmPropertyPathElement;
import com.braintribe.model.path.GmRootPathElement;

/**
 * Builder API to fluently create a {@link GmModelPath} 
 */
public interface ModelPathBuilder {

	/**
	 * Adds a given {@link GmModelPathElement} to the modelPath. 
	 */
	ModelPathBuilder addElement(GmModelPathElement element, boolean isSelected, boolean openWithAction);
	/**
	 * Adds a given {@link GmModelPathElement} to the modelPath. 
	 */
	ModelPathBuilder addElement(GmModelPathElement element);
	/**
	 * Creates and adds a new {@link GmRootPathElement} based on given typeSignature and value to the modelPath. 
	 */
	ModelPathBuilder addElement(String typeSignature, Object value, boolean isSelected, boolean openWithAction);
	/**
	 * Creates and adds a new {@link GmRootPathElement} based on given typeSignature and value to the modelPath. 
	 */
	ModelPathBuilder addElement(String typeSignature, Object value);
	/**
	 * Creates and adds a new {@link GmRootPathElement} based on given entity to the modelPath.
	 */
	ModelPathBuilder addElement(GenericEntity entity, boolean isSelected, boolean openWithAction);
	/**
	 * Creates and adds a new {@link GmRootPathElement} based on given entity to the modelPath.
	 */
	ModelPathBuilder addElement(GenericEntity entity);
	/**
	 * Creates and adds a new {@link GmPropertyPathElement} based on given entity and property to the modelPath.
	 */
	ModelPathBuilder addElement(GenericEntity entity, String property, boolean isSelected, boolean openWithAction);
	/**
	 * Creates and adds a new {@link GmPropertyPathElement} based on given entity and property to the modelPath.
	 */
	ModelPathBuilder addElement(GenericEntity entity, String property);
	
	/**
	 * Builds the {@link GmModelPath} based on the elements added before. 
	 */
	NotificationBuilder close();
	
	
}
