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
package com.braintribe.model.generic.reflection.type.custom;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.reflection.AbstractGenericModelType;
import com.braintribe.model.generic.reflection.CustomType;
import com.braintribe.model.generic.reflection.Model;

/**
 * @author peter.gazdik
 */
public abstract class AbstractCustomType extends AbstractGenericModelType implements CustomType {

	protected AbstractCustomType(Class<?> javaType) {
		super(javaType);
	}

	@Override
	public final Model getModel() {
		return GMF.getTypeReflection().getModelForType(getTypeName());
	}

	@Override
	public final String getTypeName() {
		return getJavaType().getName();
	}

	@Override
	public final String getShortName() {
		return getJavaType().getSimpleName();
	}

}
