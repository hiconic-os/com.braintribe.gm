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
package com.braintribe.model.processing.traversing.api.path;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.path.api.HasEntityPropertyInfo;
import com.braintribe.model.generic.path.api.ModelPathElementType;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;

public abstract class TraversingCollectionItemModelPathElement extends TraversingPropertyRelatedModelPathElement {

	public TraversingCollectionItemModelPathElement(TraversingModelPathElement previous, Object value, GenericModelType type) {
		super(previous, value, type);
	}

	protected HasEntityPropertyInfo getEntityPropertyInfo() {
		TraversingModelPathElement previous = getPrevious();
		if (previous.getElementType() == ModelPathElementType.Property) {
			return (HasEntityPropertyInfo) previous;
		} else
			return EmptyEntityPropertyInfo.INSTANCE;
	}

	@Override
	public <T extends GenericEntity> T getEntity() {
		return getEntityPropertyInfo().getEntity();
	}

	@Override
	public <T extends GenericEntity> EntityType<T> getEntityType() {
		return getEntityPropertyInfo().getEntityType();
	}

	@Override
	public Property getProperty() {
		return getEntityPropertyInfo().getProperty();
	}

}
