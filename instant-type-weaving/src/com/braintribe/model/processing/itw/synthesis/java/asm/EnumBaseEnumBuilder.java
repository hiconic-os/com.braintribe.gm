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

import java.util.List;

import com.braintribe.asm.MethodVisitor;
import com.braintribe.asm.Type;
import com.braintribe.model.processing.itw.asm.AsmClass;
import com.braintribe.model.processing.itw.asm.AsmClassPool;
import com.braintribe.model.processing.itw.asm.AsmEnumConstant;
import com.braintribe.model.processing.itw.asm.AsmField;
import com.braintribe.model.processing.itw.asm.AsmMethod;
import com.braintribe.model.processing.itw.asm.EnumBuilder;
import com.braintribe.model.processing.itw.asm.TypeBuilderUtils;

/**
 * @author peter.gazdik
 */
public class EnumBaseEnumBuilder extends EnumBuilder {

	private AsmField enumTypeField;

	public EnumBaseEnumBuilder(AsmClassPool classPool, String typeSignature, List<AsmEnumConstant> constants, List<AsmClass> superInterfaces) {
		super(classPool, typeSignature, constants, superInterfaces);
	}

	@Override
	protected void initializeFields(List<AsmEnumConstant> constants) {
		createEnumTypeField();

		super.initializeFields(constants);

		addTypeMethod_WithBridge();
	}

	private void createEnumTypeField() {
		// field: EnumType T;
		visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "T", AsmClassPool.enumTypeType, null);
		enumTypeField = notifyNewField();
	}

	@Override
	protected void initializeNonConstantFields(MethodVisitor mv) {
		// T = EnumType.T(${enclosingEnumType}.class);
		mv.visitLdcInsn(Type.getObjectType(asmClass.getInternalName()));
		mv.visitMethodInsn(INVOKESTATIC, "com/braintribe/model/generic/reflection/EnumTypes", "T",
				"(Ljava/lang/Class;)Lcom/braintribe/model/generic/reflection/EnumType;", false);
		TypeBuilderUtils.putStaticField(mv, enumTypeField);
	}

	private void addTypeMethod_WithBridge() {
		// method: EnumType type()
		MethodVisitor mv = visitMethod(ACC_PUBLIC, "type", AsmClassPool.enumTypeType);
		mv.visitCode();

		// return T;
		TypeBuilderUtils.getStaticField(mv, enumTypeField);
		mv.visitInsn(ARETURN);

		mv.visitMaxs(1, 1);
		mv.visitEnd();

		AsmMethod typeMethod = notifyMethodFinished();

		/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

		// method: GenericModelType type()
		mv = visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "type", AsmClassPool.genericModelTypeType);
		mv.visitCode();

		// return type();
		mv.visitVarInsn(ALOAD, 0);
		TypeBuilderUtils.invokeMethod(mv, typeMethod);
		mv.visitInsn(ARETURN);

		mv.visitMaxs(1, 1);
		mv.visitEnd();

		notifyNonReflectableMethodFinished();
	}

}
