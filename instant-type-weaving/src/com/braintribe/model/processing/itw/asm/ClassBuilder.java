// ============================================================================
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
// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
// ============================================================================
package com.braintribe.model.processing.itw.asm;

import java.util.List;

/**
 * 
 */
public class ClassBuilder extends ImplementationTypeBuilder {

	public ClassBuilder(AsmClassPool classPool, String typeSignature, boolean isAbstract, AsmClass superClass,
			List<AsmClass> superInterfaces) {
		super(classPool, typeSignature, superClass, superInterfaces);

		int access = ACC_PUBLIC + ACC_SUPER + (isAbstract ? ACC_ABSTRACT : 0);
		String internalName = AsmUtils.toInternalName(typeSignature);
		String superClassInternal = superClass != null ? superClass.getInternalName() : "java/lang/Object";

		visit(V1_6, access, internalName, null, superClassInternal, toInternalNames(superInterfaces));
	}

	public ClassBuilder addDefaultConstructor() {
		return addDelegateConstructor();
	}

	public ClassBuilder addDelegateConstructor(AsmClass... params) {
		mv = visitMethod(ACC_PUBLIC, "<init>", AsmClassPool.voidType, params);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);

		int counter = 1;
		int max = 1;
		for (AsmClass param : params) {
			mv.visitVarInsn(AsmUtils.getLoadOpCode(param), counter++);
			max += AsmUtils.getSize(param);
		}

		mv.visitMethodInsn(INVOKESPECIAL, asmClass.getSuperClass().getInternalName(), "<init>",
				AsmUtils.createMethodSignature(AsmClassPool.voidType, params), false);
		mv.visitInsn(RETURN);

		mv.visitMaxs(max, max);
		mv.visitEnd();

		return this;
	}

	public void addInitializedPublicSingletonField(String fieldName) {
		addField(fieldName, asmClass, ACC_PUBLIC + ACC_STATIC);
		initializeStaticSingleton(fieldName);
	}

	private void initializeStaticSingleton(String singletonName) {
		String internalName = asmClass.getInternalName();
		mv = visitMethod(ACC_STATIC, "<clinit>", AsmClassPool.voidType);
		mv.visitCode();
		mv.visitTypeInsn(NEW, internalName);
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKESPECIAL, internalName, "<init>", "()V", false);
		mv.visitFieldInsn(PUTSTATIC, internalName, singletonName, asmClass.getInternalNameLonger());
		mv.visitInsn(RETURN);
		mv.visitMaxs(2, 0);
		mv.visitEnd();
	}

}
