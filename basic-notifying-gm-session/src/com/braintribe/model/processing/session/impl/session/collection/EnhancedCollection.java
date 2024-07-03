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
package com.braintribe.model.processing.session.impl.session.collection;

import com.braintribe.model.generic.base.CollectionBase;
import com.braintribe.model.generic.manipulation.LocalEntityProperty;
import com.braintribe.model.generic.tracking.ManipulationListener;

public interface EnhancedCollection extends CollectionBase {

	void setCollectionOwner(LocalEntityProperty owner);

	LocalEntityProperty getCollectionOwner();

	/** The return type is the same as the collection this is invoked on i.e. List, Set or Map. */
	Object getDelegate();

	/** @see #isIncomplete() */
	void setIncomplete(boolean incomplete);

	/** Indicates whether the collection was loaded entirely, or just partially (first few elements) - used in GWT */
	boolean isIncomplete();

	/** Indicates whether the content of the collection was already retrieved, or we just created one (shallow) without  */
	boolean isLoaded();

	void addManipulationListener(ManipulationListener listener);

	void removeManipulationListener(ManipulationListener listener);

}
