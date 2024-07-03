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
package com.braintribe.model.processing.itw.synthesis.gm.experts;

import java.util.function.Supplier;



public class InstanceFactory<T> implements Supplier<T> {
	private Class<? extends T> fromClass;

	public InstanceFactory(Class<? extends T> fromClass) {
		super();
		this.fromClass = fromClass;
	}

	@Override
	public T get() throws RuntimeException {
		try {
			return postProcess(fromClass.getDeclaredConstructor().newInstance());
		} catch (Exception e) {
			throw new RuntimeException("error while instantiating from class " + fromClass, e);
		}
	}

	protected T postProcess(T instance) throws Exception {
		return instance;
	}
}
