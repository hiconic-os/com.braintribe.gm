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
package com.braintribe.model.processing.itw.synthesis.gm;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityInitializer;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.GmtsEnhancedEntityStub;
import com.braintribe.model.generic.reflection.PropertyAccessInterceptor;
import com.braintribe.model.generic.reflection.type.custom.AbstractEntityType;

public abstract class JvmEntityType<T extends GenericEntity> extends AbstractEntityType<T> implements ItwEntityType {

	private EntityInitializer[] initializers;
	
	public JvmEntityType() {
		super();
	}

	public void setInitializers(EntityInitializer[] initializers) {
		this.initializers = initializers;
	}

	@Override
	protected EntityInitializer[] getInitializers(){
		return initializers;
	}
	
	@Override
	public T createRaw(PropertyAccessInterceptor pai) {
		T result = createRaw();
		((GmtsEnhancedEntityStub) result).pai = pai;
		return result;
	}

	/** Similar to {@link #isAssignableFrom(EntityType)} */
	@Override
	public boolean isAssignableFrom(GenericModelType type) {
		return this == type || getJavaType().isAssignableFrom(type.getJavaType());
	}

	/**
	 * Semantics is 'isAssibnableFrom' i.e. returns true iff the parameter <tt>entityType</tt> is a sub-type of <tt>this</tt>.
	 */
	@Override
	public boolean isAssignableFrom(EntityType<?> entityType) {
		return this == entityType || getJavaType().isAssignableFrom(entityType.getJavaType());
	}

}
