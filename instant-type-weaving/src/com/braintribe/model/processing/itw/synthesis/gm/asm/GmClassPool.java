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

import static com.braintribe.model.processing.itw.asm.AsmClassPool.booleanType;
import static com.braintribe.model.processing.itw.asm.AsmClassPool.objectType;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.enhance.EnhancedEntity;
import com.braintribe.model.generic.pr.AbsenceInformation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.GenericToStringBuilder;
import com.braintribe.model.generic.reflection.GmtsEnhancedEntityStub;
import com.braintribe.model.generic.reflection.GmtsEntityStub;
import com.braintribe.model.generic.reflection.GmtsPlainEntityStub;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.PropertyAccessInterceptor;
import com.braintribe.model.generic.reflection.RuntimeIdGenerator;
import com.braintribe.model.generic.session.GmSession;
import com.braintribe.model.processing.itw.asm.AsmClassPool;
import com.braintribe.model.processing.itw.asm.AsmExistingClass;
import com.braintribe.model.processing.itw.asm.AsmField;
import com.braintribe.model.processing.itw.asm.AsmMethod;
import com.braintribe.model.processing.itw.synthesis.gm.JvmEntityType;
import com.braintribe.model.processing.itw.synthesis.gm.JvmProperty;
import com.braintribe.model.processing.itw.synthesis.gm.experts.DelegatingEntityAccessor;
import com.braintribe.model.processing.itw.synthesis.gm.experts.IndexedPropertyEntity;

/**
 * 
 */
public class GmClassPool {

	public final AsmExistingClass genericEntityType;
	public final AsmExistingClass gmtsEntityStubType;
	public final AsmExistingClass gmtsPlainEntityStubType;
	public final AsmExistingClass gmtsEnhancedEntityStubType;
	public final AsmExistingClass propertyAccessInterceptorType;

	public final AsmExistingClass indexedPropertyEntityType;
	public final AsmExistingClass genericModelTypeType;
	public final AsmExistingClass entityTypeType;
	public final AsmExistingClass jvmEntityTypeType;
	public final AsmExistingClass enhancedEntityType;

	public final AsmExistingClass gmSessionType;
	public final AsmExistingClass genericToStringBuilderType;
	public final AsmExistingClass absenceInformationType;
	public final AsmExistingClass propertyType;
	public final AsmExistingClass jvmPropertyType;
	public final AsmExistingClass delegatingEntityAccessorType;
	public final AsmExistingClass runtimeIdGeneratorType;

	/** @see GmtsEnhancedEntityStub#pai */
	public final AsmField paiField;

	public final AsmMethod pai_getPropertyMethod;
	public final AsmMethod pai_setPropertyMethod;

	private final AsmClassPool asmClassPool;

	public GmClassPool(AsmClassPool classPool) {
		genericEntityType = classPool.get(GenericEntity.class);
		gmtsEntityStubType = classPool.get(GmtsEntityStub.class);
		gmtsPlainEntityStubType = classPool.get(GmtsPlainEntityStub.class);
		gmtsEnhancedEntityStubType = classPool.get(GmtsEnhancedEntityStub.class);
		propertyAccessInterceptorType = classPool.get(PropertyAccessInterceptor.class);
		indexedPropertyEntityType = classPool.get(IndexedPropertyEntity.class);
		genericModelTypeType = classPool.get(GenericModelType.class);
		entityTypeType = classPool.get(EntityType.class);
		jvmEntityTypeType = classPool.get(JvmEntityType.class);
		enhancedEntityType = classPool.get(EnhancedEntity.class);

		gmSessionType = classPool.get(GmSession.class);
		genericToStringBuilderType = classPool.get(GenericToStringBuilder.class);
		absenceInformationType = classPool.get(AbsenceInformation.class);
		propertyType = classPool.get(Property.class);
		jvmPropertyType = classPool.get(JvmProperty.class);
		delegatingEntityAccessorType = classPool.get(DelegatingEntityAccessor.class);
		runtimeIdGeneratorType = classPool.get(RuntimeIdGenerator.class);

		paiField = gmtsEnhancedEntityStubType.getDeclaredField("pai");

		pai_getPropertyMethod = propertyAccessInterceptorType.getMethod("getProperty", objectType, propertyType, genericEntityType, booleanType);
		pai_setPropertyMethod = propertyAccessInterceptorType.getMethod("setProperty", objectType, propertyType, genericEntityType, objectType,
				booleanType);

		this.asmClassPool = classPool;
	}

	public AsmClassPool getAsmClassPool() {
		return asmClassPool;
	}

}
