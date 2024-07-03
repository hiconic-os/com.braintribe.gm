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
public class AsmField implements AsmMember {

	private AsmClass declaringClass;
	private String name;
	private AsmType type;

	private AsmField(AsmClass owner, String name, AsmType type) {
		this.declaringClass = owner;
		this.name = name;
		this.type = type;
	}

	public static AsmField newExistingField(AsmExistingClass declaringClass, String name, AsmClass type) {
		return new AsmField(declaringClass, name, type);
	}

	public static AsmField newRegisteredField(AsmNewClass declaringClass, String name, AsmType type) {
		return declaringClass.notifyNewField(new AsmField(declaringClass, name, type));
	}

	@Override
	public AsmClass getDeclaringClass() {
		return declaringClass;
	}

	@Override
	public String getName() {
		return name;
	}

	public AsmType getType() {
		return type;
	}

	public AsmClass getRawType() {
		return type.getRawType();
	}

}
