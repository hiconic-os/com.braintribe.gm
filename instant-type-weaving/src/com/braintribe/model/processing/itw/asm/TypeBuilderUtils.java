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

import static com.braintribe.model.processing.itw.asm.AsmClassPool.booleanType;
import static com.braintribe.model.processing.itw.asm.AsmClassPool.doubleType;
import static com.braintribe.model.processing.itw.asm.AsmClassPool.floatType;
import static com.braintribe.model.processing.itw.asm.AsmClassPool.intType;
import static com.braintribe.model.processing.itw.asm.AsmClassPool.longType;

import com.braintribe.asm.MethodVisitor;
import com.braintribe.asm.Opcodes;

/**
 *
 */
public class TypeBuilderUtils implements Opcodes {

	public static void newInstance_DefaultConstructor(MethodVisitor mv, AsmClass type) {
		mv.visitTypeInsn(NEW, type.getInternalName());
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKESPECIAL, type.getInternalName(), "<init>", "()V", false);
	}

	public static void addConversionFromPrimitiveIfNecessary(MethodVisitor mv, AsmType primitive) {
		if (primitive.isPrimitive()) {
			AsmClass object = AsmUtils.primitiveToObjectType(primitive);
			// like e.g. (I)Ljava.lang.Integer;
			String signature = AsmUtils.createMethodSignature(object, primitive);
			mv.visitMethodInsn(INVOKESTATIC, object.getInternalName(), "valueOf", signature, false);
		}
	}

	public static void addConversionToPrimitiveIfEligible(MethodVisitor mv, AsmType primitive) {
		if (primitive.isPrimitive()) {
			AsmClass object = AsmUtils.primitiveToObjectType(primitive);
			// like e.g. ()I
			String signature = AsmUtils.createMethodSignature(primitive);
			// like e.g. intValue
			String methodName = ((AsmExistingClass) primitive).getJavaType().getSimpleName() + "Value";
			mv.visitMethodInsn(INVOKEVIRTUAL, object.getInternalName(), methodName, signature, false);
		}
	}

	public static void pushDefault(MethodVisitor mv, AsmClass primitiveClazz) {
		if (primitiveClazz == intType || primitiveClazz == booleanType) {
			mv.visitInsn(ICONST_0);

		} else if (primitiveClazz == longType) {
			mv.visitInsn(LCONST_0);

		} else if (primitiveClazz == floatType) {
			mv.visitInsn(FCONST_0);

		} else if (primitiveClazz == doubleType) {
			mv.visitInsn(DCONST_0);

		} else {
			throw new UnsupportedOperationException("Unsupported primitive type: " + primitiveClazz.getName());
		}
	}

	public static void pushConstant(MethodVisitor mv, int value) {
		if (-128 <= value && value <= 127) {
			mv.visitIntInsn(BIPUSH, value);

		} else if (-32768 <= value && value <= 32767) {
			mv.visitIntInsn(SIPUSH, value);

		} else {
			mv.visitLdcInsn(value);
		}
	}

	public static void pushConstant(MethodVisitor mv, boolean value) {
		mv.visitInsn(value ? ICONST_1 : ICONST_0);
	}

	public static void invokeMethod(MethodVisitor mv, AsmMethod asmMethod) {
		AsmClass declaringClass = asmMethod.getDeclaringClass();
		if (declaringClass.isInterface()) {
			mv.visitMethodInsn(INVOKEINTERFACE, declaringClass.getInternalName(), asmMethod.getName(), asmMethod.getSignature(), true);
		} else {
			mv.visitMethodInsn(INVOKEVIRTUAL, declaringClass.getInternalName(), asmMethod.getName(), asmMethod.getSignature(), false);
		}
	}

	public static void invokePrivateMethod(MethodVisitor mv, AsmMethod asmMethod) {
		mv.visitMethodInsn(INVOKESPECIAL, asmMethod.getDeclaringClass().getInternalName(), asmMethod.getName(), asmMethod.getSignature(),
				false);
	}

	/**
	 * Adds an instruction that retrieves an instance (i.e. not static) field.
	 */
	public static void getInstanceField(MethodVisitor mv, AsmField field) {
		mv.visitFieldInsn(GETFIELD, field.getDeclaringClass().getInternalName(), field.getName(),
				field.getRawType().getInternalNameLonger());
	}

	/**
	 * Adds and instruction that stores an instance (i.e. not static) field.
	 */
	public static void putInstanceField(MethodVisitor mv, AsmField field) {
		mv.visitFieldInsn(PUTFIELD, field.getDeclaringClass().getInternalName(), field.getName(),
				field.getRawType().getInternalNameLonger());
	}

	/**
	 * Adds an instruction that retrieves a static field.
	 */
	public static void getStaticField(MethodVisitor mv, AsmField field) {
		mv.visitFieldInsn(GETSTATIC, field.getDeclaringClass().getInternalName(), field.getName(),
				field.getRawType().getInternalNameLonger());
	}

	/**
	 * Adds an instruction that retrieves a static field. Does not work with primitive types.
	 */
	public static void getStaticField(MethodVisitor mv, String declaringClass, String fieldName, String fieldType) {
		mv.visitFieldInsn(GETSTATIC, AsmUtils.toInternalName(declaringClass), fieldName, AsmUtils.toInternalNameLonger(fieldType));
	}

	/**
	 * Adds and instruction that stores a static field.
	 */
	public static void putStaticField(MethodVisitor mv, AsmField field) {
		mv.visitFieldInsn(PUTSTATIC, field.getDeclaringClass().getInternalName(), field.getName(),
				field.getRawType().getInternalNameLonger());
	}

	/**
	 * Adds checkCast instruction.
	 */
	public static void checkCast(MethodVisitor mv, AsmClass asmClass) {
		mv.visitTypeInsn(CHECKCAST, asmClass.getInternalName());
	}

}
