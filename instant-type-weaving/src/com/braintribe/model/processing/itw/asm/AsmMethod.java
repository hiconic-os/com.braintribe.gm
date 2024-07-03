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
public abstract class AsmMethod implements AsmMember {

	protected AsmClassPool classPool;
	protected String signature;

	public AsmMethod(AsmClassPool classPool) {
		this.classPool = classPool;
	}

	public String getSignature() {
		if (signature == null) {
			
			//The majority will be one of these:
			//()Lcom/braintribe/model/generic/reflection/EntityType;
			//(Ljava/lang/Object;)V
			//()Ljava/lang/Object;
			
			//Since the AsmMethod will live throughout the lifetime of tje JVM, we can
			//call signature.intern() to save a lot of memory space.

			signature = AsmUtils.createMethodSignature(getReturnType(), getParams()).intern();

		}

		return signature;
	}

	@Override
	public abstract AsmClass getDeclaringClass();

	public abstract String getReturnTypeName();

	public abstract AsmClass getReturnType();

	public abstract int getModifiers();

	public abstract AsmClass[] getParams();
}
