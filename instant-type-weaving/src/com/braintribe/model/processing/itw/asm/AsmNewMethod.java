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

/**
 * 
 */
public class AsmNewMethod extends AsmMethod {

	private AsmClass declaringClass;
	private String name;
	private AsmClass returnType;
	private AsmClass[] params;
	private int modifiers;

	private static AsmClass[] EMPTY_PARAMS = new AsmClass[0];
	
	private AsmNewMethod(AsmNewClass declaringClass, String name, AsmClass returnType, AsmClass[] params, int modifiers) {
		super(returnType.classPool);
		this.declaringClass = declaringClass;
		this.name = name;
		this.returnType = returnType;
		if (params != null && params.length == 0) {
			this.params = EMPTY_PARAMS;
		} else {
			this.params = params;
		}
		this.modifiers = modifiers;
	}

	public static AsmNewMethod newRegisteredMethod(AsmNewClass declaringClass, String name, AsmClass returnType, AsmClass[] params,
			int modifiers) {
		return declaringClass.notifyNewMethod(new AsmNewMethod(declaringClass, name, returnType, params, modifiers));
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public AsmClass getReturnType() {
		return returnType;
	}

	@Override
	public String getReturnTypeName() {
		return returnType.getInternalName();
	}

	@Override
	public AsmClass[] getParams() {
		return params;
	}

	@Override
	public int getModifiers() {
		return modifiers;
	}

	@Override
	public AsmClass getDeclaringClass() {
		return declaringClass;
	}

}
