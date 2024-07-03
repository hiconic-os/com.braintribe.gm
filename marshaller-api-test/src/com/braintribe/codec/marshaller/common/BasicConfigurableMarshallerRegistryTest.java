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
package com.braintribe.codec.marshaller.common;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class BasicConfigurableMarshallerRegistryTest {

	@Test
	public void testRegistrySimple() {
		BasicConfigurableMarshallerRegistry registry = new BasicConfigurableMarshallerRegistry();

		registry.registerMarshaller("text/plain", DummyMarshaller.instance);

		assertThat(registry.getMarshaller("text/plain")).isSameAs(DummyMarshaller.instance);
	}

	@Test
	public void testRegistryWithParams() {
		BasicConfigurableMarshallerRegistry registry = new BasicConfigurableMarshallerRegistry();

		registry.registerMarshaller("text/plain", DummyMarshaller.instance);

		assertThat(registry.getMarshaller("text/plain; encoding=UTF-8")).isSameAs(DummyMarshaller.instance);
	}

	@Test
	public void testRegistryWithoutParams() {
		BasicConfigurableMarshallerRegistry registry = new BasicConfigurableMarshallerRegistry();

		registry.registerMarshaller("text/plain; encoding=UTF-8", DummyMarshaller.instance);

		assertThat(registry.getMarshaller("text/plain")).isSameAs(DummyMarshaller.instance);
	}

	@Test
	public void testRegistryWithParams2() {
		BasicConfigurableMarshallerRegistry registry = new BasicConfigurableMarshallerRegistry();

		registry.registerMarshaller("text/plain; encoding=UTF-8", DummyMarshaller.instance);

		assertThat(registry.getMarshaller("text/plain; encoding=ISO-8859-1")).isSameAs(DummyMarshaller.instance);
	}
	
	@Test
	public void testRegistryRegex() {
		BasicConfigurableMarshallerRegistry registry = new BasicConfigurableMarshallerRegistry();

		registry.registerMarshaller("text/plain", DummyMarshaller.instance);

		assertThat(registry.getMarshaller("text/*")).isSameAs(DummyMarshaller.instance);
	}
}
