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
package com.braintribe.model.processing.itw.synthesis.java;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.processing.itw.asm.AsmClass;
import com.braintribe.model.processing.itw.asm.AsmExistingClass;
import com.braintribe.model.processing.itw.asm.AsmField;
import com.braintribe.model.processing.itw.asm.AsmType;
import com.braintribe.model.weaving.ProtoGmCollectionType;
import com.braintribe.model.weaving.ProtoGmEntityType;
import com.braintribe.model.weaving.ProtoGmProperty;
import com.braintribe.model.weaving.info.ProtoGmPropertyInfo;

public class PropertyAnalysis implements Iterable<PropertyAnalysis.PropertyDescription> {

	public int numberOfProperties;
	public List<PropertyDescription> propertyDescriptions = new ArrayList<PropertyDescription>();
	public Set<String> propertyNames = new HashSet<String>();

	@Override
	public Iterator<PropertyDescription> iterator() {
		return propertyDescriptions.iterator();
	}

	public static enum SetterGetterAchievement {
		missing,
		declared,
	}

	public boolean isEmpty() {
		return propertyDescriptions.isEmpty();
	}

	public static class PropertyDescription {
		public AsmClass ownerType;
		public AsmType accessPropertyClass; // this may be a primitive (if class exists and it's property is primitive)
		public AsmType actualPropertyClass;
		public ProtoGmPropertyInfo propertyInfo;
		public ProtoGmProperty property;
		public AsmType declaredTypeOverride;
		public List<AsmType> allTypeOverrides;
		public String fieldName;
		public String setterName;
		public String getterName;
		
		public SetterGetterAchievement achievement;

		public AsmField plainPropertyField;
		public AsmField enhancedPropertyField;

		public boolean isCollection() {
			return property.getType() instanceof ProtoGmCollectionType;
		}

		public boolean isIntroducedFor(ProtoGmEntityType gmEntityType) {
			return property.getDeclaringType() == gmEntityType;
		}

		public String getName() {
			return property.getName();
		}

		public String getFieldName() {
			return fieldName;
		}

		public AsmType getPropertyType() {
			return accessPropertyClass;
		}

		public boolean isPrimitive() {
			return accessPropertyClass.isPrimitive();
		}

		public Object defaultValue() {
			Class<?> existingClass = ((AsmExistingClass) accessPropertyClass).getExistingClass();
			return GMF.getTypeReflection().getSimpleType(existingClass).getDefaultValue();
		}
	}

}
