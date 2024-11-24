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
import com.braintribe.model.generic.GmPlatform;

import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsType;

/**
 * This represents a value of type Object (i.e. if your entity has a property of type Object, the corresponding {@link Property#getType()} method
 * would return an instance of {@linkplain BaseType}).
 * 
 * This means BaseType can only be a type of a property, or a generic parameter of a collection, but it is never returned as a type of a GM value. In
 * other words {@link GenericModelTypeReflection#getType(Object)} never returns an instance of BaseType.
 */
@JsType(namespace = GmCoreApiInteropNamespaces.reflection)
public interface BaseType extends GenericModelType {

	@JsIgnore
	public static final BaseType INSTANCE = GmPlatform.INSTANCE.getEssentialType(Object.class);

}
