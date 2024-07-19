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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.braintribe.model.generic.GmCoreApiInteropNamespaces;
import com.braintribe.model.generic.GmPlatform;

import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsProperty;

/**
 * All the possible {@link SimpleType} instances in form of static fields.
 */
public interface SimpleTypes {

	@JsProperty(name = "STRING", namespace = GmCoreApiInteropNamespaces.reflection)
	final StringType TYPE_STRING = GmPlatform.INSTANCE.getEssentialType(String.class);

	@JsProperty(name = "FLOAT", namespace = GmCoreApiInteropNamespaces.reflection)
	final FloatType TYPE_FLOAT = GmPlatform.INSTANCE.getEssentialType(Float.class);

	@JsProperty(name = "DOUBLE", namespace = GmCoreApiInteropNamespaces.reflection)
	final DoubleType TYPE_DOUBLE = GmPlatform.INSTANCE.getEssentialType(Double.class);

	@JsProperty(name = "BOOLEAN", namespace = GmCoreApiInteropNamespaces.reflection)
	final BooleanType TYPE_BOOLEAN = GmPlatform.INSTANCE.getEssentialType(Boolean.class);

	@JsProperty(name = "INTEGER", namespace = GmCoreApiInteropNamespaces.reflection)
	final IntegerType TYPE_INTEGER = GmPlatform.INSTANCE.getEssentialType(Integer.class);

	@JsProperty(name = "LONG", namespace = GmCoreApiInteropNamespaces.reflection)
	final LongType TYPE_LONG = GmPlatform.INSTANCE.getEssentialType(Long.class);

	@JsProperty(name = "DATE", namespace = GmCoreApiInteropNamespaces.reflection)
	final DateType TYPE_DATE = GmPlatform.INSTANCE.getEssentialType(Date.class);

	@JsProperty(name = "DECIMAL", namespace = GmCoreApiInteropNamespaces.reflection)
	final DecimalType TYPE_DECIMAL = GmPlatform.INSTANCE.getEssentialType(BigDecimal.class);

	// @formatter:off
	@JsIgnore
	final List<SimpleType> TYPES_SIMPLE = Arrays.asList(
			TYPE_STRING,
			TYPE_FLOAT,
			TYPE_DOUBLE,
			TYPE_BOOLEAN,
			TYPE_INTEGER,
			TYPE_LONG,
			TYPE_DATE,
			TYPE_DECIMAL
	);
	// @formatter:on

}
