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
package com.braintribe.model.processing.itw.synthesis.gm.asm;

import static com.braintribe.model.processing.itw.asm.AsmClassPool.objectType;
import static com.braintribe.model.processing.itw.asm.AsmClassPool.voidType;

import java.lang.reflect.Modifier;
import java.util.List;

import com.braintribe.model.processing.itw.asm.AsmClassPool;
import com.braintribe.model.processing.itw.asm.AsmField;
import com.braintribe.model.processing.itw.asm.AsmMethod;
import com.braintribe.model.processing.itw.asm.AsmType;
import com.braintribe.model.processing.itw.asm.AsmUtils;
import com.braintribe.model.processing.itw.asm.ClassBuilder;
import com.braintribe.model.processing.itw.synthesis.gm.PreliminaryProperty;
import com.braintribe.model.processing.itw.synthesis.java.PropertyAnalysis.PropertyDescription;
import com.braintribe.model.processing.itw.tools.ItwTools;

public class EnhancedEntityImplementer extends AbstractGenericEntityImplementer {

	public EnhancedEntityImplementer(ClassBuilder b, GmClassPool gcp) {
		super(b, gcp);
	}

	public void addBooleanConstructor() {
		mv = b.visitConstructor(ACC_PUBLIC, AsmClassPool.booleanType);
		mv.visitCode();

		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ILOAD, 1);
		mv.visitMethodInsn(INVOKESPECIAL, asmClass.getSuperClass().getInternalName(), "<init>", "(Z)V", false);
		mv.visitInsn(RETURN);

		mv.visitMaxs(2, 2);
		mv.visitEnd();
	}

	// public void addPaiConstructor() {
	// mv = b.visitConstructor(ACC_PUBLIC, gcp.propertyAccessInterceptorType);
	// mv.visitCode();
	//
	// mv.visitVarInsn(ALOAD, 0);
	// mv.visitVarInsn(ALOAD, 1);
	// mv.visitMethodInsn(INVOKESPECIAL, asmClass.getSuperClass().getInternalName(), "<init>",
	// "(Lcom/braintribe/model/generic/reflection/PropertyAccessInterceptor;)V", false);
	// mv.visitInsn(RETURN);
	//
	// mv.visitMaxs(2, 2);
	// mv.visitEnd();
	// }
	//
	public void addAopAroundGetterSetter(String propertyClassName, PreliminaryProperty pp, PropertyDescription pd) {
		pp.aopGetter = addAopAroundGetter(propertyClassName, pd);
		pp.aopSetter = addAopAroundSetter(propertyClassName, pd);
	}

	private AsmMethod addAopAroundGetter(String propertyClassName, PropertyDescription pd) {
		AsmType accessPropertyClass = pd.accessPropertyClass;
		AsmType actualPropertyClass = pd.actualPropertyClass;

		// method: %{accessPropertyClass} get${PropertyName}()
		mv = b.visitMethodWithGenerics(ACC_PUBLIC, pd.getterName, accessPropertyClass);
		mv.visitCode();

		// return (${actualPropertyClass}) pai.getProperty(${PropertyClass}.INSTANCE, this);
		mv.visitVarInsn(ALOAD, 0);
		getInstanceField(gcp.paiField);
		getStaticField(propertyClassName, PropertyImplementer.SINGLETON_NAME, propertyClassName);
		mv.visitVarInsn(ALOAD, 0);
		pushConstant(false);
		invokeMethod(gcp.pai_getPropertyMethod);
		checkCast(actualPropertyClass);
		addConversionToPrimitiveIfEligible(accessPropertyClass);
		addReturn(accessPropertyClass);

		mv.visitMaxs(4, 1);
		mv.visitEnd();

		return b.notifyMethodFinished();
	}

	private AsmMethod addAopAroundSetter(String propertyClassName, PropertyDescription pd) {
		AsmType accessPropertyClass = pd.accessPropertyClass;

		// method: void set${PropertyName}(%{accessPropertyClass} value)
		mv = b.visitMethodWithGenerics(ACC_PUBLIC, pd.setterName, AsmClassPool.voidType, accessPropertyClass);
		mv.visitCode();

		// pai.setProperty(${PropertyClass}.INSTANCE, this, value);
		mv.visitVarInsn(ALOAD, 0);
		getInstanceField(gcp.paiField);
		getStaticField(propertyClassName, PropertyImplementer.SINGLETON_NAME, propertyClassName);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(AsmUtils.getLoadOpCode(accessPropertyClass), 1);
		addConversionFromPrimitiveIfNecessary(accessPropertyClass);
		pushConstant(false);
		invokeMethod(gcp.pai_setPropertyMethod);
		mv.visitInsn(POP);
		mv.visitInsn(RETURN);

		mv.visitMaxs(4 + AsmUtils.getSize(accessPropertyClass), 1 + AsmUtils.getSize(accessPropertyClass));
		mv.visitEnd();

		return b.notifyMethodFinished();
	}

	public void addGetterOverride(PropertyDescription pd, AsmType overrideType) {
		// method: %{accessPropertyClass} get${PropertyName}()
		mv = b.visitMethodWithGenerics(ACC_PUBLIC, pd.getterName, overrideType);
		mv.visitCode();

		// return (${overrideType}) get${PropertyName}();
		mv.visitVarInsn(ALOAD, 0);
		String signature = AsmUtils.createMethodSignature(pd.actualPropertyClass).intern();
		mv.visitMethodInsn(INVOKEVIRTUAL, asmClass.getInternalName(), pd.getterName, signature, false);
		checkCast(overrideType);
		addReturn(overrideType);

		mv.visitMaxs(1, 1);
		mv.visitEnd();
		
		b.notifyMethodFinished();
	}

	public void createAndStorePropertyField(PropertyDescription pd) {
		pd.enhancedPropertyField = b.addField(pd.getFieldName(), objectType, Modifier.PROTECTED);
	}

	public void addEnhancedRead(PropertyDescription pd) {
		AsmField f = pd.enhancedPropertyField;

		// method: public Object readProperty()
		mv = b.visitMethod(ACC_PUBLIC, ItwTools.getReaderName(pd.property), objectType);
		mv.visitCode();

		// return this.property;
		mv.visitVarInsn(ALOAD, 0);
		getInstanceField(f);
		mv.visitInsn(ARETURN);

		mv.visitMaxs(1, 1);
		mv.visitEnd();
		b.notifyMethodFinished();
	}

	public void addEnhancedWrite(PropertyDescription pd) {
		AsmField f = pd.enhancedPropertyField;

		// method: public void writeProperty(Object value)
		mv = b.visitMethod(ACC_PUBLIC, ItwTools.getWriterName(pd.property), voidType, objectType);
		mv.visitCode();

		// this.property = (${propertyFieldType}) value;
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 1);
		putInstanceField(f);

		// return
		mv.visitInsn(RETURN);

		mv.visitMaxs(2, 2);
		mv.visitEnd();
		b.notifyMethodFinished();
	}

	public void addInitializePrimitiveFields(List<PropertyDescription> primitiveProps) {
		mv = b.visitMethod(ACC_PROTECTED, "initializePrimitiveFields", voidType);
		mv.visitCode();

		for (PropertyDescription pd : primitiveProps) {
			mv.visitVarInsn(ALOAD, 0);
			pushDefault(pd.accessPropertyClass);
			addConversionFromPrimitiveIfNecessary(pd.accessPropertyClass);
			mv.visitFieldInsn(PUTFIELD, asmClass.getInternalName(), pd.fieldName, "Ljava/lang/Object;");
		}

		mv.visitInsn(RETURN);
		mv.visitMaxs(3, 1);
		mv.visitEnd();
		b.notifyMethodFinished();
	}

	@Override
	protected AsmMethod getter(PreliminaryProperty pp) {
		return pp.aopGetter;
	}

	@Override
	protected AsmMethod setter(PreliminaryProperty pp) {
		return pp.aopSetter;
	}

}
