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
package com.braintribe.codec.marshaller.api.options;

import java.util.Set;
import java.util.function.Consumer;

import com.braintribe.codec.marshaller.api.DecodingLenience;
import com.braintribe.codec.marshaller.api.GmDeserializationOptions;
import com.braintribe.codec.marshaller.api.options.attributes.AbsentifyMissingPropertiesOption;
import com.braintribe.codec.marshaller.api.options.attributes.DecodingLenienceOption;
import com.braintribe.codec.marshaller.api.options.attributes.InferredRootTypeOption;
import com.braintribe.codec.marshaller.api.options.attributes.RequiredTypesRecieverOption;
import com.braintribe.codec.marshaller.api.options.attributes.SessionOption;
import com.braintribe.common.attribute.AttributeContextBuilder;
import com.braintribe.common.attribute.TypeSafeAttribute;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.session.GmSession;

/**
 * A builder for {@link GmDeserializationOptions}.
 * 
 * @author Neidhart.Orlich
 */
public interface GmDeserializationContextBuilder extends AttributeContextBuilder {

	default GmDeserializationContextBuilder absentifyMissingProperties(boolean shouldAbsentifyMissingProperties) {
		return set(AbsentifyMissingPropertiesOption.class, shouldAbsentifyMissingProperties);
	}

	default GmDeserializationContextBuilder setDecodingLenience(DecodingLenience decodingLenience) {
		return set(DecodingLenienceOption.class, decodingLenience);
	}

	default GmDeserializationContextBuilder setRequiredTypesReceiver(Consumer<Set<String>> requiredTypesReceiver) {
		return set(RequiredTypesRecieverOption.class, requiredTypesReceiver);
	}

	default GmDeserializationContextBuilder setSession(GmSession session) {
		return set(SessionOption.class, session);
	}

	default GmDeserializationContextBuilder setInferredRootType(GenericModelType inferredRootType) {
		return set(InferredRootTypeOption.class, inferredRootType);
	}

	@Override
	default <A extends TypeSafeAttribute<? super V>, V> GmDeserializationContextBuilder set(Class<A> option, V value) {
		setAttribute(option, value);
		return this;
	}

	/**
	 * Creates a new {@link GmDeserializationOptions} instance like specified before with this builder. After calling this
	 * method, this builder becomes immutable and shouldn't be used any more.
	 */
	@Override
	GmDeserializationOptions build();

}
