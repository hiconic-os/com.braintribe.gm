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
package com.braintribe.model.processing.itw.analysis;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.Initializer;
import com.braintribe.model.processing.itw.analysis.JavaTypeAnalysis.JtaClasses;

class ScannedProperty {
	public final Class<? extends GenericEntity> entityClass;
	public final String propertyName;
	public Method setter;
	public Method getter;
	public boolean isCovariantOverride;
	public Type propertyType;

	private final boolean requireEnumBase;

	public ScannedProperty(Class<? extends GenericEntity> entityClass, String propertyName, boolean requireEnumBase) {
		this.entityClass = entityClass;
		this.propertyName = propertyName;
		this.requireEnumBase = requireEnumBase;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public Type getPropertyType() {
		return propertyType;
	}

	public Class<?> getPropertyRawType() {
		return getter.getReturnType();
	}

	private Type getSetterType() {
		return setter != null ? setter.getGenericParameterTypes()[0] : null;
	}

	private Type getGetterType() {
		return getter != null ? getter.getGenericReturnType() : null;
	}

	public String getInitializerString() {
		Initializer gi = getter.getAnnotation(Initializer.class);
		return gi == null ? null : gi.value();
	}

	@Override
	public String toString() {
		return "ScannedProperty[" + fullPropertyName() + "]";
	}

	public void validate(JtaClasses jtaClasses) {
		Type setterType = getSetterType();
		Type getterType = JavaTypeAnalysis.sanitizeType(getGetterType());

		if (setterType == null) {
			try {
				Class<?> rawType = getPropertyRawType();
				entityClass.getMethod(setterName(), rawType);

			} catch (Exception e) {
				// getter has a certain type, but no setter with the same type found.
				// the only way this is valid is if the getter is a co-variant override, which we allow as type specification
				if (checkIfCovariantOverride())
					return;

				throw new JavaTypeAnalysisException("setter not found for property: " + fullPropertyName());
			}

			return;
		}

		// This must be equals cause for parameterized types we can have different instances being equal
		if (!setterType.equals(getterType))
			throw new JavaTypeAnalysisException(
					"setter type (" + setterType + ") does not match getter type (" + getterType + ") for property: " + fullPropertyName());

		propertyType = getterType;

		if (requireEnumBase)
			BeanPropertyScan.validatePossibleEnum(propertyType, jtaClasses, entityClass, propertyName);
	}

	/* If we override a getter on an interface, Java compiler creates a default bridge method on the sub-interface for the
	 * original getter. */
	private boolean checkIfCovariantOverride() {
		String getterName = getter.getName();

		Class<?> overriddenGetterType = getPropertyRawType();

		for (Method m : entityClass.getDeclaredMethods()) {
			if (!m.isBridge())
				continue;

			if (!getterName.equals(m.getName()))
				continue;

			if (m.getParameters().length != 0)
				continue;

			Class<?> getterType = m.getReturnType();
			if (getterType == overriddenGetterType)
				continue;

			if (getterType.isAssignableFrom(overriddenGetterType)) {
				isCovariantOverride = true;
				overriddenGetterType = getterType;
			}
		}

		return isCovariantOverride;
	}

	private String setterName() {
		return "s" + getter.getName().substring(1);
	}

	private String fullPropertyName() {
		return entityClass.getName() + "." + propertyName;
	}

}
