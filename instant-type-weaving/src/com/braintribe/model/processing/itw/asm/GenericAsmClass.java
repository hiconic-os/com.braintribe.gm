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
public class GenericAsmClass implements AsmType {

	public static final GenericAsmClass voidType = new GenericAsmClass(AsmClassPool.voidType);

	private AsmClass asmClass;
	private AsmType[] genericParameters;
	private String signature;

	public GenericAsmClass(AsmClass asmClass) {
		set(asmClass, (GenericAsmClass[]) null);
	}

	public GenericAsmClass(AsmClass asmClass, AsmType... genericParameters) {
		if (asmClass.isPrimitive()) {
			throw new IllegalArgumentException("Primitive type cannot be used with generics. Type: " + asmClass);
		}

		set(asmClass, genericParameters);
	}

	private void set(AsmClass asmClass, AsmType[] genericParameters) {
		this.asmClass = asmClass;
		this.genericParameters = genericParameters;
	}

	public AsmType[] getGenericParameters() {
		return genericParameters;
	}

	@Override
	public String genericSignatureOrNull() {
		return getInternalNameLonger();
	}

	@Override
	public String getInternalNameLonger() {
		if (signature == null)
			if (genericParameters == null)
				signature = asmClass.getInternalNameLonger();
			else
				signature = buildGenericSignature();

		return signature;
	}

	@Override
	public AsmClass getRawType() {
		return asmClass;
	}

	@Override
	public Class<?> getJavaType() {
		return asmClass.getJavaType();
	}

	@Override
	public boolean isPrimitive() {
		return false;
	}

	@Override
	public boolean isArray() {
		return false;
	}

	private String buildGenericSignature() {
		String s = asmClass.getInternalNameLonger();
		s = s.substring(0, s.length() - 1); // remove the ';' at the end
		StringBuilder sb = new StringBuilder();
		sb.append(s);
		sb.append("<");
		for (AsmType gp : genericParameters)
			sb.append(gp.getInternalNameLonger());

		sb.append(">;");

		return sb.toString();
	}

}
