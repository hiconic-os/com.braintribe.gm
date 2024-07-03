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
package com.braintribe.model.processing.itw.synthesis.java.asm;

import com.braintribe.asm.Opcodes;
import com.braintribe.asm.Type;
import com.braintribe.model.processing.itw.asm.AsmClassPool;
import com.braintribe.model.processing.itw.asm.AsmField;
import com.braintribe.model.processing.itw.asm.AsmNewClass;
import com.braintribe.model.processing.itw.asm.AsmType;
import com.braintribe.model.processing.itw.asm.GenericAsmClass;
import com.braintribe.model.processing.itw.asm.InterfaceBuilder;

/**
 * @author peter.gazdik
 */
public class DeclarationInfaceImplementer extends AbstractTypeBuilder<InterfaceBuilder> {

	public DeclarationInfaceImplementer(InterfaceBuilder b, AsmClassPool asmClassPool) {
		super(b, asmClassPool);
	}

	public void implementEntityTypeLiteral(AsmNewClass declarationIface) {
		AsmType type = new GenericAsmClass(AsmClassPool.entityTypeType, declarationIface);

		b.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "T", type, null);
		AsmField entityTypeField = b.notifyNewField();

		mv = b.visitMethod(Opcodes.ACC_STATIC, "<clinit>", AsmClassPool.voidType);
		mv.visitCode();

		mv.visitLdcInsn(Type.getType(declarationIface.getInternalNameLonger()));
		mv.visitMethodInsn(INVOKESTATIC, "com/braintribe/model/generic/reflection/EntityTypes", "T",
				"(Ljava/lang/Class;)Lcom/braintribe/model/generic/reflection/EntityType;", false);
		putStaticField(entityTypeField);

		mv.visitInsn(RETURN);
		mv.visitMaxs(1, 0);
		mv.visitEnd();
	}

}
