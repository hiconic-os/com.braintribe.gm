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

import com.braintribe.codec.marshaller.api.GmSerializationOptions;
import com.braintribe.codec.marshaller.api.OutputPrettiness;
import com.braintribe.codec.marshaller.api.options.attributes.InferredRootTypeOption;
import com.braintribe.codec.marshaller.api.options.attributes.OutputPrettinessOption;
import com.braintribe.codec.marshaller.api.options.attributes.StabilizeOrderOption;
import com.braintribe.codec.marshaller.api.options.attributes.UseDirectPropertyAccessOption;
import com.braintribe.codec.marshaller.api.options.attributes.WriteAbsenceInformationOption;
import com.braintribe.codec.marshaller.api.options.attributes.WriteEmptyPropertiesOption;
import com.braintribe.common.attribute.AttributeContextBuilder;
import com.braintribe.common.attribute.TypeSafeAttribute;
import com.braintribe.model.generic.reflection.GenericModelType;

/**
 * A builder for {@link GmSerializationOptions}.
 * 
 * @author Neidhart.Orlich
 */
public interface GmSerializationContextBuilder extends AttributeContextBuilder {
	default GmSerializationContextBuilder setOutputPrettiness(OutputPrettiness outputPrettiness) {
		return set(OutputPrettinessOption.class, outputPrettiness);
	}

	default GmSerializationContextBuilder outputPrettiness(OutputPrettiness outputPrettiness) {
		return set(OutputPrettinessOption.class, outputPrettiness);
	}

	default GmSerializationContextBuilder useDirectPropertyAccess(boolean useDirectPropertyAccess) {
		return set(UseDirectPropertyAccessOption.class, useDirectPropertyAccess);
	}

	default GmSerializationContextBuilder writeEmptyProperties(boolean writeEmptyProperties) {
		return set(WriteEmptyPropertiesOption.class, writeEmptyProperties);
	}

	default GmSerializationContextBuilder stabilizeOrder(boolean stabilizeOrder) {
		return set(StabilizeOrderOption.class, stabilizeOrder);
	}

	default GmSerializationContextBuilder writeAbsenceInformation(boolean writeAbsenceInformation) {
		return set(WriteAbsenceInformationOption.class, writeAbsenceInformation);
	}

	default GmSerializationContextBuilder inferredRootType(GenericModelType inferredRootType) {
		return set(InferredRootTypeOption.class, inferredRootType);
	}

	default GmSerializationContextBuilder setInferredRootType(GenericModelType inferredRootType) {
		return set(InferredRootTypeOption.class, inferredRootType);
	}

	@Override
	default <A extends TypeSafeAttribute<? super V>, V> GmSerializationContextBuilder set(Class<A> option, V value) {
		AttributeContextBuilder.super.set(option, value);
		return this;
	}

	/**
	 * Creates a new {@link GmSerializationOptions} instance like specified before with this builder. After calling this
	 * method, this builder becomes immutable and shouldn't be used any more.
	 */
	@Override
	GmSerializationOptions build();

}
