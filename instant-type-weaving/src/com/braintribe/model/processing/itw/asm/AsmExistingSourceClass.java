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

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * 
 */
public class AsmExistingSourceClass extends AsmLoadableClass {

	private final AsmClass superClass;
	private final List<AsmClass> interfaces;

	public AsmExistingSourceClass(String name, AsmClass superClass, List<AsmClass> interfaces, byte[] bytes, AsmClassPool classPool) {
		super(name, classPool);
		this.superClass = superClass;
		this.interfaces = interfaces;
		this.bytes = bytes;
	}

	@Override
	public AsmClass getSuperClass() {
		return superClass;
	}

	@Override
	public List<AsmClass> getInterfaces() {
		return interfaces;
	}

	@Override
	public byte[] getBytes() {
		return bytes;
	}

	@Override
	public AsmMethod getMethod(String name, AsmClass returnType, AsmClass... params) {
		throw new UnsupportedOperationException("Method 'AsmExistingSourceClass.getMethod' is not implemented yet!");
	}

	@Override
	protected AsmMethod getMethod(MethodSignature ms, AsmClass returnType) {
		throw new UnsupportedOperationException("Method 'AsmExistingSourceClass.getMethod' is not implemented yet!");
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
	public Stream<AsmMethod> getMethods() {
		throw new UnsupportedOperationException("Method 'AsmExistingSourceClass.getDeclaredMethods' is not implemented yet!");
	}

	@Override
	public Stream<AsmField> getFields() {
		// TODO fix this
		return Stream.empty();
		
	}

	
}
