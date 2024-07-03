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

import com.braintribe.model.generic.GmCoreApiInteropNamespaces;

import jsinterop.annotations.JsType;

/**
 * Transient property is just a field, of any java type, with a direct-accessing getter and setter (unlike a property which can only be of a valid GM
 * type and it's access methods use interceptors).
 */
@JsType(namespace=GmCoreApiInteropNamespaces.reflection)
@SuppressWarnings("unusable-by-js")
public interface TransientProperty extends Attribute {

	/**
	 * Same as {@link #getJavaType()}, this method exists to keep the interface similar to {@link Property} with it's {@link Property#getType()}
	 * method.
	 */
	Class<?> getType();

}
