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
package com.braintribe.model.processing.itw.asm;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * 
 */
public class AsmArray extends AsmClass {

	private final AsmClass componentType;

	public AsmArray(AsmClass componentType) {
		super(createArrayClassName(componentType), componentType.classPool);
		this.componentType = componentType;
	}

	public static String createArrayClassName(AsmClass componentType) {
		return "[" + componentType.getInternalNameLonger();
	}

	@Override
	public AsmMethod getMethod(String name, AsmClass returnType, AsmClass... params) {
		throw new UnsupportedOperationException("Method 'AsmArray.getMethod' is not supported!");
	}

	@Override
	protected AsmMethod getMethod(MethodSignature ms, AsmClass returnType) {
		throw new UnsupportedOperationException("Method 'AsmArray.getMethod' is not supported!");
	}

	@Override
	public AsmClass getDeclaringClass() {
		return null;
	}

	@Override
	public List<AsmClass> getDeclaredClasses() {
		return Collections.emptyList();
	}

	@Override
	public Class<?> getJavaType() {
		return Array.newInstance(componentType.getJavaType(), 0).getClass();
	}

	@Override
	public boolean isPrimitive() {
		return false;
	}

	@Override
	public boolean isArray() {
		return true;
	}

	@Override
	public AsmClass getSuperClass() {
		return null;
	}

	@Override
	public List<AsmClass> getInterfaces() {
		return null;
	}

	@Override
	public Stream<AsmMethod> getMethods() {
		return Stream.empty();
	}

	@Override
	public Stream<AsmField> getFields() {
		return Stream.empty();
	}

	public AsmClass getComponentType() {
		return componentType;
	}

	@Override
	public String toString() {
		return componentType.toString() + "[]";
	}

}
