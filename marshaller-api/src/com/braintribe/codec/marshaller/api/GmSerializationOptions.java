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
package com.braintribe.codec.marshaller.api;

import com.braintribe.codec.marshaller.api.options.GmSerializationContextBuilder;
import com.braintribe.codec.marshaller.api.options.attributes.OutputPrettinessOption;
import com.braintribe.codec.marshaller.api.options.attributes.StabilizeOrderOption;
import com.braintribe.codec.marshaller.api.options.attributes.UseDirectPropertyAccessOption;
import com.braintribe.codec.marshaller.api.options.attributes.WriteAbsenceInformationOption;
import com.braintribe.codec.marshaller.api.options.attributes.WriteEmptyPropertiesOption;
import com.braintribe.common.attribute.AttributeContext;

/**
 * Immutable {@link AttributeContext} with convenience methods for serialization related {@link MarshallerOption}s. Call
 * {@link #derive()} on it to create a new instance starting with identical settings that can be adapted to your needs
 * with the builder-like API .
 * <p>
 * To create a new instance from scratch, use {@link #defaultOptions}.
 */
public interface GmSerializationOptions extends GmMarshallingOptions {

	/**
	 * General-purpose {@link GmSerializationOptions}, already preinitialized with common default values. Call
	 * {@link #derive()} on it for a builder-like API to create your custom options.
	 */
	GmSerializationOptions defaultOptions = DefaultOptionsInitializer.createDefaultSerializationOptions();

	static GmSerializationContextBuilder deriveDefaults() {
		return defaultOptions.derive();
	}

	default OutputPrettiness outputPrettiness() {
		return getAttribute(OutputPrettinessOption.class);
	}

	default boolean writeEmptyProperties() {
		return getAttribute(WriteEmptyPropertiesOption.class);
	}

	default boolean useDirectPropertyAccess() {
		return getAttribute(UseDirectPropertyAccessOption.class);
	}

	default boolean stabilizeOrder() {
		return getAttribute(StabilizeOrderOption.class);
	}

	default boolean writeAbsenceInformation() {
		return getAttribute(WriteAbsenceInformationOption.class);
	}

	@Override
	GmSerializationContextBuilder derive();
}
