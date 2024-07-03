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
package com.braintribe.model.processing.core.commons;

import java.util.function.Supplier;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.reflection.StrategyOnCriterionMatch;
import com.braintribe.provider.Holder;


public class AssemblyEnhancer<T> implements Supplier<T> {

	protected Supplier<T> provider = null;


	@Override
	public T get() throws RuntimeException {
		if (this.provider == null) {
			return null;
		}
		
		T assembly = this.provider.get();
		
		@SuppressWarnings("unchecked")
		T clonedAssembly = (T) GMF.getTypeReflection().getBaseType().clone(assembly, null, StrategyOnCriterionMatch.reference);
		
		return clonedAssembly;
	}

	public void setProvider(Supplier<T> provider) {
		this.provider = provider;
	}
	public void setAssembly(T assembly) {
		this.provider = new Holder<T>(assembly);
	}

}
