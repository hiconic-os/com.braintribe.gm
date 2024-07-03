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
package com.braintribe.model.access.crud.api.read;

import com.braintribe.model.access.crud.api.DataReadingContext;
import com.braintribe.model.generic.GenericEntity;

/**
 * A {@link DataReadingContext} implementation provided to
 * {@link PropertyReader} experts containing necessary informations on the
 * expected property value.
 * 
 * @author gunther.schenk
 */
public interface PropertyReadingContext<T extends GenericEntity> extends DataReadingContext<T> {

	T getHolder();

	String getPropertyName();

	/**
	 * Static helper method to build a new {@link PropertyReadingContext} instance.
	 */
	static <T extends GenericEntity> PropertyReadingContext<T> create(T holder, String propertyName) {
		return create(holder, propertyName, null);
	}

	/**
	 * Static helper method to build a new {@link PropertyReadingContext} instance with query context.
	 */
	static <T extends GenericEntity> PropertyReadingContext<T> create(T holder, String propertyName, QueryContext context) {
		return new PropertyReadingContext<T>() {
			@Override
			public T getHolder() {
				return holder;
			}

			@Override
			public String getPropertyName() {
				return propertyName;
			}
			@Override
			public QueryContext getQueryContext() {
				return context;
			}
		};
	}

}
