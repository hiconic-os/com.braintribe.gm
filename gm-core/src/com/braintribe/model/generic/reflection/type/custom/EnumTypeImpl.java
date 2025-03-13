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
package com.braintribe.model.generic.reflection.type.custom;

import static com.braintribe.utils.lcd.CollectionTools2.newMap;

import java.util.Map;

import com.braintribe.model.generic.reflection.CloningContext;
import com.braintribe.model.generic.reflection.EnumType;
import com.braintribe.model.generic.reflection.GenericModelException;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.StrategyOnCriterionMatch;
import com.braintribe.model.generic.reflection.TraversingContext;
import com.braintribe.model.generic.reflection.TypeCode;
import com.braintribe.model.generic.tools.GmValueCodec;
import com.braintribe.model.generic.value.EnumReference;

public class EnumTypeImpl<E extends Enum<E>> extends AbstractCustomType implements EnumType<E> {

	@Override
	public Class<E> getJavaType() {
		return (Class<E>) super.getJavaType();
	}

	private Map<String, E> constants;

	public EnumTypeImpl(Class<? extends Enum<?>> javaType) {
		super(javaType);
	}

	@Override
	public TypeCode getTypeCode() {
		return TypeCode.enumType;
	}

	@Override
	public final boolean isEnum() {
		return true;
	}

	@Override
	public boolean isScalar() {
		return true;
	}

	@Override
	public E[] getEnumValues() {
		E enumValues[] = getJavaType().getEnumConstants();
		return enumValues;
	}

	@Override
	public E getEnumValue(String name) {
		return getInstance(name);
	}

	@Override
	public E findEnumValue(String name) {
		if (constants == null) {
			Map<String, E> _constants = newMap();

			for (E enumValue : getEnumValues())
				_constants.put(enumValue.name(), enumValue);

			constants = _constants;
		}

		return constants.get(name);
	}

	@Override
	public <T> T instanceFromString(String encodedValue) throws GenericModelException {
		return (T) getInstance(encodedValue);
	}

	@Override
	public String instanceToString(Object value) throws GenericModelException {
		return ((Enum<?>) value).name();
	}

	@Override
	public String instanceToGmString(Object value) throws GenericModelException {
		return toGmString(value);
	}

	public static String toGmString(Object value) throws GenericModelException {
		return GmValueCodec.enumToGmString((Enum<?>) value);
	}

	@Override
	public Object instanceFromGmString(String encodedValue) {
		String[] parsedEnum = GmValueCodec.parseEnumConstantIdentifier(encodedValue);
		return getInstance(parsedEnum[1]);
	}

	@Override
	@Deprecated
	public E getInstance(String value) {
		return Enum.valueOf(getJavaType(), value);
	}

	@Override
	public Object cloneImpl(CloningContext cloningContext, Object instance, StrategyOnCriterionMatch strategy) throws GenericModelException {
		return instance;
	}

	@Override
	public void traverseImpl(TraversingContext traversingContext, Object instance) {
		// noop
	}

	@Override
	public EnumReference getEnumReference(Enum<?> enumConstant) {
		EnumReference ref = EnumReference.T.createPlain();
		ref.setTypeSignature(getTypeName());
		ref.setConstant(enumConstant.name());

		return ref;
	}

	@Override
	public String getSelectiveInformation(Object instance) {
		return instance != null ? instance.toString() : "";
	}

	@Override
	public boolean isAssignableFrom(GenericModelType type) {
		return type instanceof EnumTypeImpl && type == this;
	}

	@Override
	public boolean isInstance(Object value) {
		return value != null && javaType == value.getClass();
	}

	@Override
	public boolean areCustomInstancesReachable() {
		return true;
	}

	@Override
	public boolean areEntitiesReachable() {
		return false;
	}

	@Override
	public boolean isEmpty(Object value) {
		return value == null;
	}
}
