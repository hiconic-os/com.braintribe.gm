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

import static com.braintribe.model.processing.itw.tools.ItwTools.getGetterName;
import static com.braintribe.model.processing.itw.tools.ItwTools.getSetterName;

import java.lang.reflect.Modifier;

import com.braintribe.model.processing.itw.asm.AsmClass;
import com.braintribe.model.processing.itw.asm.AsmField;
import com.braintribe.model.processing.itw.asm.AsmMethod;
import com.braintribe.model.processing.itw.asm.AsmNewClass;
import com.braintribe.model.processing.itw.asm.ClassBuilder;
import com.braintribe.model.processing.itw.synthesis.gm.PreliminaryEntityType;
import com.braintribe.model.processing.itw.synthesis.gm.PreliminaryProperty;
import com.braintribe.model.processing.itw.tools.ItwTools;

/**
 * @author peter.gazdik
 */
public abstract class AbstractGenericEntityImplementer extends AbstractClassBuilder {

	public AbstractGenericEntityImplementer(ClassBuilder b, GmClassPool gcp) {
		super(b, gcp);
	}

	public void addType_WithBridge(PreliminaryEntityType pet) {
		AsmNewClass entityTypeClass = pet.entityTypeImplementer.getPreliminaryClass();
		AsmField singletonField = entityTypeClass.getDeclaredField(EntityTypeImplementer.SINGLETON_NAME);

		// method: EntityType<?> type()
		mv = b.visitMethod(ACC_PUBLIC, "type", gcp.entityTypeType);
		mv.visitCode();

		// return ${EntitySignature}-et.INSTANCE;
		getStaticField(singletonField);
		mv.visitInsn(ARETURN);

		mv.visitMaxs(1, 1);
		mv.visitEnd();
		AsmMethod typeMethod = b.notifyMethodFinished();

		/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

		// method: GenericModelType type()
		mv = b.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "type", gcp.genericModelTypeType);
		mv.visitCode();

		// return this.type(); // this is the type method defined above
		mv.visitVarInsn(ALOAD, 0);
		invokeMethod(typeMethod);
		mv.visitInsn(ARETURN);

		mv.visitMaxs(1, 1);
		mv.visitEnd();
		b.notifyNonReflectableMethodFinished();
	}

	protected abstract AsmMethod getter(PreliminaryProperty pp);
	
	protected abstract AsmMethod setter(PreliminaryProperty pp);

	public void addTransientGetterSetter(String propertyName, AsmClass propertyType) {
		AsmField field = b.addField(ItwTools.getFieldName(propertyName), propertyType, Modifier.PUBLIC);

		b.addGetter(field, getGetterName(propertyName));
		b.addSetter(field, getSetterName(propertyName));
	}

}
