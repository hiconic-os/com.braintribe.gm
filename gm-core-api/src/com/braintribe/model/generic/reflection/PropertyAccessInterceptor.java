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
package com.braintribe.model.generic.reflection;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.GmCoreApiInteropNamespaces;

import jsinterop.annotations.JsType;

/**
 * @author peter.gazdik
 */
@JsType (namespace = GmCoreApiInteropNamespaces.reflection)
@SuppressWarnings("unusable-by-js")
public abstract class PropertyAccessInterceptor {

	public PropertyAccessInterceptor next;

	/** Default implementation with no side-effects. */
	public Object getProperty(Property property, GenericEntity entity, boolean isVd) {
		return next.getProperty(property, entity, isVd);
	}

	/** Default implementation with no side-effects. */
	public Object setProperty(Property property, GenericEntity entity, Object value, boolean isVd) {
		return next.setProperty(property, entity, value, isVd);
	}

}
