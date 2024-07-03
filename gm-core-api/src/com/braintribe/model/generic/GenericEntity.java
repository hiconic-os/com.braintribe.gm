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
package com.braintribe.model.generic;

import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.annotation.ForwardDeclaration;
import com.braintribe.model.generic.annotation.meta.Unique;
import com.braintribe.model.generic.base.EntityBase;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.GenericModelTypeReflection;

@Abstract
@ForwardDeclaration(GenericModelTypeReflection.rootModelName)
@SuppressWarnings("unusable-by-js")
public interface GenericEntity extends EntityBase {

	EntityType<GenericEntity> T = EntityTypes.T(GenericEntity.class);

	String id = "id";
	String partition = "partition";
	String globalId = "globalId";

	<T> T getId();
	void setId(Object id);

	String getPartition();
	void setPartition(String partition);

	@Unique
	String getGlobalId();
	void setGlobalId(String globalId);

}
