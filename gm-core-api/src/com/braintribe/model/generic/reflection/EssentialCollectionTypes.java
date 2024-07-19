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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.braintribe.model.generic.GmCoreApiInteropNamespaces;
import com.braintribe.model.generic.GmPlatform;

import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsProperty;

/**
 * All the possible essential {@link CollectionType} instances in form of static fields. In this case this means that
 * given types are parameterized with Objects (i.e. {@link BaseType}).
 */
public interface EssentialCollectionTypes {

	@JsProperty(name = "LIST", namespace = GmCoreApiInteropNamespaces.reflection)
	final ListType TYPE_LIST = GmPlatform.INSTANCE.getEssentialType(List.class);

	@JsProperty(name = "SET", namespace = GmCoreApiInteropNamespaces.reflection)
	final SetType TYPE_SET = GmPlatform.INSTANCE.getEssentialType(Set.class);

	@JsProperty(name = "MAP", namespace = GmCoreApiInteropNamespaces.reflection)
	final MapType TYPE_MAP = GmPlatform.INSTANCE.getEssentialType(Map.class);

	@JsIgnore
	final List<CollectionType> TYPES_COLLECTION = Arrays.asList(TYPE_LIST, TYPE_SET, TYPE_MAP);

}
