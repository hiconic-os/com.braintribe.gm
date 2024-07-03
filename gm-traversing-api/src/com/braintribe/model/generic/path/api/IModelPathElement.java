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
package com.braintribe.model.generic.path.api;

import com.braintribe.model.generic.reflection.GenericModelType;

/**
 * 
 * 
 * @author dirk.scheffler
 * @author pit.steinlin
 * @author peter.gazdik
 */
public interface IModelPathElement {

	/** @return the {@link IModelPathElement} predecessor or null if there is none */
	IModelPathElement getPrevious();

	/**
	 * @return the actual node value which is an instance of any valid GM type which is also returned by
	 *         {@link #getType()}
	 */
	<T> T getValue();

	/** @return the {@link GenericModelType} of the value returned by {@link #getValue()} */
	<T extends GenericModelType> T getType();

	/**
	 * @return the {@link ModelPathElementType} which corresponds with the subclass of {@link IModelPathElement} and can
	 *         be used for switches which is faster than instanceof chains
	 */
	ModelPathElementType getElementType();

	/** @return the length of the path */
	int getDepth();

	default boolean isPropertyRelated() {
		return false;
	}

	default boolean isCollectionElementRelated() {
		return false;
	}

}
