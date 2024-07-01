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
package com.braintribe.codec.marshaller.api;

import java.util.Set;
import java.util.function.Consumer;

import com.braintribe.codec.marshaller.api.options.GmDeserializationContextBuilder;
import com.braintribe.codec.marshaller.api.options.attributes.AbsentifyMissingPropertiesOption;
import com.braintribe.codec.marshaller.api.options.attributes.DecodingLenienceOption;
import com.braintribe.codec.marshaller.api.options.attributes.RequiredTypesRecieverOption;
import com.braintribe.codec.marshaller.api.options.attributes.SessionOption;
import com.braintribe.common.attribute.AttributeContext;
import com.braintribe.model.generic.session.GmSession;

/**
 * Immutable {@link AttributeContext} with convenience methods for deserialization related {@link MarshallerOption}s.
 * Call {@link #derive()} on it to create a new instance starting with identical settings that can be adapted to your
 * needs with the builder-like API.
 * <p>
 * To create a new instance from scratch, use {@link #defaultOptions}.
 */
public interface GmDeserializationOptions extends GmMarshallingOptions {

	/**
	 * General-purpose {@link GmDeserializationOptions}, already preinitialized with common default values. Call
	 * {@link #derive()} on it for a builder-like API to create your custom options.
	 */
	GmDeserializationOptions defaultOptions = DefaultOptionsInitializer.createDefaultDeserializationOptions();

	static GmDeserializationContextBuilder deriveDefaults() {
		return defaultOptions.derive();
	}

	default boolean getAbsentifyMissingProperties() {
		return findAttribute(AbsentifyMissingPropertiesOption.class).orElse(false);
	}

	default DecodingLenience getDecodingLenience() {
		return findAttribute(DecodingLenienceOption.class).orElse(null);
	}

	default Consumer<Set<String>> getRequiredTypesReceiver() {
		return findAttribute(RequiredTypesRecieverOption.class).orElse(null);
	}

	default GmSession getSession() {
		return findAttribute(SessionOption.class).orElse(null);
	}

	@Override
	GmDeserializationContextBuilder derive();

}
