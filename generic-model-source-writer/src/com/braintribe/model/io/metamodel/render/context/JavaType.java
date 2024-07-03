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
package com.braintribe.model.io.metamodel.render.context;

/**
 * @author peter.gazdik
 */
public class JavaType {

	public final String rawType;
	public final String keyType;
	public final String valueType;

	public final boolean isPrimitive;

	public static JavaType booleanType = new JavaType("boolean", true);
	public static JavaType intType = new JavaType("int", true);
	public static JavaType doubleType = new JavaType("double", true);
	public static JavaType floatType = new JavaType("float", true);
	public static JavaType longType = new JavaType("long", true);
	public static JavaType shortType = new JavaType("short", true);
	public static JavaType byteType = new JavaType("byte", true);
	public static JavaType charType = new JavaType("char", true);

	public JavaType(String rawType) {
		this(rawType, null, null);
	}

	public JavaType(String rawType, String elementType) {
		this(rawType, null, elementType);
	}

	public JavaType(String rawType, String keyType, String valueType) {
		this(rawType, keyType, valueType, false);
	}

	private JavaType(String rawType, boolean isPrimitive) {
		this(rawType, null, null, isPrimitive);
	}

	public JavaType(String rawType, String keyType, String valueType, boolean isPrimitive) {
		this.rawType = rawType;
		this.keyType = keyType;
		this.valueType = valueType;
		this.isPrimitive = isPrimitive;
	}

}
