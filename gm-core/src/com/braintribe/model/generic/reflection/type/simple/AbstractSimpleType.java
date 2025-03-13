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
package com.braintribe.model.generic.reflection.type.simple;

import com.braintribe.model.generic.reflection.AbstractGenericModelType;
import com.braintribe.model.generic.reflection.CloningContext;
import com.braintribe.model.generic.reflection.GenericModelException;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.SimpleType;
import com.braintribe.model.generic.reflection.StrategyOnCriterionMatch;
import com.braintribe.model.generic.reflection.TraversingContext;

public abstract class AbstractSimpleType extends AbstractGenericModelType implements SimpleType {

	protected AbstractSimpleType(Class<?> javaType) {
		super(javaType);

	}

	@Override
	public final boolean isSimple() {
		return true;
	}

	@Override
	public boolean isScalar() {
		return true;
	}

	@Override
	public String instanceToString(Object value) throws GenericModelException {
		return value.toString();
	}

	@Override
	public Object cloneImpl(CloningContext cloningContext, Object instance, StrategyOnCriterionMatch strategy) {
		return instance;
	}

	@Override
	public final void traverseImpl(TraversingContext traversingContext, Object instance) {
		// noop
	}

	@Override
	public final String getSelectiveInformation(Object instance) {
		return instance != null ? instance.toString() : "";
	}

	@Override
	public final boolean isAssignableFrom(GenericModelType type) {
		return this == type;
	}

	@Override
	public boolean isInstance(Object value) {
		return value != null && javaType == value.getClass();
	}

	@Override
	public final boolean areEntitiesReachable() {
		return false;
	}

	@Override
	public final boolean areCustomInstancesReachable() {
		return false;
	}

	@Override
	public final boolean isEmpty(Object value) {
		return value == null;
	}

}
