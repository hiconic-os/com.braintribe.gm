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

import com.braintribe.codec.marshaller.api.options.GmDeserializationContext;
import com.braintribe.codec.marshaller.api.options.GmDeserializationContextBuilderImpl;
import com.braintribe.codec.marshaller.api.options.GmSerializationContext;
import com.braintribe.codec.marshaller.api.options.GmSerializationContextBuilder;
import com.braintribe.codec.marshaller.api.options.attributes.DecodingLenienceOption;
import com.braintribe.codec.marshaller.api.options.attributes.InferredRootTypeOption;
import com.braintribe.codec.marshaller.api.options.attributes.OutputPrettinessOption;
import com.braintribe.codec.marshaller.api.options.attributes.RequiredTypesRecieverOption;
import com.braintribe.codec.marshaller.api.options.attributes.SessionOption;
import com.braintribe.codec.marshaller.api.options.attributes.AbsentifyMissingPropertiesOption;
import com.braintribe.codec.marshaller.api.options.attributes.StabilizeOrderOption;
import com.braintribe.codec.marshaller.api.options.attributes.UseDirectPropertyAccessOption;
import com.braintribe.codec.marshaller.api.options.attributes.WriteAbsenceInformationOption;
import com.braintribe.codec.marshaller.api.options.attributes.WriteEmptyPropertiesOption;
import com.braintribe.common.attribute.AttributeContextBuilder;
import com.braintribe.model.generic.reflection.BaseType;

interface DefaultOptionsInitializer {
	// Set even obvious default values to avoid an exception when calling the respective getter
	static void initializeSerializationDefaults(AttributeContextBuilder builder) {
		builder.setAttribute(OutputPrettinessOption.class, OutputPrettiness.none);
		builder.setAttribute(InferredRootTypeOption.class, BaseType.INSTANCE);
		builder.setAttribute(WriteAbsenceInformationOption.class, true);
		
		builder.setAttribute(StabilizeOrderOption.class, false);
		builder.setAttribute(UseDirectPropertyAccessOption.class, false);
		builder.setAttribute(WriteEmptyPropertiesOption.class, false);
	}
	
	// Set even obvious default values to avoid an exception when calling the respective getter
	static void initializeDeserializationDefaults(AttributeContextBuilder builder) {
		builder.setAttribute(DecodingLenienceOption.class, null);
		builder.setAttribute(InferredRootTypeOption.class, null);
		builder.setAttribute(RequiredTypesRecieverOption.class, null);
		builder.setAttribute(SessionOption.class, null);
		builder.setAttribute(AbsentifyMissingPropertiesOption.class, false);
	}

	static GmSerializationOptions createDefaultSerializationOptions() {
		GmSerializationContextBuilder emptyContextBuilder = new GmSerializationContext().derive();
		initializeSerializationDefaults(emptyContextBuilder);
		return emptyContextBuilder.build();
	}
	
	static GmDeserializationOptions createDefaultDeserializationOptions() {
		GmDeserializationContextBuilderImpl emptyContextBuilder = new GmDeserializationContext().derive();
		initializeDeserializationDefaults(emptyContextBuilder);
		return emptyContextBuilder.build();
	}
}
