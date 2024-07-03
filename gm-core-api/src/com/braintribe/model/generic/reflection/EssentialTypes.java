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

import com.braintribe.model.generic.GmCoreApiInteropNamespaces;

import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsType;

@JsType(namespace = GmCoreApiInteropNamespaces.reflection)
public interface EssentialTypes extends SimpleTypes, EssentialCollectionTypes {

	final BaseType TYPE_OBJECT = BaseType.INSTANCE;

	// @formatter:off
	@JsIgnore
	final List<GenericModelType> TYPES_ESSENTIAL = Arrays.<GenericModelType> asList(
			TYPE_OBJECT, 
			
			TYPE_STRING, 
			TYPE_FLOAT,
			TYPE_DOUBLE,
			TYPE_BOOLEAN,
			TYPE_INTEGER,
			TYPE_LONG,
			TYPE_DATE,
			TYPE_DECIMAL,

			TYPE_LIST,
			TYPE_SET,
			TYPE_MAP
	);
	// @formatter:on

}
