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
package com.braintribe.model.processing.query.stringifier.experts;

import java.util.Collection;
import java.util.stream.Collectors;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.value.ValueDescriptor;
import com.braintribe.model.processing.query.api.stringifier.experts.paramter.MultipleParameterProvider;
import com.braintribe.utils.lcd.StringTools;
import com.braintribe.utils.lcd.CommonTools;

public class ValueDescriptorStringifier<V extends ValueDescriptor> extends FunctionStringifier<V> {

	public ValueDescriptorStringifier() {
		super(ValueDescriptorStringifier::functionName, new ValueDescriptorParameterProvider());
	}
	
	
	protected static String functionName(ValueDescriptor valueDescriptor) {
		String typeSignature = valueDescriptor.entityType().getTypeSignature();
		String simpleTypName = CommonTools.getSimpleNameFromFullyQualifiedClassName(typeSignature);
		String functionName = StringTools.uncapitalize(simpleTypName);
		return functionName;
	}
	
	protected static class ValueDescriptorParameterProvider implements MultipleParameterProvider<ValueDescriptor> {
		@Override
		public Collection<?> provideParameters(ValueDescriptor valueDescriptor) {
			
			EntityType<GenericEntity> entityType = valueDescriptor.entityType();
			//@formatter:off
			return entityType
				.getProperties() //This list is already sorted by name!
				.stream()
				.filter(ValueDescriptorStringifier::isVdProperty)
				.map(p -> p.get(valueDescriptor))
				.collect(Collectors.toList());
			//@formatter:on
		}
	}
	
	protected static boolean isVdProperty(Property property) {
		return !(property.isIdentifying() || property.isGlobalId());
	}
}
