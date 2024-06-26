// ============================================================================
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
// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
// ============================================================================
package com.braintribe.codec.marshaller.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.Test;

import com.braintribe.codec.marshaller.api.DecodingLenience;
import com.braintribe.codec.marshaller.api.GmDeserializationOptions;
import com.braintribe.codec.marshaller.api.GmSerializationOptions;
import com.braintribe.codec.marshaller.api.OutputPrettiness;
import com.braintribe.codec.marshaller.api.options.GmDeserializationContextBuilder;
import com.braintribe.codec.marshaller.api.options.GmSerializationContextBuilder;
import com.braintribe.codec.marshaller.api.options.attributes.AbsentifyMissingPropertiesOption;
import com.braintribe.codec.marshaller.api.options.attributes.OutputPrettinessOption;
import com.braintribe.codec.marshaller.api.options.attributes.StabilizeOrderOption;
import com.braintribe.model.generic.reflection.SimpleTypes;

public class ConfigurableMarshallingOptionsTest {

	/**
	 * Tests whether any of the default options were initialized, choosing one random option for both
	 * {@link GmSerializationOptions} and {@link GmDeserializationOptions}
	 */
	@Test
	public void testDefaultsInitialized() {
		assertThat(GmSerializationOptions.deriveDefaults().build().getAttribute(OutputPrettinessOption.class)) //
				.isEqualTo(OutputPrettiness.none);

		assertThat(GmSerializationOptions.deriveDefaults().build().findAttribute(AbsentifyMissingPropertiesOption.class)) //
				.isEmpty();
	}

	@Test
	public void testImmutability() {
		GmSerializationOptions defaultOptions = GmSerializationOptions.deriveDefaults().build();
		OutputPrettiness defaultPrettiness = defaultOptions.outputPrettiness();

		changePrettinessOption(defaultOptions, defaultPrettiness, OutputPrettiness.high);
		GmSerializationOptions changedOption2 = changePrettinessOption(defaultOptions, defaultPrettiness, OutputPrettiness.high);
		changePrettinessOption(defaultOptions, defaultPrettiness, OutputPrettiness.mid);

		changePrettinessOption(changedOption2, OutputPrettiness.high, OutputPrettiness.none);

		changePrettinessOption(defaultOptions, defaultPrettiness, OutputPrettiness.high);
	}

	@Test
	public void testNestedDerive() {
		GmDeserializationContextBuilder optionsBuilder = GmDeserializationOptions.deriveDefaults();
		optionsBuilder.setAttribute(StabilizeOrderOption.class, true);
		GmDeserializationOptions defaultOptions = optionsBuilder.build();

		DecodingLenience decodingLenience = new DecodingLenience(true);

		GmDeserializationOptions changedOptions = defaultOptions.derive() //
				.setInferredRootType(SimpleTypes.TYPE_DATE) //
				.build() //
				.derive() //
				.setDecodingLenience(decodingLenience) //
				.build();

		assertThat(SimpleTypes.TYPE_DATE) //
				.isNotEqualTo(defaultOptions.getInferredRootType()) //
				.isEqualTo(changedOptions.getInferredRootType());

		assertThat(defaultOptions.findAttribute(StabilizeOrderOption.class).get()).isTrue();
		assertThat(true).isEqualTo(changedOptions.getAttribute(StabilizeOrderOption.class));

		assertThat(decodingLenience) //
				.isNotSameAs(defaultOptions.getDecodingLenience()) //
				.isSameAs(changedOptions.getDecodingLenience());

	}

	private GmSerializationOptions changePrettinessOption(GmSerializationOptions parentOptions, OutputPrettiness oldPrettiness,
			OutputPrettiness newPrettiness) {
		GmSerializationContextBuilder optionBuilder = parentOptions.derive();

		GmSerializationOptions changedOptions = optionBuilder.setOutputPrettiness(newPrettiness).build();

		// after calling build() the builder itself is now immutable
		assertThatThrownBy(() -> optionBuilder.setOutputPrettiness(oldPrettiness)).isExactlyInstanceOf(IllegalStateException.class);

		// prettiness changed for derived option
		assertThat(changedOptions.outputPrettiness()).isEqualTo(newPrettiness);
		assertThat(changedOptions.outputPrettiness()).isNotEqualTo(oldPrettiness);

		// prettiness stayed the same for parent options
		assertThat(oldPrettiness).isEqualTo(parentOptions.outputPrettiness());

		return changedOptions;
	}
}
