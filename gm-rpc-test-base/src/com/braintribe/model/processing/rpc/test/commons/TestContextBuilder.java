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
package com.braintribe.model.processing.rpc.test.commons;

import java.util.HashMap;
import java.util.Map;

public interface TestContextBuilder extends TestContext {
	<V, A extends TestAttribute<? super V>> TestContextBuilder set(Class<A> attribute, V value);
	
	static TestContextBuilder create() {
		return new TestContextBuilder() {
			private Map<Class<? extends TestAttribute<?>>, Object> attributes = new HashMap<>();
			
			@Override
			public <V, A extends TestAttribute<? super V>> TestContextBuilder set(Class<A> attribute, V value) {
				attributes.put(attribute, value);
				return this;
			}
			
			@Override
			public <V, A extends TestAttribute<? super V>> V get(Class<A> attribute) {
				return (V)attributes.get(attribute);
			}
		};
	}
}
