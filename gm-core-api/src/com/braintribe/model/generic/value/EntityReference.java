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
package com.braintribe.model.generic.value;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.annotation.ForwardDeclaration;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.value.type.DynamicallyTypedDescriptor;

import jsinterop.annotations.JsMethod;

@ForwardDeclaration("com.braintribe.gm:value-descriptor-model")
@Abstract
@SuppressWarnings("unusable-by-js")
public interface EntityReference extends DynamicallyTypedDescriptor {

	EntityType<EntityReference> T = EntityTypes.T(EntityReference.class);

	/**
	 * This value is used when referencing entity in a context where there is only a single partition value possible for
	 * a given type. In that case we don't want to force the user to provide the partition, and he instead can use this
	 * value to indicate just that - there is only a single possible value, so imagine I gave you the right one and
	 * don't bother me asking which one it is.
	 * 
	 * This is useful when we are creating an interface (e.g. for querying) where the partition is optional. It makes
	 * sense for the user to not be forced to specify a partition as long as there is ambiguity, so he doesn't, and then
	 * the interface layer uses this value when propagating the "request" to the actual "handler" (generic terms, they
	 * do not denote any specific implementation).
	 */
	String ANY_PARTITION = "*";
	
	String refId = "refId";
	String refPartition = "refPartition";

	Object getRefId();
	void setRefId(Object id);

	String getRefPartition();
	void setRefPartition(String refPartition);

	EntityReferenceType referenceType();

	@Override @JsMethod(name="_valueType")
	default EntityType<?> valueType() {
		return GMF.getTypeReflection().getType(getTypeSignature());
	}

}
