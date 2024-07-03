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
package com.braintribe.utils.genericmodel.entity.lcd;

import com.braintribe.common.lcd.ClassInstantiator;
import com.braintribe.model.generic.GenericEntity;

/**
 * Super class for {@link ClassInstantiator}s that can create {@link GenericEntity}s. The purpose of this interface is
 * to avoid dependencies on <code>GmSession</code> or <code>GMF</code> (and thus the GmCore) if the only thing the code
 * needs to do is instantiating entities.
 *
 * @author michael.lafite
 */
public abstract class EntityInstantiator implements ClassInstantiator<GenericEntity> {

	@Override
	public <U extends GenericEntity> U instantiate(Class<U> clazz) {
		try {
			return instantiateWithoutExceptionHandling(clazz);
		} catch (Exception e) {
			throw new ClassInstantiationException(
					"Error while trying to create a new entity instance of type " + clazz.getName() + "!", e);
		}
	}

	@Override
	public GenericEntity instantiate(String className) throws ClassInstantiationException {
		throw new UnsupportedOperationException("instantiate(" + className + ") should not be called in a GWT environment");
	}

	/**
	 * Creates a new instance of the specified entity class. The method may propagate any exceptions.
	 */
	protected abstract <U extends GenericEntity> U instantiateWithoutExceptionHandling(Class<U> entityClass)
			throws Exception;

}
