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
package com.braintribe.model.processing.vde.clone.async;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.VdHolder;
import com.braintribe.processing.async.api.AsyncCallback;

public class EntityCollector extends AsyncCollector<GenericEntity> {

	public EntityCollector(GenericEntity entity) {
		this(entity.entityType().getProperties().size(), entity);
	}

	private EntityCollector(int propertiesToClone, GenericEntity entity) {
		super(propertiesToClone, entity);
	}

	public AsyncCallback<Object> collectProperty(Property property) {
		return collect((e, v) -> {
			if (VdHolder.isVdHolder(v))
				property.setDirectUnsafe(e, v);
			else
				property.setDirect(e, v);
			});
	}

	public static EntityCollector forNonClonedEntity(GenericEntity entity) {
		return new EntityCollector(0, entity);
	}

}
